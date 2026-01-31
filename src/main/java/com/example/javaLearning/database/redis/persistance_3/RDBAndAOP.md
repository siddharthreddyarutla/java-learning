These are **excellent OS-level questions** — you’re now thinking like a **systems engineer**, not just a Redis user 👌
Let’s go step-by-step and remove *all* ambiguity. I’ll explain this **from OS → Redis → disk → restart**, cleanly.

---

# 1️⃣ What is `fork()`? (OS-level, very important)

### Simple definition

`fork()` is an **OS system call** that:

> Creates a **new process** by copying the current process.

After `fork()`:

* **Parent process** → original Redis
* **Child process** → snapshot / rewrite worker

Both run **independently**.

---

## 1.1 Parent vs Child after fork

After `fork()`:

| Process | What it does              |
| ------- | ------------------------- |
| Parent  | Continues serving clients |
| Child   | Writes RDB / AOF rewrite  |

They:

* Have separate PIDs
* Run in parallel (scheduled by OS)
* Do NOT block each other

👉 Important:

> Redis being single-threaded means **one thread per process**, not “only one process allowed”.

---

# 2️⃣ What is Copy-On-Write (COW)?

COW is the **magic** that makes fork efficient.

---

## 2.1 Without COW (bad)

If Redis copied all memory:

* Huge RAM spike
* Very slow

---

## 2.2 With COW (what actually happens)

After fork:

* Parent & child **share the same memory pages**
* Pages are **read-only**

Only when:

* Parent modifies a page
  → OS **copies that page**

That’s Copy-On-Write.

---

### Visual mental model

![Image](https://s3.ap-south-1.amazonaws.com/s3.studytonight.com/tutorials/uploads/pictures/1609221978-71449.png)

![Image](https://cdn.jsdelivr.net/gh/b0xt/sobyte-images1/2022/08/24/cd8101527eab4634a3b664334ddfe53f.png)

```
Before write:
Parent → Page A
Child  → Page A

After parent modifies Page A:
Parent → Page A'
Child  → Page A
```

👉 This is why:

* Snapshot is fast
* But heavy writes increase memory usage

---

# 3️⃣ Redis is single-threaded — how do parent & child run?

Important clarification:

| Concept         | Meaning                          |
| --------------- | -------------------------------- |
| Single-threaded | One execution thread per process |
| Multi-process   | OS runs multiple processes       |

So:

* Redis main process = 1 thread
* Child snapshot process = 1 thread
* OS schedules both on CPU cores

👉 No contradiction.

---

# 4️⃣ RDB: What happens on crash & restart?

Great question — let’s do **full lifecycle**.

---

## 4.1 Where is RDB stored?

* On **disk**
* Default file: `dump.rdb`
* Location: `dir` config

Example:

```conf
dir /var/lib/redis
dbfilename dump.rdb
```

---

## 4.2 What happens during snapshot?

1. Child process writes:

    * `dump.rdb.tmp`
2. Once complete:

    * Atomic rename → `dump.rdb`

Old snapshot stays safe until new one is ready.

---

## 4.3 Redis crashes — then what?

On restart:

1. Redis starts fresh (empty RAM)
2. Reads `dump.rdb` from disk
3. Loads entire dataset into RAM
4. Starts accepting clients

👉 YES:

> RDB file is read and fully loaded back into RAM.

Redis **never queries disk at runtime**.

---

# 5️⃣ AOF: How async writing works internally

This is where people get confused — let’s make it crystal clear.

---

## 5.1 Where is AOF stored?

* On disk
* Default file: `appendonly.aof`
* Same `dir` config

---

## 5.2 Is AOF one file or many?

### Historically:

* Single file: `appendonly.aof`

### Modern Redis:

* Multiple AOF files:

    * Base AOF
    * Incremental AOF files

But logically:

> Redis treats AOF as a **single logical log**

---

## 5.3 AOF write flow (detailed)

![Image](https://www.nootcode.com/knowledge/redis-aof/en/redis-aof-mechanism.png)

![Image](https://assets.bytebytego.com/diagrams/0085-big-keys.png)

Steps:

1. Client sends write command
2. Redis executes command in memory
3. Command appended to **AOF buffer (RAM)**
4. Background thread flushes buffer to disk

---

## 5.4 How async fsync works

### Policy: `appendfsync everysec`

Flow:

* Redis writes to OS page cache
* Every 1 second:

    * fsync flushes to disk

So:

* Redis thread is NOT blocked
* Disk writes happen asynchronously

Worst case loss:

* Last 1 second of writes

---

## 5.5 What happens if Redis crashes during AOF write?

Possible states:

* AOF has partial command
* Redis detects corruption
* Truncates safely
* Continues loading valid commands

---

# 6️⃣ AOF Rewrite: Files & reconnection

---

## 6.1 Why rewrite uses fork?

Same reason as RDB:

* Avoid blocking
* Use COW

---

## 6.2 File flow during rewrite

Files involved:

* `appendonly.aof`
* `temp-rewriteaof.aof`
* Incremental buffer in RAM

Flow:

1. Child writes compact AOF
2. Parent logs new writes
3. Merge buffer
4. Atomic rename

---

## 6.3 Redis restart with AOF enabled

Order:

1. Redis reads **AOF first**
2. Replays commands
3. Builds memory
4. Discards RDB (if both exist)

---

# 7️⃣ Where is data *really* stored?

| Layer         | Purpose               |
| ------------- | --------------------- |
| RAM           | Active dataset        |
| OS page cache | Buffered disk writes  |
| Disk          | Persistence (RDB/AOF) |

Redis:

* Reads from disk only on startup
* Writes to disk via OS buffers

---

In Redis, the Append Only File (AOF) persists the commands that modify data (add/update operations). The AOF only saves the commands themselves, not the results of those commands.

Key points regarding the Redis AOF:
* Only Modifying Commands: The AOF records operations that change the dataset, such as SET, HSET, LPUSH, and SADD and .
* No Read Operations: Read operations, like GET, HGET, or LLEN are not recorded in the AOF because they do not alter the data state.
* Command Format: The file contains the exact commands sent by clients to the Redis server, formatted in a protocol the server can replay upon restart to reconstruct the dataset
  This approach ensures that replaying the AOF exactly restores the database to the state it was in before the server shut down.

---

# 8️⃣ How Redis “reconnects” data after crash

Not reconnect — **reconstructs**.

* No metadata DB
* No indexes on disk
* Just replays or loads snapshot

That’s why:

* Restart time depends on dataset size

---

# 🔥 Interview-level summary (memorize)

> Redis uses OS fork to create background child processes for persistence. Copy-on-write allows memory pages to be shared efficiently. On restart, Redis loads RDB snapshots or replays AOF logs entirely into RAM. Disk is never used during runtime queries.

---

## 🔜 Next logical step

Now you’re ready for:

### **Step 4: Redis Memory Management, Eviction, Fragmentation**

This explains:

* maxmemory
* LRU vs LFU
* OOM behavior

Say **“Step 4”** when ready 🚀
