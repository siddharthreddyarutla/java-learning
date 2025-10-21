Great — these are the exact operational questions you need to understand to run Kafka in production. I’ll answer each point clearly and practically, then finish with concrete recommendations and patterns you can apply immediately.

---

# Big-picture short answers first

* **Yes**, brokers (specifically the *Group Coordinator* broker) coordinate rebalances and partition assignment when consumers join/leave.
* **Each microservice** that “consumes” normally represents a **consumer *group*** (all instances of that service share the same `group.id`). If one microservice instance runs a single consumer thread, that instance will receive *at most* one or more partitions (depending on partition count vs number of instances).
* **Parallelism** in Kafka comes from **partitions**. You get up to *N* parallel consumers per group where *N = number of partitions*.
* If one partition gets many messages (a “hot partition”), it can dominate a consumer’s poll and *appear* to block progress on other partitions unless you design processing to avoid it.
* Heartbeats and poll-time rules are controlled by **`heartbeat.interval.ms`**, **`session.timeout.ms`**, and **`max.poll.interval.ms`** — they govern liveness and when the coordinator will mark a consumer dead and trigger a rebalance.

Now the deep, precise explanation.

---

## 1) Who handles rebalancing, and when does it happen?

* **Group Coordinator** (a broker elected to manage a particular consumer group) handles consumer group membership and **triggers rebalances**.
* Rebalance events happen when:

    * A consumer **joins** the group (new instance starts with same `group.id`).
    * A consumer **leaves** (clean shutdown or detected failure).
    * Topic **partition count changes**.
    * Consumer **session** times out (broker deems it dead).
    * You explicitly call admin operations (e.g., change subscription).
* The broker *doesn’t* push partitions to consumers; it orchestrates assignments and tells each consumer which partitions it should own.

---

## 2) Microservices, groups, partitions — mapping

* **Pattern A (one microservice = one consumer group):**

    * All instances of `service-A` use `group.id=service-A`. Each instance is a consumer in that group. Kafka assigns the partitions across instances.
    * If you have 8 partitions and 4 instances of the service, each instance will get ~2 partitions. If you have 1 instance, it gets all 8 partitions.
* **Pattern B (a microservice with a single consumer thread per instance):**

    * If you have many microservices and each microservice instance runs exactly one consumer thread, each instance will be assigned some subset of partitions. A partition is consumed by **one consumer within a group**. Separate groups (different `group.id`) each receive all messages.
* **Important:** you get **parallelism** by increasing the partition count or increasing consumer instances in the same group. If partitions < consumer instances, some consumers will be idle.

---

## 3) If one partition is hot (lots of messages), will it block others?

Yes it *can*, depending on how your consumer processes polled records.

### Two consumption models:

* **Single-threaded processing**: poll → process records sequentially → commit.

    * If processing one partition’s messages is slow, the same consumer thread will spend time on them and will not call `poll()` until processing finishes, so other partitions’ messages are not processed until next poll — *appears blocked*.
* **Worker-thread model**: poll → dispatch records to a processing thread pool → keep calling `poll()` to maintain heartbeats → commit when processed.

    * This avoids blocking `poll()` (but adds complexity for offset commit correctness).

So with a *single consumer thread*, a hot partition assigned to that consumer will slow processing of other partitions owned by that consumer.

**Options to solve partition skew:**

* Increase number of **partitions** to spread the hot-key workload (but only if keys can be redistributed).
* Create **more consumer instances** (scale the microservice horizontally) so leadership can spread across instances.
* Change **partitioning strategy** or application logic so hot keys are sharded across multiple partitions (if possible).
* Use a dedicated consumer for hot partitions (balance manually).

---

## 4) What happens when you `poll()` — which partitions’ messages do you receive?

* `consumer.poll(timeout)` returns records from **all partitions currently assigned** to that consumer that have data available, up to `max.poll.records` and broker fetch limits.
* If one partition has much more data, you are likely to get many records from that partition in the poll response (subject to `max.poll.records` and fetch sizes). Kafka does not strictly round-robin across partitions in a single poll; it returns what's available and respects fetch limits.

---

## 5) Heartbeat and session timeouts — what they mean and how they interact

There are **three** different but related configuration timeouts you must know:

### `heartbeat.interval.ms`

* How frequently the **consumer client** sends heartbeats to the Group Coordinator to signal that it’s alive.
* Typical default: `3000 ms`.
* It is the *heartbeat frequency*; it must be lower than `session.timeout.ms`.

### `session.timeout.ms`

