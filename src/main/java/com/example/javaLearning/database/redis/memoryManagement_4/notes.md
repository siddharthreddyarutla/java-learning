# 🧱 STEP 4: Redis Memory Management & Eviction (LRU, LFU, OOM)

This is where Redis behaves like a **real cache**, not just a fast store.

---

## 4.1 How Redis uses memory (baseline)

* All data lives in **heap memory**
* Redis allocates memory via:

    * jemalloc (default)
* OS gives Redis memory pages
* Redis never swaps to disk (unless OS forces it → bad)

👉 Redis is **memory-bound**, not disk-bound.

---

## 4.2 maxmemory — the MOST IMPORTANT config

```conf
maxmemory 4gb
```

What it means:

* Redis will **never exceed this limit**
* Once reached → eviction or error

If NOT set:

* Redis uses **all available RAM**
* Can cause OOM at OS level

👉 Interview answer:

> maxmemory defines the upper bound of Redis memory usage.

---

## 4.3 What happens when memory is full?

Two possibilities:

1. Redis evicts keys
2. Redis rejects writes (`OOM command not allowed`)

This depends on **eviction policy**.

---

## 4.4 Eviction Policies (🔥 memorize)

```conf
maxmemory-policy <policy>
```

### All Redis eviction policies

| Policy          | Meaning                     |
| --------------- | --------------------------- |
| noeviction      | Reject writes               |
| allkeys-lru     | Evict least recently used   |
| volatile-lru    | LRU only on TTL keys        |
| allkeys-lfu     | Evict least frequently used |
| volatile-lfu    | LFU only on TTL keys        |
| allkeys-random  | Random eviction             |
| volatile-random | Random TTL keys             |
| volatile-ttl    | Lowest TTL first            |

---

## 4.5 LRU — How Redis *actually* implements it

❌ Redis does NOT use true LRU
✅ Uses **Approximate LRU**

### Why approximate?

* True LRU = heavy bookkeeping
* Redis trades precision for speed

How it works:

* Each key stores a **last-access timestamp**
* Random sampling of keys
* Evicts the best candidate

👉 Interview gold:

> Redis uses approximate LRU to reduce overhead while maintaining good eviction accuracy.

---

## 4.6 LFU — How frequency-based eviction works

Introduced in Redis 4+

Each key tracks:

* Access frequency counter
* Decays over time

Eviction:

* Removes **least frequently used** keys

Better than LRU for:

* Hot keys accessed repeatedly
* Avoids “scan pollution”

---

## 4.7 TTL & Expiration (Lazy vs Active)

### Setting TTL

```bash
SET key value EX 60
```

---

### Two ways Redis deletes expired keys

#### 1️⃣ Lazy Expiration

* Key checked **only when accessed**
* If expired → deleted

#### 2️⃣ Active Expiration

* Redis periodically:

    * Samples keys with TTL
    * Deletes expired ones

👉 Why not scan all keys?

* Too expensive

---

## 4.8 Memory Fragmentation (advanced but asked)

### What is fragmentation?

* Memory allocated ≠ memory used

Causes:

* Object creation/deletion
* Copy-On-Write during fork

Check:

```bash
INFO memory
```

Fragmentation ratio:

* ~1.0 → healthy
* > 1.5 → concern

---

## 4.9 jemalloc & lazy freeing

### jemalloc

* Efficient memory allocator
* Reduces fragmentation

### Lazy freeing

* Large deletes happen asynchronously
* Avoids blocking main thread

Commands affected:

* DEL (large keys)
* FLUSHDB
* UNLINK (preferred)

---

## 4.10 Common Interview Questions (Step 4)

❓ What happens when Redis runs out of memory?
✔ Depends on eviction policy.

❓ Redis LRU vs LFU?
✔ LRU = recency, LFU = frequency.

❓ Does Redis delete expired keys immediately?
✔ No, lazy + active deletion.

❓ How to avoid OOM?
✔ Set maxmemory + eviction policy.

---

## ✅ What you should now clearly understand

✔ How Redis allocates memory
✔ maxmemory behavior
✔ Eviction policies
✔ TTL handling
✔ Fragmentation & lazy freeing