# 🧩 Module 5: Kafka Consumers & Consumer Groups

---

## 🎯 **Goal**

By the end of this module, you’ll understand:

✅ What a consumer and consumer group are
✅ How Kafka handles offsets and rebalancing
✅ Auto vs manual offset commits
✅ How consumers read data (poll mechanism)
✅ What happens when a new consumer joins or leaves a group
✅ Real **Java + Spring Boot consumer examples**

---

## 🧠 **What is a Kafka Consumer?**

A **Consumer** is a client that **reads data from Kafka topics**.

Kafka consumers are **pull-based**:

> They poll data from brokers, instead of brokers pushing data to them.

---

## 🧩 **Consumer Group Overview**

A **Consumer Group** is a set of consumers that work together to consume a topic.

Each **partition** of a topic is consumed by **only one consumer in a group** — ensuring *no duplication within the group*.

📦 Example:

| Partition | Consumer   |
| :-------- | :--------- |
| orders-0  | consumer-1 |
| orders-1  | consumer-2 |
| orders-2  | consumer-3 |

🧠 But if you start **two separate consumer groups**, both will receive *all messages* independently — perfect for fan-out use cases.

---

## ⚙️ **How Kafka Consumers Work**

### Step 1️⃣: Join a Group

Each consumer joins a **group.id** (like a team name).
The broker (via **Group Coordinator**) assigns partitions to them.

### Step 2️⃣: Poll Messages

Consumers continuously call:

```java
consumer.poll(Duration.ofMillis(100))
```

to fetch messages.

### Step 3️⃣: Process Messages

The app processes each record received in the poll.

### Step 4️⃣: Commit Offsets

After processing, the consumer **commits offsets** to track progress.

---

## 🧩 **Offset Management**

The **offset** is the position of a message in a partition.

Example:

```
Partition-0:
Offset | Message
----------------
0      | User login
1      | User logout
2      | User update
```

Consumers store **the last processed offset** (like a bookmark).

### Where are offsets stored?

* By default, in Kafka’s internal topic:
  `__consumer_offsets`

### Why it matters:

If a consumer crashes, Kafka restarts it at the **last committed offset**.

---

## ⚖️ **Auto vs Manual Offset Commit**

### 1️⃣ Automatic Commit (default)

Kafka commits offsets **periodically** at a fixed interval.

```properties
enable.auto.commit=true
auto.commit.interval.ms=5000
```

🧠 Issue:
If your app crashes between receiving and processing a record, offset is already committed → you lose that message.

---

### 2️⃣ Manual Commit (recommended)

You explicitly commit after successful processing.

```java
consumer.commitSync();
```

🧠 Advantage:
You ensure “**at least once**” semantics — no data loss.

---

## 🧠 **Delivery Semantics**

| Guarantee         | Description                                                    | Trade-off     |
| :---------------- | :------------------------------------------------------------- | :------------ |
| **At most once**  | Messages may be lost, never duplicated                         | Fastest       |
| **At least once** | No message loss, possible duplicates                           | Safer         |
| **Exactly once**  | No loss, no duplicates (Kafka Streams or Idempotent Producers) | Most reliable |

🧠 In most production systems, **at least once** is used.

---

## 🧩 **Rebalancing**

When:

* A new consumer joins
* A consumer leaves
* A partition count changes

Kafka **rebalances** partitions across consumers.

🧠 Example:

| Before                 | After                               |
| :--------------------- | :---------------------------------- |
| C1 → [0,1], C2 → [2,3] | (Add C3) → C1:[0], C2:[1,2], C3:[3] |

During rebalancing:

* Consumption stops briefly.
* Offsets are re-assigned.

This is managed by **Group Coordinator** broker.

---

## ⚙️ **Key Consumer Configurations**