* The maximum time the **coordinator** waits without hearing a heartbeat before marking the consumer as dead and triggering a rebalance.
* Default/typical: ~`10000 ms` (10s) — but values vary by client/broker default.
* `heartbeat.interval.ms` should be a fraction of `session.timeout.ms` (e.g., heartbeat at 1/3 of session timeout).

### `max.poll.interval.ms` **(often the most confusing but critical)**

* The maximum time allowed **between client `poll()` calls**.
* If your application does heavy processing and does not call `poll()` before `max.poll.interval.ms` elapses, the coordinator considers the consumer **unresponsive** and triggers a rebalance — partition ownership may be moved to other consumers.
* Default: `300000 ms` (5 minutes). If your processing of a batch can take longer than this, increase it or change your processing design.
* Note: a consumer can still send heartbeats (if the client has a heartbeat thread) but if `poll()` isn't called within `max.poll.interval.ms`, the client is considered stuck with respect to partition consumption and the coordinator triggers a rebalance. This is to allow the group to make progress if a consumer is slow in processing.

**Summary of interactions:**

* Heartbeats keep a consumer *registered* (used for liveness). If heartbeats stop for `session.timeout.ms`, the broker marks consumer dead quickly.
* `max.poll.interval.ms` is about *application processing progress*, not just liveness — if you don't call `poll()` frequently enough, the group rebalances even though heartbeats might still be happening (behavior depends on client version), because the consumer is not making progress on its assigned partitions.

---

## 6) Why does consumer get "inactive" or go into millions of lag?

Common causes and their effects:

### Causes of large lag:

* **Slow processing**: Consumers process messages too slowly (heavy CPU, DB calls, third-party calls).
* **Single-threaded consumer**: One thread processes all partitions serially; hot partitions slow everything.
* **GC pauses or OS pauses**: JVM long GC can stop poll thread, stopping heartbeats/polls.
* **Network or broker issues**: Slow fetches, slow replication, overloaded brokers.
* **Insufficient consumers**: Not enough consumer instances for partition count.
* **Partition skew**: A few partitions receive most traffic (hot keys).

### What happens when a consumer becomes “inactive”:

* The **Group Coordinator** marks it as failed (either due to missed heartbeats or exceeded `max.poll.interval.ms`).
* Kafka triggers a **rebalance**: partitions owned by that consumer are reassigned to other consumers.
* While rebalance is happening, **that group’s consumption stops temporarily**. If the failing consumer is still processing messages when its partitions are reassigned, messages may be processed twice (duplicates) or, if the consumer committed offsets prematurely, some messages may be lost.

So “inactive” → rebalance → potential duplicate work or processing gaps depending on commit strategy.

---

## 7) Does one consumer reading from multiple partitions create dependencies?

Yes — if you use a **single-threaded** consumer loop that processes all records synchronously, **one slow partition’s processing will delay the others**.

### Ways to avoid the dependency:

1. **One consumer per thread** (recommended): each consumer instance runs in its own thread/process and gets its own set of partitions. Scale instances for parallelism.
2. **Poll-thread + worker threads**: the poll loop immediately dispatches records to worker threads, continues calling `poll()` frequently to keep membership alive, and handles commits when workers complete. This requires careful offset commit logic and tracking.
3. **Pause/Resume** API**: you can `pause()` partitions that are being processed and `resume()` them later so the poll doesn’t fetch new records for partitions being processed.
4. **Smaller `max.poll.records`**: limit records per poll so processing per poll completes faster.
5. **Increase `max.poll.interval.ms`**: if some processing necessarily takes long, increase this timeout to avoid spurious rebalances (but do this with caution — it delays recovery if the consumer actually died).

---

## 8) Practical configuration and code patterns (recommended)

### Consumer configs (starting point)

```properties
enable.auto.commit=false
max.poll.records=200
max.poll.interval.ms=300000   # raise if processing can take long, default 5m
session.timeout.ms=10000
heartbeat.interval.ms=3000
fetch.max.bytes=52428800      # 50 MB; tune by use-case
```

### Pattern A: Poll-thread + worker pool (safe)

* Single thread does:

    * `poll()` with `max.poll.records` small.
    * For each `ConsumerRecord`, submit work to a thread pool.
    * Maintain a structure mapping partition -> in-flight offsets.
    * Only commit offsets for a partition up to the highest contiguous offset that all earlier records are processed.
* Pros: `poll()` is frequent (no `max.poll.interval.ms` violations), higher throughput.
* Cons: more complex to implement safely (offset tracking + rebalance handling).

### Pattern B: One consumer per thread

* Simpler: run multiple app instances or multiple JVM consumer threads (each with its own `group.id` membership).
* Pros: simple and aligns with Kafka design.
* Cons: requires scaling infrastructure.

