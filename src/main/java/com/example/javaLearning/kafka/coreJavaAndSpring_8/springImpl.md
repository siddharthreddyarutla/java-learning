# 🧩 **Module 9: Kafka with Spring Boot**

---

## 🎯 **Goal**

By the end of this module, you’ll:

✅ Understand how Spring Kafka works internally
✅ Configure producer & consumer settings in `application.yml`
✅ Create **Kafka producers** using `KafkaTemplate`
✅ Consume messages using `@KafkaListener`
✅ Implement **error handling, retries, and dead-letter topics**
✅ Learn **transactional Kafka messaging** for exactly-once semantics

---

## 🧠 **Why Spring Kafka?**

Spring Kafka provides:

* Easier configuration and dependency management
* Auto-wiring of producers, consumers, and factories
* Declarative message listeners (`@KafkaListener`)
* Built-in support for:

    * Manual and auto commits
    * Batch listeners
    * Retries and error handlers
    * Transactions

It abstracts the **low-level Kafka client API** but still gives full control.

---

## 🧩 **1️⃣ Add Maven Dependencies**

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>3.1.0</version> <!-- or compatible with your Spring Boot version -->
</dependency>
```

Spring Boot’s Kafka autoconfiguration automatically provides:

* `KafkaTemplate`
* `ConcurrentKafkaListenerContainerFactory`
* Consumer and producer factories

---

## ⚙️ **2️⃣ Producer Configuration (application.yml)**

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
        linger.ms: 5
        batch.size: 16384
```

🧠 Explanation:

* `acks: all` → Wait for ISR replicas for durability
* `enable.idempotence: true` → Avoid duplicates
* `linger.ms` and `batch.size` → Increase throughput

---

## 🧩 **3️⃣ Consumer Configuration (application.yml)**

```yaml
spring:
  kafka:
    consumer:
      group-id: order-consumer-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      properties:
        max.poll.records: 200
        session.timeout.ms: 10000
        heartbeat.interval.ms: 3000
        max.poll.interval.ms: 300000
    listener:
      ack-mode: manual
      concurrency: 3   # 3 parallel consumers in same group
```

🧠 Explanation:

* `ack-mode: manual` → app explicitly acknowledges after processing
* `concurrency` → how many concurrent consumer threads Spring creates for this listener

---

## 🧱 **4️⃣ Create a Kafka Producer**

### Producer Service

```java
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(String orderId, String message) {
        kafkaTemplate.send("orders", orderId, message)
            .thenAccept(result ->
                System.out.printf("✅ Sent: topic=%s partition=%d offset=%d key=%s%n",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    orderId)
            );
    }
}
```

🧠 `KafkaTemplate` is thread-safe and handles producer pooling, retries, and batching internally.

---

### Example Controller (to test via REST)

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping("/{id}")
    public String sendOrder(@PathVariable String id, @RequestBody String body) {
        orderProducer.sendOrder(id, body);
        return "Order sent successfully!";
    }
}
```

Now you can POST an order to:

```
POST /orders/123
Body: "Order placed successfully"
```

✅ The message goes to the `orders` topic.

---

## 🧩 **5️⃣ Create a Kafka Consumer**

### Consumer Listener

```java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    @KafkaListener(topics = "orders", groupId = "order-consumer-group")
    public void listen(String message, Acknowledgment ack) {
        try {
            System.out.println("📩 Received: " + message);
            processOrder(message);
            ack.acknowledge(); // ✅ Manual offset commit
        } catch (Exception e) {
            System.err.println("❌ Error processing: " + message);
            throw e; // Let error handler handle it
        }
    }

    private void processOrder(String message) throws Exception {
        // Simulate business logic
        if (message.contains("fail")) throw new RuntimeException("Processing failed");
        Thread.sleep(100);
        System.out.println("✅ Processed order: " + message);
    }
}
```

🧠 Spring Kafka injects an `Acknowledgment` object to allow **manual offset commits**.

---

## ⚠️ **6️⃣ Error Handling & Retries**

Spring Kafka automatically supports **error handling and retries** via:

* `DefaultErrorHandler` (newer versions)
* `SeekToCurrentErrorHandler` (older versions)

Example configuration:

```java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Configuration;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // Retry 3 times with 2 seconds gap before sending to DLT
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new FixedBackOff(2000L, 3L)
        ));

        return factory;
    }
}
```

✅ This automatically retries the listener method on failure 3 times before forwarding to **Dead Letter Topic** (DLT).

---

## 💀 **7️⃣ Dead Letter Topic (DLT)**

When retries fail, messages can be sent to a **DLT** for later inspection or manual replay.

Spring automatically appends `.DLT` to topic name (if enabled).

### Enable DLT:

```yaml
spring:
  kafka:
    listener:
      default-error-handler:
        dead-letter-topic-suffix: .DLT
