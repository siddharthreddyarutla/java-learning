# 🧩 **Module 8: Kafka in Java (Core API)**

---

## 🎯 **Goal**

By the end of this module, you’ll:

✅ Create Kafka **Producer** and **Consumer** in pure Java (without Spring)
✅ Understand **custom partitioning** and **key-based message routing**
✅ Use **manual offset commit** correctly
✅ Handle **graceful shutdowns**
✅ See a complete **end-to-end producer–consumer pipeline**

---

## 🧠 **Overview**

The **Kafka Java Client API** (`org.apache.kafka.clients`) is the foundation of every Kafka integration — including Spring Kafka, Kafka Streams, and Connect.

It provides two main classes:

* `KafkaProducer<K, V>`
* `KafkaConsumer<K, V>`

You configure these clients using `Properties` and then connect them to your Kafka brokers (via `bootstrap.servers`).

---

## 🧩 **Section 1: Producer Example**

We’ll write a producer that:

* Sends messages to a topic (`orders`)
* Uses keys to decide partitions (customer IDs)
* Prints record metadata (partition and offset)
* Handles retries and acknowledgments safely

---

### ✅ **1. Producer Setup**

```java
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class SimpleProducer {

    public static void main(String[] args) {

        // 1️⃣ Producer Configuration
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        // 2️⃣ Create Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 3️⃣ Send Messages (Asynchronous)
        for (int i = 0; i < 10; i++) {
            String key = "customer-" + (i % 3);
            String value = "Order placed #" + i;

            ProducerRecord<String, String> record = new ProducerRecord<>("orders", key, value);

            // Callback to log metadata or exceptions
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.printf("✅ Sent to topic=%s partition=%d offset=%d key=%s%n",
                            metadata.topic(), metadata.partition(), metadata.offset(), key);
                } else {
                    exception.printStackTrace();
                }
            });
        }

        // 4️⃣ Close Producer (flush ensures all records sent)
        producer.flush();
        producer.close();
    }
}
```

---

### ⚙️ **How Partitioning Works**

By default:

* Kafka hashes the **record key** using **murmur2** algorithm.
* That hash determines the partition:

  ```
  partition = hash(key) % number_of_partitions
  ```
* All messages with the same key go to the **same partition**, preserving order per key.

So in our example:

```
customer-0 → partition 0
customer-1 → partition 1
customer-2 → partition 2
```

🧠 Ordering is guaranteed **only within a partition**, not across partitions.

---

### 🧩 **2. Custom Partitioner Example**

Sometimes you want control — say, send VIP customers to a dedicated partition.

You can create a **custom partitioner** by implementing `org.apache.kafka.clients.producer.Partitioner`.

```java
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class VipCustomerPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object keyObj, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        int partitions = cluster.partitionCountForTopic(topic);
        String key = (String) keyObj;

        if (key != null && key.startsWith("vip")) {
            return partitions - 1; // last partition reserved for VIPs
        }
        return Math.abs(key.hashCode()) % (partitions - 1);
    }

    @Override public void close() {}
    @Override public void configure(Map<String, ?> configs) {}
}
```

**Register in Producer properties:**

```java
props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, VipCustomerPartitioner.class.getName());
```

Now all VIP messages go to the last partition.

---

## 🧩 **Section 2: Consumer Example**

We’ll now create a consumer that:

* Belongs to a group (`order-consumers`)
* Reads messages from the `orders` topic
* Uses **manual offset commit** after successful processing
* Handles **graceful shutdown** and **rebalancing**

---

### ✅ **1. Consumer Setup**

```java
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

public class SimpleConsumer {

    private static volatile boolean keepConsuming = true;

    public static void main(String[] args) {

        // 1️⃣ Consumer configuration
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-consumers");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // manual commit
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // start from beginning if no offset

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("orders"), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println("⚠️ Partitions revoked: " + partitions);
                consumer.commitSync(); // Commit processed offsets before losing ownership
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println("✅ Partitions assigned: " + partitions);
            }
        });

        // 2️⃣ Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Shutting down...");
            keepConsuming = false;
        }));

        // 3️⃣ Poll loop
        try {
            while (keepConsuming) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("📩 Received | topic=%s partition=%d offset=%d key=%s value=%s%n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                    processRecord(record);
                }

                // Commit offsets only after processing all messages
                consumer.commitSync();
            }
        } finally {
            consumer.close();
        }
    }

    private static void processRecord(ConsumerRecord<String, String> record) {
        // Mock business logic (DB writes, API calls)
        try {
            Thread.sleep(100); // simulate processing delay
        } catch (InterruptedException ignored) {}
    }
}
```

