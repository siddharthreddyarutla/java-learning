
# 🧩 Module 4: Kafka Producers (Concept + Java Hands-On)

---

## 🎯 **Goal**

By the end of this module, you’ll understand:

✅ How a Kafka Producer works internally
✅ Producer configuration parameters (`acks`, `retries`, `linger.ms`, etc.)
✅ Message delivery guarantees (at least once, at most once, exactly once)
✅ Idempotence and batching
✅ How to build a real **Java Producer** using Kafka API and with **Spring Boot**

---

## 🧠 **What is a Kafka Producer?**

A **Producer** is any application or client that **publishes (writes)** records to a **Kafka topic**.

Producers decide:

* Which **topic** to send data to
* Which **partition** within that topic
* How messages are **batched, retried, or acknowledged**

---

## 🧩 **Producer Workflow Overview**

```
App → Serializer → Partition Selector → Broker (Leader) → Log
```

Step-by-step:

1️⃣ **Producer app** sends a record (key + value).
2️⃣ The **serializer** converts data to byte format (JSON, String, Avro, etc.).
3️⃣ **Partitioner** decides which partition to send the message to.
4️⃣ The **Kafka client** batches and sends requests asynchronously to the **broker leader**.
5️⃣ The **broker acknowledges** when data is written successfully.

---

## 🧩 **Producer Record Structure**

Each record (message) has:

```java
ProducerRecord<String, String> record =
    new ProducerRecord<>("topic-name", "key", "value");
```

| Field         | Description                                |
| :------------ | :----------------------------------------- |
| **Topic**     | Target topic name                          |
| **Key**       | Optional; determines partition if provided |
| **Value**     | Actual message payload                     |
| **Partition** | Optional; can override default partitioner |

---

## ⚙️ **Producer Configuration Parameters (Very Important)**

Let’s break down key parameters every dev must understand:

| Config                                  | Description                                    | Typical Value      |
| :-------------------------------------- | :--------------------------------------------- | :----------------- |
| `bootstrap.servers`                     | List of brokers to connect to                  | `localhost:9092`   |
| `acks`                                  | Acknowledgment mode (explained below)          | `all`              |
| `retries`                               | Retry attempts if send fails                   | `3`                |
| `linger.ms`                             | Delay before sending batch (to allow grouping) | `5-10` ms          |
| `batch.size`                            | Max batch size (bytes)                         | `16384` (16 KB)    |
| `key.serializer`                        | Serializer for message key                     | `StringSerializer` |
| `value.serializer`                      | Serializer for message value                   | `StringSerializer` |
| `enable.idempotence`                    | Ensures exactly-once delivery                  | `true`             |
| `compression.type`                      | Compress messages (gzip, snappy, lz4, zstd)    | `lz4`              |
| `max.in.flight.requests.per.connection` | Controls request ordering                      | `1–5`              |

---

## ⚖️ **Acknowledgment Levels (`acks`)**

This parameter determines **when** the producer considers a message “successfully sent”.

| `acks` | Description                         | Reliability | Latency           |
| :----- | :---------------------------------- | :---------- | :---------------- |
| `0`    | Producer doesn’t wait for ack       | Fastest     | Risk of data loss |
| `1`    | Waits for leader broker’s ack only  | Moderate    | Some risk         |
| `all`  | Waits for all replicas (ISR) to ack | Safest      | Slightly slower   |

🧠 **Best Practice:**
Use `acks=all` for **reliability** and `acks=1` for **high-speed pipelines** where minor loss is acceptable.

---

## 🔁 **Retries and Idempotence**

When you enable:

```properties
retries=3
enable.idempotence=true
```

Kafka ensures:

* Retries won’t cause **duplicate messages**
* Message delivery is **exactly once**

🧠 Without idempotence:

> Retries may resend the same record multiple times.

With idempotence:

> Kafka assigns a **unique Producer ID (PID)** and **sequence number** to every message, ensuring duplicates are rejected.

---

## 🧩 **Batching and Linger**

Kafka producers batch multiple messages to improve throughput.

