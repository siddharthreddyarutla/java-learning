# 🔹 RDB: Start-to-End Flow (Snapshot)

### 🔁 Normal runtime

1. App sends `SET / GET`
2. Redis:

    * Executes command
    * Stores data **in RAM (heap)**
3. Disk is **NOT touched**

---

### 📸 When RDB snapshot is triggered

( `save` rule / `BGSAVE` )

```
Redis (parent)
   |
   |-- fork()
   |
Child process
```

---

### 🔹 Child process flow

1. Child **reads entire in-memory dataset**
2. Writes it to:

   ```
   dump.rdb.tmp
   ```
3. Data goes to:

    * **OS page cache first**
    * Then flushed to disk
4. When done:

    * Atomic rename → `dump.rdb`

👉 YES:
✅ **Full dataset is written every time**
❌ Not incremental

---

### 🔹 Parent process during snapshot

* Continues serving requests
* Writes stay in RAM
* Uses **Copy-On-Write** if memory changes

---

### 💥 Redis crashes

* RAM is gone
* Disk remains

---

### 🔄 Redis restart

1. Redis starts empty
2. Reads `dump.rdb` from disk
3. Loads **entire dataset into RAM**
4. Accepts traffic

👉 Disk → RAM happens **only at startup**

---

# 🔹 AOF: Start-to-End Flow (Command Log)

### 🔁 Normal runtime (write command)

1. App sends:

   ```
   SET a 10
   ```
2. Redis:

    * Executes command in RAM
    * Appends command to **AOF buffer (RAM)**

---

### 📝 Writing to disk (async)

Depends on policy (`everysec` most common):

1. AOF buffer → OS page cache
2. Every 1 second:

    * `fsync` flushes to disk

File:

```
appendonly.aof
```

👉 YES:
✅ **Only commands are written**
❌ Not full data

---

### 🧹 AOF rewrite (cleanup)

1. Redis forks child
2. Child writes **minimal commands**
3. Parent buffers new writes
4. Merge + atomic replace

---

### 💥 Redis crashes

* RAM lost
* AOF file remains

---

### 🔄 Redis restart

1. Redis reads `appendonly.aof`
2. Replays commands **one by one**
3. Rebuilds RAM
4. Ready

---

# 🔹 Key Differences (One-glance)

| Feature         | RDB            | AOF                |
| --------------- | -------------- | ------------------ |
| What is written | Full dataset   | Commands           |
| When            | Periodic       | Every write        |
| Disk I/O        | Heavy but rare | Light but frequent |
| Restart         | Fast           | Slower             |
| Data loss       | Possible       | Minimal            |

---

# 🔹 OS & Buffers (Very short)

* Redis writes → **OS page cache**
* OS flushes → **disk**
* Redis does **not manage disk directly**
* fork + COW handled by OS