### Rebalance listener (must implement)

* `onPartitionsRevoked` → commit offsets of in-flight work for revoked partitions to avoid duplicates.
* `onPartitionsAssigned` → initialize state for assigned partitions.

Example (pseudo):

```java
consumer.subscribe(topics, new ConsumerRebalanceListener() {
  public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
    // flush and commit offsets for partitions being revoked
    commitOffsets(currentOffsets);
  }
  public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
    // reset internal bookkeeping
  }
});
```

### Use `commitSync()` in `onPartitionsRevoked` to make sure offsets are persisted before losing ownership.

---

## 9) If I poll 500 records and don’t finish before `max.poll.interval.ms`, what happens?

* If you do not call `poll()` again before `max.poll.interval.ms` elapses, Kafka considers that consumer **stalled** and will trigger a **rebalance**. Partitions will be moved to other consumers — your in-flight 500 records may be reprocessed by the new owners (duplicate processing) unless you carefully manage transactional processing or idempotency.
* **Heartbeats alone are not enough** to prevent this if `max.poll.interval.ms` is exceeded — the coordinator uses poll progress as a sign of consumer making progress, not only heartbeats.

---

## 10) Concrete examples of fixes for large lag or inactive consumers

### Short-term fixes:

* Increase `max.poll.interval.ms` if you know processing may be slow.
* Reduce `max.poll.records` so per-poll processing finishes faster.
* Increase the number of consumer instances (scale out).
* Reduce heavy blocking work inside consumer thread (e.g., move DB writes to async).

### Long-term/design fixes:

* Make processing idempotent (so duplicates are safe).
* Use **exactly-once** features (transactions or idempotent producers + Kafka Streams) where appropriate.
* Partition hot keys differently (hash + salt) or shard data to avoid hot partitions.
* Use a separate **queue** for long-running tasks (e.g., create a job in DB/queue and process asynchronously) so consumers remain responsive.
* Monitor GC and tune JVM: long GC pauses are a common root cause of missed polls.

---

## 11) Quick FAQ (concise)

* **Does the broker reassign partitions?**
  Yes — the coordinator broker triggers assignment; new leaders for partitions are elected among replicas when needed.

* **If I reset offsets, will messages be reprocessed?**
  Yes — consumers that start with the earlier offsets will re-read messages until retention expires.

* **If one consumer instance fails, do other consumers pick up its partitions?**
  Yes — after coordinator detects failure (session timeout or poll timeout), it rebalances and reassigns partitions.

* **Is it bad to increase `session.timeout.ms`?**
  Increasing `session.timeout.ms` delays detection of truly-dead consumers; use it carefully. Prefer to ensure `poll()` happens regularly.

---

## 12) Recommended default starting config + rules-of-thumb

* `enable.auto.commit=false` (manual commit gives control)
* `max.poll.records=100..500` (tune per message processing time)
* `max.poll.interval.ms = 300000` (5m) — increase only if needed
* `heartbeat.interval.ms = session.timeout.ms / 3`
* `session.timeout.ms = 10000` (10s) — faster failure detection in many environments
* Use `ConsumerRebalanceListener` and commit on `onPartitionsRevoked`
* Prefer worker threads + frequent `poll()` over long-blocking processing

---

## 13) Final practical patterns you should implement now

1. **Implement `onPartitionsRevoked` to persist/commit offsets.**
2. **Use `max.poll.records` small enough so processing per poll finishes well before `max.poll.interval.ms`.**
3. **If you need long-running processing per record, use poll-thread + worker threads, track per-partition offsets, and only commit safe offsets.**
4. **Monitor consumer lag, GC pauses, and rebalance frequency.** High rebalance frequency = trouble.
5. **Design for idempotency** on message handlers (so reprocessing is safe).



### 🔎 Quick: Group Coordinator vs Partition Leader

Partition Leader: for each partition, one broker is elected leader. The leader handles all reads and writes for that partition and replicates to followers. Leaders are per-partition and can be different brokers for different partitions.


Group Coordinator: for each consumer group Kafka elects one broker to act as the Group Coordinator for that group. The Group Coordinator handles consumer group membership, heartbeats, partition assignment (rebalance), and storing consumer offsets (coordination tasks).


→ The Group Coordinator is not necessarily the partition leader. It’s just a broker chosen to coordinate the group; it may or may not be leader for any particular partition. Leader role and group-coordinator role are independent.
So: partition leader = responsible for data I/O of that partition; group coordinator = responsible for membership & assignment for a consumer group.