| Config                  | Description                      | Common Value           |
| :---------------------- | :------------------------------- | :--------------------- |
| `group.id`              | Unique consumer group name       | `order-consumer-group` |
| `enable.auto.commit`    | Auto offset commit               | `false`                |
| `auto.offset.reset`     | Start point if no offset found   | `latest` / `earliest`  |
| `max.poll.records`      | Max records per poll             | `500`                  |
| `session.timeout.ms`    | Consumer heartbeat timeout       | `10000`                |
| `heartbeat.interval.ms` | Heartbeat frequency              | `3000`                 |
| `fetch.min.bytes`       | Minimum bytes broker should send | `1`                    |
| `fetch.max.wait.ms`     | Wait time for min bytes          | `500`                  |

---

## 🧾 **Offset Reset Behavior**

If Kafka doesn’t find a committed offset for a new consumer:

* `auto.offset.reset=latest` → starts from newest messages
* `auto.offset.reset=earliest` → reads from beginning

---

## 💻 **Hands-on: Java Kafka Consumer**

Let’s write a simple consumer to read from our `test_topic`.

### Maven Dependency

Same as producer:

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.4.0</version>
</dependency>
```

### ☕ Code Example

```java
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SimpleKafkaConsumer {

    public static void main(String[] args) {
        // 1. Properties
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 2. Create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("test_topic"));

        // 3. Poll messages
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Consumed record: topic=%s partition=%d offset=%d key=%s value=%s%n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
                // 4. Commit offset manually
                consumer.commitSync();
            }
        } finally {
            consumer.close();
        }
    }
}
```

✅ Output:

```
Consumed record: topic=test_topic partition=0 offset=0 key=key-1 value=value-1
Consumed record: topic=test_topic partition=1 offset=0 key=key-2 value=value-2
```

---

## 🧩 **Spring Boot Consumer Example**

### `application.yml`

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: order-consumer-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

### Consumer Service

```java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @KafkaListener(topics = "test_topic", groupId = "order-consumer-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
```

✅ Output (console):

```
Received message: Hello Kafka
Received message: Apache Kafka Rocks
```

Spring Kafka automatically handles polling and offset commits by default — but you can switch to manual acknowledgment if needed.

---

## 🔄 **Consumer Lag**

Lag = how many messages are not yet processed by the consumer.

Check via CLI:

```bash
bin/kafka-consumer-groups.sh \
--bootstrap-server localhost:9092 \
--group demo-group \
--describe
```

✅ Output:

```
TOPIC       PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG
test_topic  0          10              12              2
```

🧠 Meaning:
Consumer is **2 messages behind**.

You can use tools like **Kafka UI**, **Conduktor**, or **Burrow** to monitor lag visually.

---

## ⚠️ **Common Consumer Issues**

| Issue             | Cause                                    | Fix                                    |
| :---------------- | :--------------------------------------- | :------------------------------------- |
| Rebalancing loops | Long processing > `max.poll.interval.ms` | Increase interval or use async workers |
| Lag increasing    | Slow consumers                           | Scale consumers or optimize processing |
| Duplicate reads   | Committing before processing             | Use manual commit                      |
| Message loss      | Auto-commit enabled                      | Disable auto-commit                    |

---

## 🧠 **Rebalancing Example (Visual)**

Initial state (2 consumers, 4 partitions):

```
C1 → [0,1]
C2 → [2,3]
```

Add one more consumer:

```
C1 → [0]
C2 → [1,2]
C3 → [3]
```

Remove one:

```
C1 → [0,1]
C2 → [2,3]
```

💡 Kafka ensures every partition always has exactly one active consumer in the group.

---

## 🧭 **Summary Table**

| Concept               | Description                     |
| :-------------------- | :------------------------------ |
| **Consumer**          | Reads messages from topic       |
| **Group**             | Collection of consumers         |
| **Offset**            | Bookmark of last read record    |
| **Commit**            | Save offset after processing    |
| **Rebalancing**       | Redistribution of partitions    |
| **Lag**               | Messages pending to be consumed |
| **Auto Offset Reset** | Start from earliest/latest      |
| **Manual Commit**     | Prevents data loss              |
| **Coordinator**       | Broker managing group state     |

---

## 🚀 **End-to-End Flow Recap**

```
Producer → Broker(Leader) → Consumer(Group)
           ↳ writes message   ↳ reads partition
                            ↳ commits offset
                            ↳ rebalancing ensures load balance
```

---