```

### DLT Consumer

```java
@KafkaListener(topics = "orders.DLT", groupId = "dlt-handler")
public void handleDeadLetter(String failedMessage) {
    System.err.println("⚰️ Dead-letter message: " + failedMessage);
}
```

🧠 DLT ensures you don’t lose failed events — they can be reprocessed later manually.

---

## 💰 **8️⃣ Kafka Transactions (Exactly-Once)**

For financial or stateful operations, Kafka supports **transactions** — combining multiple sends and commits atomically.

### Producer configuration:

```yaml
spring:
  kafka:
    producer:
      transactional-id-prefix: tx-  # enables transactional producer
      properties:
        enable.idempotence: true
```

### Transactional producer:

```java
import org.springframework.kafka.annotation.KafkaTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransactionalOrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransactionalOrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional("kafkaTransactionManager")
    public void sendTransactionalOrder(String key, String msg) {
        kafkaTemplate.send("orders", key, msg);
        kafkaTemplate.send("order-audit", key, "Audit: " + msg);
        System.out.println("✅ Transactional send complete");
    }
}
```

🧠 Both messages are committed together — or rolled back together — ensuring **exactly-once semantics** across topics.

---

## ⚙️ **9️⃣ Parallelism and Scaling**

| Setting                | Description                                |
| :--------------------- | :----------------------------------------- |
| `concurrency` (Spring) | Number of threads per listener             |
| `partitions` (topic)   | Max parallelism possible                   |
| `group.id`             | Unique per microservice                    |
| Multiple instances     | Each instance = 1+ consumers in same group |

🧠 Rule:

> *Number of active consumers in a group ≤ number of partitions.*

---

## 🧾 **10️⃣ Monitoring**

Spring exposes Kafka metrics via Micrometer / Actuator.

Enable:

```yaml
management:
  metrics:
    enable:
      kafka: true
  endpoints:
    web:
      exposure:
        include: health, metrics
```

Then check:

```
/actuator/metrics/kafka.consumer.records-consumed-total
/actuator/metrics/kafka.producer.records-sent-total
```

---

## 🧠 **11️⃣ Common Interview-Level Q&A**

| Question                                         | Answer                                               |
| :----------------------------------------------- | :--------------------------------------------------- |
| What is `KafkaTemplate`?                         | High-level abstraction over producer API             |
| Can you manually commit offsets in Spring Kafka? | Yes — via `Acknowledgment.acknowledge()`             |
| What’s the difference between `ack-mode` values? | `record`, `batch`, `manual`, `manual_immediate`      |
| How to ensure no message loss?                   | Disable auto-commit, commit after processing         |
| How to handle poison messages?                   | Use `DefaultErrorHandler` + DLT                      |
| How to guarantee exactly-once?                   | Use transactional producer and idempotent config     |
| What is `concurrency` in listeners?              | No. of consumer threads within the same app instance |

---

## 🧭 **Summary**

| Concept            | Description                              |
| :----------------- | :--------------------------------------- |
| **Spring Kafka**   | Framework simplifying Kafka client usage |
| **KafkaTemplate**  | Simplified producer                      |
| **@KafkaListener** | Simplified consumer                      |
| **AckMode**        | Controls when offsets are committed      |
| **Error Handler**  | Manages retries and DLT                  |
| **Transactions**   | Ensure exactly-once semantics            |
| **Concurrency**    | Controls parallelism                     |
| **DLT**            | Stores failed messages                   |
