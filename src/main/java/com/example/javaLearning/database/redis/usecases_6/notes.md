# 🧱 STEP 6: Redis Use Cases (Companies LOVE This)

When interviewers ask:

> ❓ *“Where have you used Redis?”*

They expect:

* **Clear use case**
* **Correct data type**
* **Why Redis fits**
* **Basic commands**

We’ll do **6 core use cases** you MUST be confident with.

---

## 6.1 Caching (MOST common)

### Problem

* DB queries are slow
* Same data requested repeatedly

### Redis Solution

* Store DB result in Redis
* Serve future requests from memory

### Data Type

* **String / Hash**

### Flow (Cache-Aside Pattern)

1. Check Redis
2. Cache miss → query DB
3. Store in Redis with TTL
4. Return response

```bash
GET user:1
SET user:1 "{...json...}" EX 300
```

### Why Redis?

✔ Fast
✔ TTL support
✔ Simple key access

---

## 6.2 Session Storage

### Problem

* Stateless services
* Load balancer switches servers

### Redis Solution

* Central session store

### Data Type

* **Hash**

```bash
HSET session:abc123 userId 42 role admin
EXPIRE session:abc123 1800
```

### Why Redis?

✔ Shared across servers
✔ Fast reads
✔ TTL auto-cleanup

---

## 6.3 Rate Limiting

### Problem

* Too many requests
* Prevent abuse / DDoS

### Redis Solution

* Count requests per user/IP

### Data Type

* **String / Sorted Set**

Simple counter approach:

```bash
INCR rate:user:1
EXPIRE rate:user:1 60
```

Sorted set approach:

```bash
ZADD rate:user:1 <timestamp> reqId
```

### Why Redis?

✔ Atomic INCR
✔ Expiry
✔ O(1) ops

---

## 6.4 Leaderboards

### Problem

* Rank users by score
* Frequent updates

### Redis Solution

* Sorted Set

### Data Type

* **Sorted Set**

```bash
ZADD leaderboard 100 user1
ZRANK leaderboard user1
ZREVRANGE leaderboard 0 9 WITHSCORES
```

### Why Redis?

✔ Sorted data
✔ Fast rank queries
✔ O(log n) updates

---

## 6.5 Message Queues / Task Queues

### Problem

* Async processing
* Background jobs

### Redis Solution

* Use List or Streams

### Data Type

* **List / Stream**

List-based queue:

```bash
LPUSH jobs job1
BRPOP jobs 0
```

Stream-based:

```bash
XADD events * type order_created
```

### Why Redis?

✔ Fast enqueue/dequeue
✔ Blocking ops
✔ Lightweight

---

## 6.6 Distributed Locks

### Problem

* Multiple services
* Avoid double processing

### Redis Solution

* Atomic SET with TTL

### Data Type

* **String**

```bash
SET lock:order:1 "uuid" NX EX 10
```

Release:

```bash
DEL lock:order:1
```

### Why Redis?

✔ Atomic operations
✔ TTL prevents deadlock
✔ Simple

---

## 6.7 Bonus Use Cases (Name-drop in interviews)

You don’t need deep details, just mention:

| Use Case       | Redis Feature |
| -------------- | ------------- |
| Analytics      | HyperLogLog   |
| Feature flags  | String        |
| Online status  | Set           |
| Pub/Sub        | Messaging     |
| Event sourcing | Streams       |

---

## 6.8 Common Interview Follow-ups

❓ Why Redis for rate limiting instead of DB?
✔ Atomic ops + speed.

❓ Why Redis for session storage?
✔ Centralized, fast, TTL.

❓ Redis vs Kafka for queues?
✔ Redis is lightweight, Kafka is durable & scalable.

---

## 6.9 One PERFECT interview answer

> Redis is commonly used for caching, session storage, rate limiting, leaderboards, background queues, and distributed locking because it provides fast in-memory access, atomic operations, and flexible data structures.

---

## ✅ STEP 6 COMPLETE — You should now be able to:

✔ Map use case → data type
✔ Explain why Redis fits
✔ Show commands confidently
✔ Answer real-world questions