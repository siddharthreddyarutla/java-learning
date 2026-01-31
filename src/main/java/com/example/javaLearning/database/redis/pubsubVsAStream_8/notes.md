# 🧱 STEP 8: Redis Pub/Sub vs Streams

Core interview question:

> ❓ *What messaging features does Redis provide, and when should I use each?*

Redis offers **two different messaging models**:

1. **Pub/Sub**
2. **Streams**

They solve **different problems**.

---

## 8.1 Redis Pub/Sub (Fire & Forget)

![Image](https://miro.medium.com/1%2Aa0pgncA21O79-o29MWMKIw.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1200/1%2AjiMBLv0wpPxZdfZ32YKSvQ.jpeg)

### What Pub/Sub is

* Publisher sends messages
* Subscribers receive messages **only if online**
* No storage
* No replay

Example:

```bash
SUBSCRIBE notifications
PUBLISH notifications "order created"
```

---

### How Pub/Sub works (simple flow)

1. Subscriber subscribes to a channel
2. Publisher publishes a message
3. Redis pushes message to **currently connected subscribers**
4. Message is gone forever

👉 **No persistence**

---

### Pub/Sub Characteristics

| Feature           | Pub/Sub         |
| ----------------- | --------------- |
| Persistence       | ❌ No            |
| Message replay    | ❌ No            |
| Ordering          | ✔ Yes           |
| Consumer groups   | ❌ No            |
| Offline consumers | ❌ Miss messages |

---

### When to use Pub/Sub

✔ Real-time notifications
✔ Chat messages
✔ Live updates (stock ticker, dashboard)
✔ Cache invalidation signals

👉 Interview one-liner:

> Redis Pub/Sub is suitable for real-time, transient messaging where message loss is acceptable.

---

## 8.2 Redis Streams (Persistent Messaging)

![Image](https://miro.medium.com/0%2AIT2Xajn2Fo-TjYn9.jpg)

![Image](https://devopedia.org/images/article/229/1804.1571239690.png)

### What Streams are

* Log-based data structure
* Messages are **stored**
* Consumers can read later
* Kafka-like behavior

Example:

```bash
XADD orders * orderId 123 status created
XREAD STREAMS orders 0
```

---

### Stream Internals (high level)

* Each message has:

    * ID (timestamp-sequence)
    * Key-value fields
* Stored in Redis memory (can be persisted)

---

### Consumer Groups (🔥 VERY IMPORTANT)

Consumer groups allow:

* Multiple consumers
* Load balancing
* Acknowledgements

Example:

```bash
XGROUP CREATE orders group1 0
XREADGROUP GROUP group1 c1 STREAMS orders >
XACK orders group1 <id>
```

---

### Streams Characteristics

| Feature           | Streams     |
| ----------------- | ----------- |
| Persistence       | ✅ Yes       |
| Message replay    | ✅ Yes       |
| Ordering          | ✔ Yes       |
| Consumer groups   | ✅ Yes       |
| Offline consumers | ✅ Supported |

---

## 8.3 Pub/Sub vs Streams (INTERVIEW TABLE)

| Feature         | Pub/Sub       | Streams          |
| --------------- | ------------- | ---------------- |
| Persistence     | ❌             | ✅                |
| Replay messages | ❌             | ✅                |
| Consumer groups | ❌             | ✅                |
| Message loss    | Possible      | Prevented        |
| Use case        | Notifications | Event processing |

👉 **Golden interview line**:

> Redis Pub/Sub is ephemeral, while Redis Streams provide durable, replayable messaging with consumer groups.

---

## 8.4 Redis Streams vs Kafka (Name-drop smartly)

| Aspect      | Redis Streams      | Kafka                 |
| ----------- | ------------------ | --------------------- |
| Setup       | Simple             | Complex               |
| Throughput  | Medium             | Very high             |
| Persistence | In-memory + disk   | Disk-first            |
| Use case    | Lightweight queues | Large-scale pipelines |

👉 Interview-safe answer:

> Redis Streams are a lightweight alternative to Kafka for simpler event-driven systems.

---

## 8.5 Common Interview Trap Questions

❓ If subscriber is down, will Pub/Sub deliver later?
✔ No.

❓ Can Redis Streams replace Kafka?
✔ For small to medium workloads, yes.

❓ Are Streams blocking?
✔ Can be blocking with `XREAD BLOCK`.

❓ Are Streams stored in memory?
✔ Yes, with optional persistence.

---

## 8.6 One PERFECT interview answer

> Redis provides Pub/Sub for real-time, transient messaging and Streams for persistent, replayable event processing with consumer groups. The choice depends on whether message durability is required.

---

## ✅ STEP 8 COMPLETE — You should now confidently explain

✔ Pub/Sub flow
✔ Why Pub/Sub loses messages
✔ Streams internals (high level)
✔ Consumer groups
✔ Streams vs Pub/Sub vs Kafka