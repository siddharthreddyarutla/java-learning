Perfect — **Step 3 is where Redis stops feeling “risky”** and starts feeling production-ready.
This step answers **crash, restart, durability, and safety** questions that interviewers *love*.

---

# 🧱 STEP 3: Redis Persistence & Crash Recovery (IN DEPTH)

Core interview question:

> ❓ *Redis is in-memory — what happens if Redis crashes or restarts?*

Short answer:

> Redis provides **optional persistence** using **RDB** and **AOF**.

Now let’s go **deep, clean, and structured**.

---

## 3.1 First, set the mental model (IMPORTANT)

Redis is:

* **In-memory first**
* **Persistence second**
* **Performance > durability by default**

So persistence is **configurable**, not mandatory.

---

## 3.2 Two Persistence Mechanisms (Big Picture)

![Image](https://substackcdn.com/image/fetch/%24s_%21HeLD%21%2Cf_auto%2Cq_auto%3Agood%2Cfl_progressive%3Asteep/https%3A%2F%2Fsubstack-post-media.s3.amazonaws.com%2Fpublic%2Fimages%2F003fc232-d5ce-4286-b368-1773e819e359_2478x1536.png)

![Image](https://miro.medium.com/1%2AkgDlyG6LWa__rpBRbDIhQQ.png)

![Image](https://miro.medium.com/1%2Auh9IkNndhgIjqu-cMCgFgw.jpeg)

Redis supports **two independent persistence methods**:

1. **RDB** → Snapshotting
2. **AOF** → Command logging

They can be:

* Used individually
* Used together (common in prod)

---

# 🔹 PART A: RDB (Redis Database Snapshot)

---

## 3.3 What is RDB?

RDB takes a **snapshot of Redis memory** and saves it to disk as a `.rdb` file.

Think of it as:

> “A photo of Redis memory at a moment in time”

---

## 3.4 How RDB works internally

![Image](https://www.nootcode.com/knowledge/redis-rdb/en/redis-rdb-mechanism.png)

![Image](https://i.sstatic.net/R1ODN.png)

Steps:

1. Redis calls `fork()`
2. Child process:

    * Reads memory
    * Writes snapshot to disk
3. Parent continues serving clients

### Why `fork()`?

* Avoid blocking Redis
* Use **Copy-On-Write (COW)**

👉 Interview gold:

> Redis uses fork and copy-on-write to create RDB snapshots without blocking clients.

---

## 3.5 RDB Configuration

Example:

```conf
save 900 1
save 300 10
save 60 10000
```

Meaning:

* Save if **1 change in 15 min**
* Save if **10 changes in 5 min**
* Save if **10k changes in 1 min**

---

## 3.6 RDB Pros & Cons

### ✅ Pros

* Fast restart
* Compact file
* Minimal runtime overhead

### ❌ Cons

* Data loss between snapshots
* Fork can be heavy on large datasets

---

## 3.7 RDB Interview Traps

❓ What happens if Redis crashes before snapshot?
✔ Data since last snapshot is lost.

❓ Is Redis blocked during snapshot?
✔ No, child process handles it.

---

# 🔹 PART B: AOF (Append Only File)

---

## 3.8 What is AOF?

AOF logs **every write command** Redis executes.

Example:

```text
SET a 1
INCR a
DEL b
```

On restart:

* Redis **replays commands**
* Rebuilds memory

---

## 3.9 AOF Write Flow (Internal)

![Image](https://www.nootcode.com/knowledge/redis-aof/en/redis-aof-mechanism.png)

![Image](https://assets.bytebytego.com/diagrams/0085-big-keys.png)

Flow:

1. Client sends write command
2. Redis executes command
3. Command appended to AOF buffer
4. Buffer flushed to disk (based on policy)

---

## 3.10 AOF fsync Policies (VERY IMPORTANT)

| Policy   | Description        | Safety     | Speed   |
| -------- | ------------------ | ---------- | ------- |
| always   | fsync every write  | 🔥 safest  | ❌ slow  |
| everysec | fsync every second | ✅ balanced | ✅       |
| no       | OS decides         | ❌ risky    | 🔥 fast |

👉 **Most production systems use `everysec`**

Interview line:

> AOF with everysec provides a good balance between durability and performance.

---

## 3.11 AOF Rewrite (Critical concept)

Problem:

* AOF grows indefinitely

Solution:

### AOF Rewrite (Compaction)

Example:

```text
SET a 1
SET a 2
SET a 3
```

Rewritten as:

```text
SET a 3
```

---

## 3.12 How AOF Rewrite Works (Internals)

Steps:

1. Redis forks child
2. Child writes minimal commands
3. Parent logs new commands to buffer
4. Buffers merged at end

✔ Non-blocking
✔ Safe
✔ Efficient

---

## 3.13 AOF Pros & Cons

### ✅ Pros

* Minimal data loss
* Precise recovery

### ❌ Cons

* Larger files
* Slightly slower than RDB

---

# 🔹 PART C: RDB + AOF Together (PRODUCTION SETUP)

---

## 3.14 Why use both?

| Concern       | RDB | AOF |
| ------------- | --- | --- |
| Restart speed | ✅   | ❌   |
| Data safety   | ❌   | ✅   |
| File size     | ✅   | ❌   |

👉 Common setup:

```conf
save ...
appendonly yes
appendfsync everysec
```

Redis loads:

1. **AOF first**
2. Falls back to RDB

---

## 3.15 What happens on Redis restart?

Order:

1. Load AOF (if enabled)
2. Replay commands
3. Memory rebuilt
4. Accept traffic

---

# 🔥 CRASH SCENARIOS (INTERVIEW FAVORITE)

---

### ❓ Redis crashes mid-write (AOF everysec)

Worst-case data loss:

* **~1 second**

---

### ❓ Disk corruption?

* AOF can be truncated
* Redis can still recover partial data

---

### ❓ Power failure during fork?

* Parent continues
* Old snapshot remains safe

---

# 🧠 Summary Table (Memorize This)

| Feature          | RDB      | AOF         |
| ---------------- | -------- | ----------- |
| Persistence type | Snapshot | Command log |
| Data loss        | Possible | Minimal     |
| Restart speed    | Fast     | Slow        |
| File size        | Small    | Large       |
| Production use   | Yes      | Yes         |

---

## ✅ You should now confidently answer

✔ How Redis persists data
✔ Difference between RDB & AOF
✔ Crash recovery scenarios
✔ fsync policies
✔ AOF rewrite internals
✔ Production persistence strategy