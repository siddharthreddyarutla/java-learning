# 🔥 STEP 17: PERFORMANCE TUNING (HOW REAL SYSTEMS STAY FAST)

> Indexes fix queries.
> **Tuning fixes the database itself.**

---

## 🧠 First: Performance = 3 Layers

Think in this order (VERY IMPORTANT):

1. **Query level** (EXPLAIN, indexes) ✅ you already know
2. **InnoDB memory & IO**
3. **Operational mistakes**

Most people jump to (1) only. Seniors think in all 3.

---

## 17.1 INNODB BUFFER POOL (MOST IMPORTANT SETTING)

### What it is

* Memory cache for:

    * Data pages
    * Index pages

If data is in buffer pool → **no disk IO**

💬 Interview line:

> “Buffer pool hit ratio is the most important MySQL performance metric.”

---

### Key rule

* Dedicated DB server:

```text
innodb_buffer_pool_size ≈ 70–80% of RAM
```

If buffer pool is small:
❌ Disk reads
❌ Slow queries even with indexes

---

### How to check

```sql
SHOW ENGINE INNODB STATUS;
```

Look for:

* Buffer pool hit rate
* Pages read from disk

---

## 17.2 SLOW QUERY LOG (YOUR BEST FRIEND)

### What it captures

* Queries slower than threshold
* Queries without indexes

Enable:

```sql
slow_query_log = ON
long_query_time = 1
```

💬 Interview line:

> “I always start optimization from the slow query log.”

---

## 17.3 TOO MANY INDEXES (COMMON MISTAKE)

Each index:

* Speeds up SELECT
* Slows down:

    * INSERT
    * UPDATE
    * DELETE

Why?

* Every write updates **all indexes**

💬 Interview killer:

> “Indexes are not free; they increase write cost.”

---

## 17.4 BIG TRANSACTIONS (SILENT KILLER)

Problems:

* Hold locks longer
* Delay undo purge
* Increase replication lag
* Increase deadlocks

🚨 Example:

```sql
UPDATE orders SET status='PAID';
-- 1 million rows in one transaction
```

Better:

* Batch updates
* Commit frequently

---

## 17.5 SELECT * (WHY INTERVIEWERS HATE IT)

Problems:

* Reads unnecessary columns
* Breaks covering indexes
* More IO
* Schema changes hurt more

💬 Interview line:

> “SELECT * prevents covering index usage.”

---

## 17.6 TEMP TABLES & FILESORT (WHEN TO CARE)

Not all temp tables are bad.

### Worry when:

* Temp tables go to disk
* Seen frequently in slow query log

Causes:

* GROUP BY without index
* ORDER BY without index
* DISTINCT on large data

---

## 17.7 CONNECTION MANAGEMENT

Too many connections:

* Context switching
* Memory pressure

Best practice:

* Use connection pools
* Limit max connections

💬 Interview line:

> “Connection pooling is mandatory in production.”

---

## 17.8 REPLICATION & PERFORMANCE (IMPORTANT)

### Lag caused by:

* Large transactions
* Heavy writes
* Single-threaded apply

Fixes:

* Smaller transactions
* Parallel replication
* Faster disks on replicas

---

## 17.9 GALERA-SPECIFIC PERFORMANCE NOTES (FOR YOU)

Since you use Galera:

✔ Writes are synchronous
✔ Network latency matters
✔ Conflicts cost retries

Best practices:

* Avoid hot rows
* Keep transactions short
* Retry logic in app

💬 Interview line:

> “Galera performance depends heavily on write contention.”

---

## 17.10 REAL INTERVIEW SCENARIO (MEMORIZE)

**Q:** Queries are fast locally but slow in prod. Why?

**Answer:**

> “Likely buffer pool is smaller in prod, causing disk IO.”

That answer alone shows **experience**.

---

## 🎯 PERFORMANCE CHECKLIST (MEMORIZE)

✔ Correct indexes
✔ High buffer pool hit rate
✔ Slow query log enabled
✔ No huge transactions
✔ Limited indexes
✔ No SELECT *
✔ Proper connection pooling

---

## ✅ STEP 17 CHECKPOINT

You can now explain:

✔ Why buffer pool matters
✔ How slow query log helps
✔ Why too many indexes hurt
✔ Why big transactions are bad
✔ Galera performance trade-offs

If yes → you’re **production-ready**