We’ll go step by step — explaining **commands, configurations, examples, and outputs**.

---

# 🧩 Module 3: Kafka Topics & Partitions (Hands-on)

---

## 🎯 **Objective**

By the end of this module, you’ll be able to:

✅ Start Kafka and ZooKeeper (or KRaft mode)
✅ Create topics (with partitions & replication)
✅ Describe and inspect topics
✅ Understand leaders, followers, ISR
✅ Produce and consume data manually via CLI

---

## ⚙️ Step 1: Start Kafka Environment

Kafka can run in two modes:

* **Traditional mode** → with **ZooKeeper**
* **KRaft mode (Kafka Raft)** → ZooKeeper-less (Kafka ≥ 2.8)

We’ll use the **ZooKeeper setup** first (since it’s still common).

### 🧠 Directory structure

Assuming Kafka is extracted at:

```
/opt/kafka_2.13-3.4.0/
```

---

### 🟢 Start ZooKeeper

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

✅ Output (sample):

```
INFO binding to port 0.0.0.0/2181
INFO Started AdminServer on port 8080
```

---

### 🟢 Start Kafka Broker

Open another terminal:

```bash
bin/kafka-server-start.sh config/server.properties
```

✅ Output:

```
INFO [KafkaServer id=1] started (kafka.server.KafkaServer)
```

Now your **Kafka cluster (1 broker)** is running.
We can expand it to multiple brokers later.

---

## 🧩 Step 2: Create a Kafka Topic

A **topic** is like a “category” or “stream” name that holds messages.

Syntax:

```bash
bin/kafka-topics.sh --create \
--bootstrap-server localhost:9092 \
--replication-factor 1 \
--partitions 3 \
--topic test_topic
```

### Parameters explained:

| Flag                   | Meaning                       |
| :--------------------- | :---------------------------- |
| `--bootstrap-server`   | Connect to Kafka broker       |
| `--replication-factor` | No. of replicas per partition |
| `--partitions`         | No. of partitions             |
| `--topic`              | Topic name                    |

✅ Output:

```
Created topic test_topic.
```

---

## 🧾 Step 3: Describe the Topic

To inspect details (leaders, replicas, ISR):

```bash
bin/kafka-topics.sh --describe \
--bootstrap-server localhost:9092 \
--topic test_topic
```

✅ Example output:

```
Topic: test_topic  PartitionCount: 3  ReplicationFactor: 1  Configs:
    Topic: test_topic  Partition: 0  Leader: 1  Replicas: 1  Isr: 1
    Topic: test_topic  Partition: 1  Leader: 1  Replicas: 1  Isr: 1
    Topic: test_topic  Partition: 2  Leader: 1  Replicas: 1  Isr: 1
```

🧠 Interpretation:

* **Leader:** Broker responsible for reads/writes.
* **Replicas:** All brokers holding copies (only 1 here).
* **ISR (In-Sync Replicas):** Brokers that have caught up with the leader.

---

## 🧩 Step 4: Produce Messages to the Topic

Kafka ships with a built-in CLI **producer** tool.

```bash
bin/kafka-console-producer.sh \
--broker-list localhost:9092 \
--topic test_topic
```

Now type a few messages and press Enter after each:

```
hello kafka
this is a message
apache kafka rocks
```

✅ Output:
(You won’t see any acknowledgment, but messages are written.)

---

## 🧩 Step 5: Consume Messages from the Topic

Use the **console consumer**:

```bash
bin/kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic test_topic \
--from-beginning
```

✅ Output:

```
hello kafka
this is a message
apache kafka rocks
```

🧠 The `--from-beginning` flag makes the consumer read **from offset 0** (reprocessing all messages).

---

## 🧠 Step 6: Understanding Partitions in Action

Each partition stores messages sequentially with **offsets**.

You can check partition assignment manually:

```bash
bin/kafka-topics.sh --describe --topic test_topic --bootstrap-server localhost:9092
```

If you send more messages, Kafka distributes them across partitions (round-robin by default).

💡 You can specify a **key** in the producer to decide which partition the message goes to:

```bash
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test_topic \
--property "parse.key=true" --property "key.separator=:"
```

Then type:

```
key1:Hello
key2:Kafka
key1:Message
```