| Config       | Role                                |
| :----------- | :---------------------------------- |
| `batch.size` | Max batch (in bytes) before sending |
| `linger.ms`  | Wait time to allow batch to fill up |

🧠 Example:

* If you send messages quickly → Kafka batches them together.
* If slow → Kafka waits up to `linger.ms` ms before sending.

This **reduces network calls** and improves **throughput** drastically.

---

## 🧩 **Compression**

To optimize network and disk usage:

```properties
compression.type=lz4
```

Kafka supports:

* `none` (default)
* `gzip`
* `snappy`
* `lz4`
* `zstd` (best compression ratio)

🧠 Always use compression for **high-throughput** systems.

---

## ⚙️ **Producer Delivery Guarantees**

| Delivery Type     | Description                              | Config                                |
| :---------------- | :--------------------------------------- | :------------------------------------ |
| **At most once**  | Message may be lost but never duplicated | `acks=0`                              |
| **At least once** | Message never lost, might duplicate      | `acks=1`                              |
| **Exactly once**  | No loss, no duplicates                   | `acks=all`, `enable.idempotence=true` |

🧠 Best Practice:

* For financial or critical data → **exactly-once**.
* For logs or metrics → **at least-once**.

---

## 🧱 **Flow Visualization**

```
Producer
   |
   |--> Batch messages in memory
   |
   |--> Send to Broker (Leader partition)
   |
   |--> Wait for ACK (based on config)
   |
   |--> Commit success (update metrics)
```

If broker fails → producer retries → ISR replica leader takes over → message delivery continues seamlessly.

---

## 💻 **Hands-on: Java Kafka Producer**

Let’s write a simple Java Producer using the Kafka client library.

### 🧩 Maven dependency:

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.4.0</version>
</dependency>
```

---

### ☕ Java Code Example

```java
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class SimpleKafkaProducer {

    public static void main(String[] args) {
        // 1. KafkaProducerConfig properties
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "5");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        // 2. Create Kafka producer
        Producer<String, String> producer = new KafkaProducer<>(props);

        // 3. Send message (async)
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>("test_topic", "key-" + i, "value-" + i);

            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("Sent message: " +
                            "Topic=" + metadata.topic() +
                            ", Partition=" + metadata.partition() +
                            ", Offset=" + metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            });
        }

        // 4. Flush and close
        producer.flush();
        producer.close();
    }
}
```

✅ Output example:

```
Sent message: Topic=test_topic, Partition=1, Offset=0
Sent message: Topic=test_topic, Partition=2, Offset=0
Sent message: Topic=test_topic, Partition=0, Offset=0
...
```

Kafka auto-load balances partitions across available brokers.

---

## 🧩 **Spring Boot Kafka Producer Example**

### Add dependency:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      acks: all
      retries: 3
      properties:
        enable.idempotence: true
        compression.type: lz4
```

### Producer service:

```java
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public MessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message)
            .thenAccept(result ->
                System.out.println("Sent message to partition: " +
                        result.getRecordMetadata().partition()));
    }
}
```

---

## 🧠 **When You Should Tune Producer Config**

| Scenario                        | Config Focus                                               |
| :------------------------------ | :--------------------------------------------------------- |
| Low latency (real-time)         | `acks=1`, small `linger.ms`, small `batch.size`            |
| High throughput (batch systems) | Increase `linger.ms`, `batch.size`, use `compression.type` |
| Reliability critical            | `acks=all`, `enable.idempotence=true`, high `retries`      |

---

## 🧭 **Summary**

| Concept                | Key Idea                       |
| :--------------------- | :----------------------------- |
| **Producer**           | Sends messages to Kafka topics |
| **acks**               | Controls durability            |
| **linger.ms**          | Wait before batching           |
| **batch.size**         | Messages per request           |
| **idempotence**        | Prevent duplicates             |
| **compression**        | Save bandwidth                 |
| **delivery semantics** | Control loss/duplication       |
| **partitioner**        | Distributes load               |
| **Spring Kafka**       | Simplifies producer management |

---
