# 🧭 Module 6: Kafka Broker Internals (Deep Dive)

Now the broker internals — how Kafka stores data, replicates it, and provides fault tolerance, throughput, and durability. This will help you reason about tuning, failures, and why Kafka behaves the way it does.

---

## 1. Kafka log storage model — commit log & segments

* Each **partition** is implemented as an **append-only commit log** on disk.
* On disk the partition is a directory with multiple **log segment files** and index files:

  ```
  /kafka-logs/<topic>-<partition>/
      00000000000000000000.log       (messages)
      00000000000000000000.index     (offset->file position)
      00000000000000000000.timeindex (optional)
      leader-epoch-checkpoint
  ```
* Kafka writes to the **active segment**; when it reaches a configured size or time limit it **rolls** to a new segment.
* Segments are the unit for deletion/retention (old segments are deleted when retention time/size exceeded).

**Why segments?**

* Avoid huge single-file growth
* Efficient deletion by removing whole files (cheap)
* Easier index lookup and recovery

---

## 2. Leader / Follower replication protocol

* Each partition has one **leader** and zero or more **followers** (replicas).
* Producers write to the **leader**. Followers fetch data from the leader via the **replica fetcher** thread.
* Followers maintain a local copy of the leader’s log; they periodically fetch batches of records (pull-based replication).
* **In-Sync Replicas (ISR)**: set of replicas that are sufficiently caught up with the leader.
* The **leader** maintains a **high watermark (HW)** — the offset up to which all ISR replicas have replicated. Consumers read up to the HW to ensure durability.

**Replication steps simplified**

1. Leader appends record to its log.
2. Leader responds to producer based on `acks`:

    * `acks=all`: leader waits until all replicas in ISR have acknowledged.
    * `acks=1`: leader responds as soon as appended locally.
3. Followers fetch appended data, write locally, and send fetch responses.
4. Leader advances HW when replicas in ISR acknowledge replication.

---

## 3. High Watermark (HW) and visibility

