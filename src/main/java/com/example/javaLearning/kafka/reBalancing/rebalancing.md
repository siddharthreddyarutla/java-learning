# 🧩 **Module 7: Kafka Rebalancing & Fault Tolerance**

---

## 🎯 **Goal**

By the end of this module, you’ll understand:

✅ What a *rebalance* is and what triggers it
✅ How partition reassignments work internally
✅ What happens when consumers, producers, or brokers fail
✅ How Kafka achieves *fault tolerance* (zero data loss, continued availability)
✅ How to tune and stabilize rebalancing in production

---

## 🧠 **What is Rebalancing in Kafka?**

A **rebalance** is the process of **redistributing partitions among consumers in a consumer group** when membership or assignment changes.

Kafka uses rebalancing to ensure:

* Each partition is consumed by **exactly one** consumer in a group.
* Workload is evenly distributed.
* The group can recover from consumer/broker failures.

---

## ⚙️ **When Does a Rebalance Happen?**

Kafka triggers rebalancing in these situations:

| Trigger                                                          | Explanation                                             |
| :--------------------------------------------------------------- | :------------------------------------------------------ |
| ✅ **Consumer joins the group**                                   | New instance starts → more parallelism available        |
| ✅ **Consumer leaves the group**                                  | Instance stopped/crashed → partitions need reassignment |
| ✅ **Broker hosting partition leader goes down**                  | Leader election → partition reassignment                |
| ✅ **Topic partitions increase**                                  | Kafka must redistribute them among consumers            |
| ✅ **Consumer fails heartbeat or exceeds `max.poll.interval.ms`** | Kafka assumes it’s dead and removes it from the group   |

During a rebalance, **consumption halts temporarily** — all consumers pause fetching until new assignments are completed.

---

## 🧩 **How Rebalancing Works Internally**

Let’s break it down step by step 👇

### Step 1️⃣ — Group Coordinator (broker role)

Each consumer group is managed by one broker known as the **Group Coordinator**.
It keeps track of:

* Consumers in the group
* Their subscriptions
* Partition ownership
* Committed offsets

### Step 2️⃣ — Join Group Phase

When consumers start (or restart), they send a **JoinGroup** request to the coordinator.
The coordinator designates one consumer as the **Group Leader**.

### Step 3️⃣ — Assignment Phase

The **Group Leader** collects topic/partition metadata and decides which partitions go to which consumers.

Assignment strategies (default):

* `RangeAssignor` (default) → sequential partition ranges
* `RoundRobinAssignor`
* `StickyAssignor` (recommended for stability)
* `CooperativeStickyAssignor` (graceful incremental rebalances)

### Step 4️⃣ — Sync Group Phase

The coordinator sends each consumer its partition assignments → consumers start polling messages again.

🧠 **Rebalance duration = downtime** for that group. During this time, no consumer processes data.

---

## ⚖️ **Rebalance Example**

Let’s say:

* Topic: `orders`
* Partitions: 6
* Group: `order-processing-group`

### Initial setup:

| Consumer | Assigned Partitions |
| :------- | :------------------ |
| C1       | 0, 1                |
| C2       | 2, 3                |
| C3       | 4, 5                |

### If C2 crashes:

→ Coordinator detects no heartbeat → triggers rebalance
New assignment:

| Consumer | Assigned Partitions |
| :------- | :------------------ |
| C1       | 0, 1, 2             |
| C3       | 3, 4, 5             |

When C2 rejoins, another rebalance happens.

---

## ⚙️ **Sticky and Cooperative Rebalancing**

Kafka versions ≥ **2.4** introduced improved rebalancing:

* **Sticky Assignor**: tries to *minimize partition movement* (less disruption).
* **CooperativeStickyAssignor**: performs *incremental rebalances*, meaning not all consumers have to stop at once.

✅ Use this assignor for stable production systems.

### Example config:

```properties
partition.assignment.strategy=org.apache.kafka.clients.consumer.CooperativeStickyAssignor
```

---

## 🧩 **What Happens During a Rebalance**

| Step | Event                                    | Impact                           |
| :--- | :--------------------------------------- | :------------------------------- |
| 1    | Consumers stop polling                   | Temporary halt in processing     |
| 2    | Coordinator collects memberships         | Consumers can’t process messages |
| 3    | Group leader calculates new assignments  | CPU work, metadata sync          |
| 4    | Coordinator distributes assignments      | Consumers resume polling         |
| 5    | Offsets for revoked partitions committed | To avoid duplicates              |

🧠 Tip: Implement `ConsumerRebalanceListener` to **commit offsets** before partitions are revoked, preventing duplicates.

---

## 🔁 **How to Reduce or Prevent Rebalances**

Excessive rebalancing kills performance and throughput. Here’s how to control it 👇

| Problem                          | Fix                                                   |
| :------------------------------- | :---------------------------------------------------- |
| Frequent consumer joins/leaves   | Stabilize instances (graceful shutdowns)              |
| Slow consumers                   | Increase `max.poll.interval.ms`                       |
| Heartbeat timeouts               | Adjust `session.timeout.ms` / `heartbeat.interval.ms` |
| Too many partitions per consumer | Add consumers (scale horizontally)                    |
| Inefficient assignor             | Use `CooperativeStickyAssignor`                       |
| Long rebalances                  | Use incremental rebalances (Kafka ≥ 2.4)              |

---