✅ Messages with the same key (like `key1`) go to the **same partition** → ensures ordering.

---

## ⚙️ Step 7: Creating Topic with Replication (Multi-broker Setup)

Let’s simulate multiple brokers:

### Copy default configs:

```
cp config/server.properties config/server-1.properties
cp config/server.properties config/server-2.properties
```

Edit:

* In `server-1.properties`

  ```properties
  broker.id=1
  listeners=PLAINTEXT://:9093
  log.dirs=/tmp/kafka-logs-1
  ```
* In `server-2.properties`

  ```properties
  broker.id=2
  listeners=PLAINTEXT://:9094
  log.dirs=/tmp/kafka-logs-2
  ```

Start all brokers:

```bash
bin/kafka-server-start.sh config/server-1.properties &
bin/kafka-server-start.sh config/server-2.properties &
```

---

### Create a replicated topic

```bash
bin/kafka-topics.sh --create \
--bootstrap-server localhost:9092 \
--replication-factor 2 \
--partitions 3 \
--topic replicated_topic
```

Describe it:

```bash
bin/kafka-topics.sh --describe \
--bootstrap-server localhost:9092 \
--topic replicated_topic
```

✅ Output:

```
Topic: replicated_topic  PartitionCount: 3  ReplicationFactor: 2
    Partition: 0  Leader: 1  Replicas: 1,2  Isr: 1,2
    Partition: 1  Leader: 2  Replicas: 2,1  Isr: 2,1
    Partition: 2  Leader: 1  Replicas: 1,2  Isr: 1,2
```

🧠 **Notice:**

* Each partition has **a leader and a replica**.
* Kafka auto-balances leaders across brokers.
* If a broker goes down → the follower becomes new leader.

---

## 🧩 Step 8: Simulating Broker Failure (Fault Tolerance Demo)

Stop one broker:

```bash
bin/kafka-server-stop.sh config/server-1.properties
```

Now describe topic again:

```bash
bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic replicated_topic
```

✅ Output (sample):

```
Partition: 0  Leader: 2  Replicas: 1,2  Isr: 2
```

🧠 Meaning:

* Broker 1 went down.
* Broker 2 took over leadership automatically.
* ISR reduced to the live replica (Broker 2).

Kafka maintains availability even during failures.

---

## 🧩 Step 9: Checking Topic List

```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

✅ Output:

```
__consumer_offsets
test_topic
replicated_topic
```

---

## 📊 Step 10: Offset Visualization

To check committed offsets for consumer groups:

```bash
bin/kafka-consumer-groups.sh \
--bootstrap-server localhost:9092 \
--list
```

Output:

```
console-consumer-12345
```

To describe group offsets:

```bash
bin/kafka-consumer-groups.sh \
--bootstrap-server localhost:9092 \
--group console-consumer-12345 \
--describe
```

✅ Output:

```
TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG
test_topic      0          3               3               0
```

🧠 Lag = messages not yet consumed.

---

## 🧭 **Recap of Key Concepts**

| Concept             | Explanation                        |
| :------------------ | :--------------------------------- |
| **Topic**           | Logical stream name for messages   |
| **Partition**       | Unit of parallelism inside a topic |
| **Replication**     | Duplicates of data across brokers  |
| **Leader Broker**   | Handles reads/writes               |
| **Follower Broker** | Syncs from leader for backup       |
| **ISR**             | In-Sync Replicas (healthy copies)  |
| **Offset**          | Message position within partition  |
| **Consumer Lag**    | Unprocessed messages count         |

---

## 🚀 Summary Flow (End-to-End)

```
Producer → sends message to topic partition
Broker (Leader) → stores message → replicates to followers
Follower Brokers → sync via ISR
Consumer → reads from leader → commits offset
ZooKeeper → manages metadata, broker state, leadership
```

Kafka = fast, fault-tolerant, and horizontally scalable message streaming system.

---

## 🧱 What’s Next (Module 4 Preview)

Next, we’ll go deeper into:

* Kafka **Producer Internals**
* `acks`, `retries`, `batch.size`, `linger.ms`
* **Idempotent producers** & **delivery semantics**
* Hands-on: Writing a **Java Producer** to send data to Kafka

---