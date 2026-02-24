# 🧩 Module 2: Kafka Architecture Overview

---

## 🏗️ Kafka High-Level Architecture

Kafka is a **distributed, partitioned, and replicated commit log** service.
It consists of **producers**, **brokers**, **consumers**, and **ZooKeeper/KRaft** for coordination.

---

### 🖼️ Architecture Diagram (Conceptual)

```
                   +-----------------------------+
                   |        ZooKeeper / KRaft    |
                   | (Cluster metadata, leader   |
                   |   election, coordination)   |
                   +--------------+--------------+
                                  |
         +------------------------+------------------------+
         |                                                 |
+--------v--------+     +--------v--------+     +--------v--------+
|     Broker 1     |     |     Broker 2     |     |     Broker 3     |
| (Topic partitions|     | (Topic partitions|     | (Topic partitions|
|   and logs)      |     |   and logs)      |     |   and logs)      |
+--------+--------+     +--------+--------+     +--------+--------+
         ^                     ^                     ^
         |                     |                     |
   +-----+-----+         +-----+-----+         +-----+-----+
   |  Producer |         |  Consumer |         |  Consumer |
   |  (writes) |         |  (reads)  |         |  (reads)  |
   +-----------+         +-----------+         +-----------+
```

---

## 🧩 Core Components Explained

### 1. **Broker**

* A **Kafka server** that stores messages in **topics**.
* Each broker is identified by a unique **broker ID**.
* Brokers can host **multiple topics and partitions**.
* In production, a Kafka cluster typically has **3 or more brokers** for fault tolerance.

🧠 **Key point:**
Each broker can handle **read and write requests** from producers and consumers and also perform replication of data across brokers.

---

### 2. **Cluster**

* A **collection of brokers** working together.
* Provides **redundancy and load balancing**.
* Cluster metadata (like topic list, partitions, leaders, etc.) is managed by **ZooKeeper** or **KRaft (Kafka Raft)**.

---

### 3. **Topic**

* A **logical channel** where data is published.
* Example: `db_LEAVE_MANAGEMENT`, `user_activity`, `payment_events`.
* Each topic has one or more **partitions** for scalability.

🧠 Think of topics as “folders” where Kafka organizes messages, and partitions as “files” inside those folders that actually store data.

---

### 4. **Partition**

* A **sequence of ordered, immutable messages**.
* Each message inside a partition has an **offset** (unique ID).
* Kafka guarantees **ordering only within a partition**, not across partitions.

A partition in a Kafka topic is an ordered, immutable sequence of records (messages) that acts as a physical segment of the topic, allowing data to be distributed across multiple brokers for high throughput and horizontal scaling. Partitions guarantee message ordering within them, but not across the entire topic. 
Key aspects of partitions:
* Parallelism and Scalability: Topics can have multiple partitions, allowing multiple producers to write and multiple consumers to read simultaneously.
* Ordering: Messages within a single partition are ordered and assigned a unique ID called an offset.
* Distribution: Partitions are distributed across different brokers in a Kafka cluster to balance load.
* Retention and Immutability: Once written, data in a partition cannot be changed and is retained for a configured period.
* Replication: Each partition can be replicated across brokers for fault tolerance


#### Example:

```
Partition 0: [Msg0, Msg1, Msg2, Msg3]
Partition 1: [Msg0, Msg1, Msg2]
```

🧠 **Tip:**
When a topic has multiple partitions, Kafka can **distribute load** among consumers for parallel processing.

---

### 5. **Offset**

* A **unique incremental ID** assigned to each message in a partition.
* Consumers use this offset to **keep track of which messages have been read**.
* Offsets are stored in an internal Kafka topic:
  👉 `__consumer_offsets`

---

### 6. **Producer**

* A **client application** that publishes messages to Kafka topics.
* Responsible for choosing which partition to send data to.
* Can assign partition manually or let Kafka decide using a **partitioner** (usually hash-based).

🧩 Producer can specify:

* `acks` level (0, 1, or all)
* `retries`
* `linger.ms` (batching)
* `idempotence` (for avoiding duplicates)

---

### 7. **Consumer**

* A **client application** that subscribes to one or more topics.
* Reads data from partitions.
* Kafka ensures **each message is delivered to one consumer per consumer group**.

---

### 8. **Consumer Group**

* A set of consumers that share the load of reading data from partitions.
* Each partition is consumed by **only one consumer within a group**.

#### Example:

| Partition | Consumer |
| :-------- | :------- |
| 0         | C1       |
| 1         | C2       |
| 2         | C3       |

🧠 When a consumer joins or leaves a group → **Rebalancing** occurs (we’ll cover this in a dedicated module).

---

### 9. **ZooKeeper / KRaft**

* **ZooKeeper**: older coordination system for Kafka clusters.

    * Handles broker registration, leader election, topic configuration, etc.
* **KRaft (Kafka Raft)**: new built-in consensus protocol that eliminates the need for ZooKeeper.

    * More efficient and native to Kafka.

🧠 Kafka versions 2.8+ support **KRaft mode** (ZooKeeper-less).

---

## ⚙️ How Data Flows in Kafka

Let’s go through the **lifecycle of a message** step by step 👇

```
Producer  --->  Broker (Leader Partition)  --->  Replica Brokers  --->  Consumer
```

### Step 1: Producer sends a record

Producer sends a message to a **topic**, and the broker decides the correct **partition** based on key/hash.

### Step 2: Broker stores data

The **leader broker** for that partition appends the record to its **log file** (on disk).

### Step 3: Replication

Other brokers maintain **replicas** of this partition.
They constantly sync with the leader to stay up to date.

### Step 4: Consumer fetches data

Consumer reads from the leader broker and tracks its **offset**.
Offsets are committed to Kafka after processing (manually or automatically).

---

## 🧾 Storage Internals (How Kafka Stores Data)

Each partition is stored as a **commit log file** on disk:

```
/kafka-logs/
  └── topicA-0/
      ├── 00000000000000000000.log
      ├── 00000000000000001000.log
      ├── leader-epoch-checkpoint
      └── index files
```

🧠 Kafka doesn’t delete messages immediately after consumption.
Retention is **time-based** or **size-based** (default 7 days).

---

## ⚡ Kafka’s Secret Sauce: Sequential Disk Writes

Kafka’s performance comes from:

* Writing messages **sequentially to disk** (no random writes)
* Using **page cache** and **zero-copy transfer**
* **Batching** multiple messages together
* **Asynchronous** I/O

That’s why Kafka easily handles **millions of events per second** with low latency.

---

## 🧩 Protocol Communication

All components communicate via a **binary TCP protocol**:

* **Producers → Brokers** (send data)
* **Consumers → Brokers** (fetch data)
* **Brokers ↔ Brokers** (replication)
* **ZooKeeper / KRaft ↔ Brokers** (metadata & coordination)

Kafka protocol supports **compression**, **authentication (SASL)**, and **encryption (SSL)**.

---

## 🧠 Summary

| Component             | Role                                |
| :-------------------- | :---------------------------------- |
| **Broker**            | Stores and serves messages          |
| **Topic**             | Logical category for messages       |
| **Partition**         | Unit of parallelism & scalability   |
| **Producer**          | Publishes data                      |
| **Consumer**          | Reads data                          |
| **Consumer Group**    | Parallel consumers for load sharing |
| **ZooKeeper / KRaft** | Cluster coordination                |
| **Offset**            | Record’s position in a partition    |

---