## 🧱 **Fault Tolerance in Kafka**

Kafka’s **fault tolerance** is achieved through **replication**, **leader election**, and **client recovery** mechanisms.

Let’s go through each one.

---

### 1️⃣ **Replication-based Tolerance**

Every partition has **N replicas** (default 3).
If the leader fails → one follower in the ISR becomes leader automatically.

✅ **Guarantee:**
As long as ≥1 ISR replica is alive, Kafka continues operating without data loss.

---

### 2️⃣ **min.insync.replicas + acks=all**

| Config                | Role                                                      |
| :-------------------- | :-------------------------------------------------------- |
| `min.insync.replicas` | Minimum replicas required for successful writes           |
| `acks=all`            | Producer waits for all ISR replicas to ack before success |

Example:

```properties
min.insync.replicas=2
acks=all
```

If fewer than 2 replicas are alive → producer gets an error (`NOT_ENOUGH_REPLICAS`).

🧠 This prevents data loss even if 1 broker crashes.

---

### 3️⃣ **Leader Election and Recovery**

* Kafka automatically elects a **new leader** from the ISR set when the current leader fails.
* Election handled by:

    * ZooKeeper in older clusters
    * KRaft quorum in newer ones
* Follower takes over, continues from last replicated offset (High Watermark).

🧠 Because consumers read only up to the HW, data remains consistent.

---

### 4️⃣ **Producer Retries + Idempotence**

When a broker or network fails mid-send:

* Producers retry (`retries>0`)
* With `enable.idempotence=true`, duplicates are filtered using producer ID and sequence number.

Guarantee = **exactly-once delivery** (from producer to broker).

---

### 5️⃣ **Consumer Recovery**

When consumers restart after crash:

* They resume from **last committed offset**
* Kafka rebalances automatically to redistribute partitions
* No message loss (if commits are handled properly)

---

## 🧪 **Simulating Fault Tolerance (Hands-on)**

Try this in your local setup:

### Step 1: Start 3 brokers

```
bin/kafka-server-start.sh config/server-0.properties &
bin/kafka-server-start.sh config/server-1.properties &
bin/kafka-server-start.sh config/server-2.properties &
```

### Step 2: Create a replicated topic

```
bin/kafka-topics.sh --create \
--bootstrap-server localhost:9092 \
--replication-factor 3 \
--partitions 4 \
--topic replicated_topic
```

### Step 3: Describe topic

```
bin/kafka-topics.sh --describe --topic replicated_topic --bootstrap-server localhost:9092
```

✅ Output:

```
Partition: 0  Leader: 1  Replicas: 1,2,3  Isr: 1,2,3
```

### Step 4: Stop leader broker

```
bin/kafka-server-stop.sh config/server-1.properties
```

✅ Output (after re-election):

```
Partition: 0  Leader: 2  Replicas: 1,2,3  Isr: 2,3
```

🎉 Consumer and producer continue without manual intervention → **fault-tolerant cluster**.

---

## ⚡ **Tuning for Fault Tolerance vs Throughput**

| Goal                                   | Recommended Config                                                                                  |
| :------------------------------------- | :-------------------------------------------------------------------------------------------------- |
| **Maximum durability**                 | `replication.factor=3`, `min.insync.replicas=2`, `acks=all`, `unclean.leader.election.enable=false` |
| **High throughput (accept some risk)** | `replication.factor=2`, `acks=1`, `compression=lz4`, increase `batch.size` & `linger.ms`            |
| **Low-latency streaming**              | Small batches, `acks=1`, fine-tune `fetch.min.bytes`                                                |

---

## 🧠 **Rebalancing vs Failover — Key Difference**

| Concept       | Trigger                          | Who Handles It    | Impact                                   |
| :------------ | :------------------------------- | :---------------- | :--------------------------------------- |
| **Rebalance** | Consumer joins/leaves            | Group Coordinator | Temporary pause in consumption           |
| **Failover**  | Broker or partition leader fails | Controller broker | Leader re-election, short producer delay |

They’re related but separate:

* **Rebalance** = consumer side event (redistribute partitions).
* **Failover** = broker side event (change leader/follower roles).

---

## 🧭 **Summary**

| Concept                            | Explanation                                     |
| :--------------------------------- | :---------------------------------------------- |
| **Rebalance**                      | Redistribution of partitions among consumers    |
| **Group Coordinator**              | Broker managing group membership and offsets    |
| **Sticky Assignor**                | Minimizes partition movement                    |
| **Cooperative Assignor**           | Enables incremental rebalancing                 |
| **Fault Tolerance**                | Achieved via replication and leader election    |
| **Failover**                       | Automatic leader change when broker fails       |
| **min.insync.replicas + acks=all** | Prevents data loss                              |
| **ISR (In-Sync Replicas)**         | Replicas fully caught up with leader            |
| **High Watermark (HW)**            | Only messages up to HW are visible to consumers |

---

## 🧱 **Real-world Tips**

✅ Use `CooperativeStickyAssignor` for stable rebalances
✅ Set `max.poll.interval.ms` high enough for processing-heavy consumers
✅ Commit offsets on partition revoke (`ConsumerRebalanceListener`)
✅ Keep replication factor ≥3 in production
✅ Monitor `UnderReplicatedPartitions` metric — it’s your early warning sign
✅ Avoid frequent consumer restarts — they cause rebalances and throughput drops