* **HW** is the last offset known to be replicated to all ISR replicas (durable offset).
* Consumers read only up to HW (ensuring they only see records that won't be lost if leader fails).
* When a follower becomes leader, it must have up-to-date data (depending on leader election policy).

---

## 4. Replica states and ISR

* Replicas are in either **ISR** (in-sync) or not.
* If a follower falls behind (exceeds `replica.lag.time.ms` or `replica.lag.max.messages` thresholds), it is removed from the ISR.
* Being outside ISR means that replica may not be eligible for leader election (unless unclean election is enabled).

---

## 5. Leader election & unclean leader election

* Leader election occurs when the current leader fails.
* **Preferred leader election** chooses the replica with the preferred order (usually the first in the replicas list).
* **Unclean leader election** (`unclean.leader.election.enable=true`) allows a non-ISR follower to become leader (may cause data loss). Usually disabled in production to avoid data loss.
* With ZooKeeper: leader election and metadata updates coordinated via ZK. With KRaft: Kafka uses its built-in quorum/raft for metadata and election.

---

## 6. Replica fetcher & follower catching-up

* Followers run **ReplicaFetcherThreads** which pull data from the leader in batches (efficient network usage).
* Followers write sequentially to disk — same high throughput characteristics as leader writes.
* Tuning fetch size and fetch frequencies impacts replication lag.

---

## 7. Log retention and compaction

* Two ways to retain data:

    * **Time-based / Size-based retention**: `log.retention.hours`, `log.retention.bytes`
    * **Log compaction**: `cleanup.policy=compact` retains the latest record per key (useful for changelogs / compacted topics)
* Compaction runs asynchronously and produces guarantees about key-based retention rather than time.

---

## 8. Flush/sync model & durability

* Kafka uses OS page cache; writes are appended to the file and flushed asynchronously.
* Configs controlling flush:

    * `log.flush.interval.messages` / `log.flush.interval.ms` (rarely used; default relies on OS)
    * `replica.fetch.max.bytes`, `replica.fetch.response.max.bytes` impact replication
* For strict durability, `acks=all` + proper `min.insync.replicas` ensures multiple copies before ack.

---

## 9. Throughput & performance features

* **Sequential disk writes**: writing is append-only so disk IO is sequential (fast).
* **Zero-copy sendfile**: OS-level optimization to reduce CPU and copies during network transfers.
* **Batching**: both producer batching and replica fetcher batching reduce overhead.
* **Compression**: network and disk savings.
* **Index files**: allow efficient offset -> position lookup without scanning.

---

## 10. Broker metadata & ZooKeeper vs KRaft

* **ZooKeeper (old mode)**:

    * Manages cluster metadata, broker lists, controller election, topic config.
    * Kafka broker talks to ZooKeeper for cluster state changes.
* **KRaft (Kafka Raft, newer)**:

    * Kafka’s own Raft-based quorum for metadata management; removes ZooKeeper.
    * Simplifies operations and reduces moving parts.

---

## 11. Important broker configs to know (practical)

* `log.dirs` — where logs are stored (multiple dirs for balancing).
* `num.io.threads`, `num.network.threads`, `num.replica.fetchers` — thread pools for IO/network/replication.
* `replication.factor` — topic-level default replication factor (set when creating topic).
* `min.insync.replicas` — number of replicas that must acknowledge for `acks=all` to succeed.
* `unclean.leader.election.enable` — controls data-loss-prone leader election.
* `log.segment.bytes`, `log.retention.hours`, `log.cleanup.policy` — control storage and retention.
* `socket.send.buffer.bytes` / `socket.receive.buffer.bytes` — network tuning.
* `message.max.bytes` — max message size.

---

## 12. Monitoring & metrics to watch

* **Under-replicated partitions** — replicas that are not ISR (bad).
* **Offline partitions** — partitions without a leader.
* **Leader imbalance** — skew in number of leaders across brokers.
* **Replication lag** — how far followers are behind leader.
* **Consumer lag** — per consumer group (not broker metric, but operationally important).
* **Broker disk utilization / file descriptor usage / GC pauses** — JVM metrics.
* Useful JMX metrics: `kafka.server:type=ReplicaManager`, `kafka.server:type=Log`, `kafka.network:type=RequestHandlerPool`.

---

## 13. Useful CLI commands (practical ops)

* List topics:

  ```bash
  bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
  ```
* Describe topic (leaders, replicas, ISR):

  ```bash
  bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic my_topic
  ```
* Describe consumer group (offsets, lag):

  ```bash
  bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-group --describe
  ```
* Check controller & broker state (in logs or via JMX / admin api). For KRaft, use admin APIs / logs for Raft status.

---

## 14. Operational patterns & recommendations

* **Replication factor >= 3** for production (tolerates one broker failure and still maintain ISR).
* **min.insync.replicas** set to 2 with replication.factor=3 for safe `acks=all` writes.
* **Disable unclean leader election** unless you accept possible data loss for availability.
* Spread `log.dirs` across disks and use RAID/fast disks for high throughput.
* Monitor and alert on under-replicated partitions and leader imbalance.
* Pre-create topics with appropriate partition counts (changing partitions later requires careful planning because partitions and key ordering).
* Use KRaft for new deployments (simpler ops); when migrating from ZooKeeper, plan carefully.

---

## 15. How broker internals explain behaviors you see

* **Hot partition causing consumer slowness**: a single partition's sequential backlog maps to one consumer thread — if that partition's leader broker is busy or the consumer is slow, replication lag and consumer lag grow.
* **Why rebalances happen when consumers are slow**: `max.poll.interval.ms` and session/heartbeat logic protect group progress; if consumers aren't polling, coordinator shifts partitions.
* **Why consumers only read committed data**: HW ensures only replicated offsets are visible, preventing data loss when leader changes.

---

## Quick recap

* Kafka stores data as append-only logs per partition, split into segments.
* Replication (leader/follower + ISR + HW) provides durability and availability.
* Broker-level settings and replication tuning directly affect throughput, durability, and failover behavior.
* Monitoring, sensible defaults (RF=3, min.insync=2), and avoiding unclean election are key for production safety.

---

If you want, I can now:

* Provide **example broker tuning values** for a specific workload (e.g., low-latency vs high-throughput), or
* Produce **a walkthrough** simulating a leader failure and showing log output + CLI commands and how ISR/leader election behaves, or
* Continue to **Module 7: Rebalancing & Fault Tolerance** with deep examples and code showing partition reassignment and preferred leader election.

Which of those would you like next?
