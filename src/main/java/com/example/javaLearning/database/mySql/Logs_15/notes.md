# 🔥 STEP 15: MYSQL LOGS — REDO, UNDO, BINLOG

## Crash Recovery, Consistency & CDC (Change data capture)

> If MVCC explains **reads**,
> logs explain **writes, crashes, and replication**.

---

## 🧠 Big Picture (MEMORIZE THIS)

MySQL uses **THREE different logs** for **THREE different problems**.

| Log      | Solves                      |
| -------- | --------------------------- |
| Redo log | Crash recovery (Durability) |
| Undo log | MVCC + Rollback             |
| Binlog   | Replication + CDC           |

💬 Interview one-liner:

> “Redo is physical, binlog is logical, undo is version history.”

---

## 15.1 REDO LOG (WAL — MOST IMPORTANT)

### What problem it solves

👉 **Durability**

If MySQL crashes:

* Memory is lost
* Disk pages may be half-written

Redo log fixes this.

---

### How redo log works (WRITE-AHEAD LOG)

When you run:

```sql
UPDATE accounts SET balance=200 WHERE id=1;
```

Order of operations:

1. Change written to **redo log (sequential IO)**
2. Transaction marked committed
3. Data pages flushed later

📌 **Redo is written BEFORE data pages**

That’s WAL.

---

### Crash scenario

* MySQL crashes
* On restart:

    * Reads redo log
    * Replays committed changes
    * Ignores uncommitted ones

💬 Interview line:

> “Redo log replays committed changes after crash.”

---

## 15.2 REDO LOG STRUCTURE (ENOUGH FOR INTERVIEW)

* Fixed-size
* Circular (ring buffer)
* Stored in `ib_logfile*`

Writes:

* Sequential → fast
  Flush:
* Controlled by `innodb_flush_log_at_trx_commit`

---

### Durability settings (VERY COMMON QUESTION)

| Value | Meaning                              |
| ----- | ------------------------------------ |
| 1     | Flush on every commit (safe, slower) |
| 2     | Flush every second (balanced)        |
| 0     | OS decides (fast, unsafe)            |

💬 Interview-safe line:

> “Setting 1 gives strongest durability guarantees.”

---

## 15.3 UNDO LOG (MVCC + ROLLBACK)

### What undo log does

* Stores **old versions of rows**
* Used for:

    * Rollback
    * MVCC reads

---

### Example

```sql
UPDATE users SET salary=200 WHERE id=1;
```

Steps:

1. Old row written to undo log
2. Data page updated
3. Roll pointer updated

If transaction rolls back:
➡️ Undo log restores old value

If reader needs old version:
➡️ Undo log supplies it

💬 Interview line:

> “Undo logs provide older row versions for MVCC.”

---

### Undo log lifecycle (IMPORTANT)

* Stored in undo tablespace
* Purge thread cleans old undo
* **Blocked by long-running transactions**

🚨 Production issue:

> Long transactions = undo bloat

---

## 15.4 BINLOG (REPLICATION + CDC)

### What binlog is

* Logical change log
* Records **what happened**, not how

Used for:
✔ Replication
✔ Backups
✔ CDC (Kafka, Debezium)

---

### Binlog content

Depending on format:

* Statement-based
* Row-based
* Mixed

Modern MySQL:
➡️ **ROW-based** (default & safest)

💬 Interview line:

> “Binlog is used for replication, not crash recovery.”

---

## 15.5 REDO vs BINLOG (CONFUSION KILLER)

| Aspect             | Redo           | Binlog      |
| ------------------ | -------------- | ----------- |
| Scope              | Storage engine | Server-wide |
| Purpose            | Crash recovery | Replication |
| Format             | Physical       | Logical     |
| Needed after crash | ✅ Yes          | ❌ No        |
| Needed for replica | ❌ No           | ✅ Yes       |

💬 Killer line:

> “Redo recovers the same server; binlog feeds other servers.”

---

## 15.6 COMMIT FLOW (VERY IMPORTANT)

When a transaction commits:

1. Write undo (already done)
2. Write redo log
3. Write binlog
4. Mark commit

📌 **Two-phase commit** ensures:

* Redo + binlog stay consistent

💬 Interview gold:

> “MySQL uses two-phase commit to sync redo and binlog.”

---

## 15.7 CRASH SCENARIOS (INTERVIEW CLASSIC)

### Crash AFTER redo, BEFORE binlog

➡️ Rollback on restart
➡️ Binlog ignored

### Crash AFTER binlog, BEFORE redo

➡️ Redo replayed
➡️ Binlog applied on replica

Consistency preserved.

---

## 15.8 CDC & KAFKA (RELEVANT TO YOU)

CDC tools:

* Read **binlog**
* Convert row events to messages
* Push to Kafka

📌 They **do NOT read redo or undo**

💬 Interview line:

> “CDC tools consume MySQL binlog, not redo logs.”

---

## 15.9 COMMON PRODUCTION MISTAKES

🚨 Turning off redo durability for speed
🚨 Long-running transactions blocking undo purge
🚨 Binlog disabled → replication impossible

---

## 15.10 VISUAL MAP (LOCK THIS IN)

![Image](https://segmentfault.com/img/remote/1460000041919267)

![Image](https://substackcdn.com/image/fetch/%24s_%21vSYT%21%2Cf_auto%2Cq_auto%3Agood%2Cfl_progressive%3Asteep/https%3A%2F%2Fsubstack-post-media.s3.amazonaws.com%2Fpublic%2Fimages%2Fc16c0cb9-b79b-45e4-8fee-04aacfa73bc0_1394x715.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2AGwdFh0b0Im-dz-yclKJsFA.png)

Mentally remember:

* Undo → past
* Redo → crash recovery
* Binlog → replication

---

## 🎯 INTERVIEW ONE-LINERS (MEMORIZE)

* “Redo is for crash recovery.”
* “Undo is for MVCC and rollback.”
* “Binlog is for replication and CDC.”
* “Redo is physical; binlog is logical.”
* “MySQL uses two-phase commit.”

---

## ✅ STEP 15 CHECKPOINT

You can now explain:

✔ Why redo exists
✔ How undo supports MVCC
✔ Why binlog is separate
✔ How crash recovery works
✔ How CDC tools work

If yes → **you’re absolutely senior-level in MySQL internals**