---

### 🧠 **Consumer Behavior**

* `poll()` fetches records from *assigned partitions only*.
* `commitSync()` commits the last processed offset to Kafka’s internal topic (`__consumer_offsets`).
* If you restart the consumer, it resumes from the last committed offset → **no data loss**.

---

## ⚙️ **Manual Commit Flow**

When `enable.auto.commit=false`:
1️⃣ Consumer polls messages
2️⃣ You process them (store, compute, etc.)
3️⃣ You call `commitSync()` manually

If the consumer crashes before commit → messages are reprocessed (safe).
If you commit before processing → messages can be lost (unsafe).
Hence: **process first → commit later**.

---

## 🧩 **Section 3: Graceful Shutdown**

Graceful shutdown ensures:

* All polled records are processed
* Offsets are committed before closing

Handled by:

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    keepConsuming = false;
    consumer.wakeup();  // interrupt the poll() safely
}));
```

This allows the consumer to exit the `poll()` loop cleanly and commit offsets before closing.

---

## 🧱 **Section 4: End-to-End Flow Summary**

```
PRODUCER → (Broker Leader) → REPLICATION → CONSUMER
   |                           |               |
   |                           |               |--> poll() + process() + commit()
   |                           |
   |--> send(key, value)       |--> replicate across brokers (ISR)
```

### 💡 Key Concepts at Play

| Layer                 | Behavior                                 |
| :-------------------- | :--------------------------------------- |
| **Producer**          | Asynchronous, batched, key-based routing |
| **Broker**            | Stores messages, replicates partitions   |
| **Consumer**          | Pull-based reading, offset tracking      |
| **Group Coordinator** | Handles rebalancing and offsets          |
| **Fault Tolerance**   | Replication + retries + commits          |

---

## ⚙️ **Tuning for Real Systems**

| Goal            | Config                                                        |
| :-------------- | :------------------------------------------------------------ |
| High throughput | Increase `batch.size`, `linger.ms`, enable `compression.type` |
| Low latency     | Smaller batches, lower `linger.ms`                            |
| No duplicates   | `enable.idempotence=true`, `acks=all`, manual offset commit   |
| Smooth scaling  | Use `CooperativeStickyAssignor`                               |

---

## 🧠 **Quick Interview Nuggets**

| Question                              | Answer                                 |
| :------------------------------------ | :------------------------------------- |
| What ensures ordering?                | Key → Partition mapping                |
| Where are offsets stored?             | Internal topic `__consumer_offsets`    |
| Can one consumer read all partitions? | Yes, if group has one consumer         |
| How to avoid data loss?               | Process → commit offset manually       |
| What is `acks=all`?                   | Wait for all ISR replicas before ack   |
| Why `enable.idempotence=true`?        | Prevent duplicate writes after retries |

---

## 🧭 **Summary**

| Component             | Role                                |
| :-------------------- | :---------------------------------- |
| **Producer**          | Writes messages to topic partitions |
| **Partitioner**       | Decides partition per message       |
| **Consumer**          | Reads and commits messages          |
| **Group Coordinator** | Handles partition assignment        |
| **Manual Commit**     | Ensures reliable processing         |
| **Graceful Shutdown** | Prevents offset loss                |
| **Java Client API**   | Low-level Kafka interface           |

---

## ⚡ Next Module (Preview)

Next, we’ll step up from “basic Java” to **Spring Boot integration**:
📘 **Module 9: Kafka with Spring Boot**

You’ll learn:

* How `KafkaTemplate` and `@KafkaListener` work
* How to configure consumer and producer properties in YAML
* Error handling, retries, dead-letter topics
* Transactional Kafka messaging in Spring

---