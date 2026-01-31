# 🧱 STEP 10: Common Redis Interview Questions (Rapid Fire + Traps)

I’ll give:

* ✅ **Expected answer**
* ⚠️ **What NOT to say** (trap)

---

## 🔹 Basics (Warm-up)

### ❓ Is Redis single-threaded?

✅ **Answer**:
Yes, Redis executes commands in a single thread, but network I/O is multi-threaded in Redis 6+.

⚠️ Don’t say: *Redis is fully single-threaded.*

---

### ❓ Why is Redis so fast?

✅

* In-memory storage (RAM)
* Single-threaded execution (no locks)
* I/O multiplexing
* Optimized data structures

⚠️ Don’t say only: *Because it uses RAM.*

---

## 🔹 Data Types

### ❓ Difference between Set and Sorted Set?

✅

* Set: unordered, unique elements
* Sorted Set: ordered by score, supports ranking

⚠️ Don’t say: *Sorted Set is just a sorted list.*

---

### ❓ Why is Sorted Set used for leaderboards?

✅
Because it maintains order by score and supports fast rank queries in O(log n).

---

### ❓ Are all Redis operations O(1)?

✅
No. Most are O(1), but operations like LRANGE, ZRANGE, HGETALL are O(n).

⚠️ Saying *everything is O(1)* is a red flag.

---

## 🔹 Persistence

### ❓ Redis is in-memory. What happens if it crashes?

✅
Data in RAM is lost, but Redis can recover using RDB snapshots or AOF logs if enabled.

---

### ❓ RDB vs AOF?

✅

* RDB: snapshot, faster restart, possible data loss
* AOF: command log, safer, slower restart

👉 Best practice: **use both**

---

### ❓ Does Redis write to disk on every request?

✅
No. Writes go to memory first. Disk writes are asynchronous (AOF/RDB).

---

## 🔹 Memory & Eviction

### ❓ What happens when Redis memory is full?

✅
Depends on `maxmemory-policy`:

* Evict keys
* Or reject writes

---

### ❓ LRU vs LFU?

✅

* LRU: evict least recently used keys
* LFU: evict least frequently used keys

---

### ❓ Does Redis delete expired keys immediately?

✅
No. It uses:

* Lazy expiration
* Active expiration (sampling)

---

## 🔹 Transactions & Atomicity

### ❓ Are Redis operations atomic?

✅
Yes, **each command** is atomic.

---

### ❓ Does Redis support transactions like MySQL?

✅
Partially. Redis supports MULTI/EXEC but:

* No rollback
* No isolation levels
* Not ACID

---

### ❓ MULTI vs Lua?

✅
Lua scripts are fully atomic and preferred for complex logic.

---

## 🔹 Pub/Sub & Streams

### ❓ Redis Pub/Sub vs Streams?

✅

* Pub/Sub: real-time, no persistence
* Streams: persistent, replayable, consumer groups

---

### ❓ If a Pub/Sub consumer is down, will it receive messages later?

✅
No.

---

## 🔹 High Availability & Cluster

### ❓ Is Redis replication synchronous?

✅
No, it’s asynchronous.

---

### ❓ Can Redis guarantee zero data loss?

✅
No. Small data loss is possible due to async replication.

---

### ❓ Sentinel vs Cluster?

✅

* Sentinel: HA only
* Cluster: HA + sharding + write scaling

---

### ❓ What are hash slots in Redis Cluster?

✅
Redis Cluster uses **16384 hash slots** to distribute keys across nodes.

---

### ❓ Why Redis Cluster doesn’t support multi-key operations easily?

✅
Because keys may belong to different hash slots.
Solution: **hash tags `{}`**.

---

## 🔹 Redis vs Others (Very common)

### ❓ Redis vs MySQL?

✅
Redis is a fast in-memory store, MySQL is a durable relational database.
They are used **together**, not as replacements.

---

### ❓ Redis vs Memcached?

✅

* Redis: persistence, data types, replication
* Memcached: simple cache, no persistence

---

### ❓ Redis vs Kafka?

✅

* Redis Streams: lightweight, in-memory
* Kafka: durable, disk-based, high throughput

---

## 🔹 System Design Traps

### ❓ Should Redis be used as a primary database?

✅
Only for specific workloads (cache, counters, sessions).
Not for critical financial data.

---

### ❓ How do you prevent cache stampede?

✅

* TTL with jitter
* Mutex / locking
* Cache warming

---

### ❓ How is distributed lock implemented in Redis?

✅
Using:

```bash
SET lock:key value NX EX seconds
```

---

## 🧠 ONE FINAL PERFECT ANSWER (MEMORIZE)

> Redis is an in-memory data structure store optimized for speed using RAM, a single-threaded event loop, and efficient data structures. It supports optional persistence, replication, and clustering, and is commonly used alongside databases for caching, sessions, rate limiting, and real-time systems.

---

## 🎯 You are now READY if you can:

✔ Explain Redis internals calmly
✔ Justify design choices
✔ Answer trade-offs (not just features)
✔ Correct interview traps politely