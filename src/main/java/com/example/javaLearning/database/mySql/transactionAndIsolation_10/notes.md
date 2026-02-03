# 🔥 STEP 10: TRANSACTIONS & ISOLATION (REAL-WORLD UNDERSTANDING)

> If indexing shows performance skill,
> **transactions show correctness under pressure.**

---

## 🧠 First: What a Transaction REALLY Is

A transaction is **a logical unit of work** that must be:

* Correct
* Isolated from others
* Durable even after crashes

💬 Interview line:

> “Transactions protect data correctness under concurrency.”

---

## 10.1 ACID (ONLY WHAT INTERVIEWERS EXPECT)

| Property    | What it REALLY means               |
| ----------- | ---------------------------------- |
| Atomicity   | All statements succeed or none     |
| Consistency | Constraints remain valid           |
| Isolation   | Concurrent txns don’t corrupt data |
| Durability  | Committed data survives crash      |

🚨 **Important correction (senior insight)**
MySQL guarantees **Atomicity + Durability** via logs
**Consistency is your responsibility (schema + code)**

💬 Killer line:

> “Databases enforce constraints, not business logic.”

---

## 10.2 AUTOCOMMIT (SNEAKY INTERVIEW TRAP)

By default:

```sql
autocommit = ON
```

Meaning:

* Every statement is its own transaction
* `INSERT` commits immediately

```sql
START TRANSACTION;
UPDATE ...
UPDATE ...
COMMIT;
```

💬 Interview line:

> “Without explicit transactions, each statement commits independently.”

---

## 10.3 Isolation Levels (FOCUS ON BEHAVIOR)

| Level            | Allows                             |
| ---------------- | ---------------------------------- |
| READ UNCOMMITTED | Dirty reads                        |
| READ COMMITTED   | Non-repeatable reads               |
| REPEATABLE READ  | Phantom prevention (MySQL default) |
| SERIALIZABLE     | Full isolation (slow)              |

📌 **MySQL default = REPEATABLE READ**

---

## 10.4 The 3 Classic Problems (MUST KNOW)

### 1️⃣ Dirty Read

Read uncommitted data → ❌ bad
(MySQL basically doesn’t allow this in InnoDB)

---

### 2️⃣ Non-repeatable Read

```text
T1 reads row
T2 updates row + commits
T1 reads again → different value
```

Prevented in:

* REPEATABLE READ
* SERIALIZABLE

---

### 3️⃣ Phantom Read (IMPORTANT)

```text
T1: SELECT count(*) WHERE status='PAID'
T2: INSERT new PAID row
T1: SELECT again → count changes
```

💡 **MySQL prevents this using locks**, not snapshots.

💬 Interview gold:

> “MySQL prevents phantom reads using next-key locks.”

---

## 10.5 REPEATABLE READ (WHY MYSQL IS SPECIAL)

Most DBs:

* RR still allows phantoms

MySQL:

* RR + **gap locks**
* Prevents new rows in range

👉 This is why MySQL’s RR behaves almost like SERIALIZABLE for ranges.

---

## 10.6 MVCC + Transactions (HIGH-YIELD)

### How reads work:

* Snapshot created at first read
* Reads use undo logs
* No row locks

### How writes work:

* Lock rows
* Write redo log
* Commit → visible

💬 Interview line:

> “MVCC allows consistent reads without blocking writers.”

---

## 10.7 Locks You MUST Understand

| Lock          | Purpose               |
| ------------- | --------------------- |
| Row lock      | Protect specific rows |
| Gap lock      | Protect ranges        |
| Next-key lock | Row + gap             |

### When gap locks appear:

* Range queries
* RR isolation
* Indexed conditions

🚨 This surprises people:

```sql
SELECT * FROM orders
WHERE id BETWEEN 10 AND 20
FOR UPDATE;
```

Locks **gaps too**, not just rows.

---

## 10.8 Deadlocks (VERY COMMON INTERVIEW QUESTION)

### What is a deadlock?

Two transactions waiting on each other forever.

### Classic cause:

```text
T1 locks row A → waits for B
T2 locks row B → waits for A
```

### MySQL behavior:

* Detects deadlock
* Rolls back **one** transaction

💬 Interview killer:

> “Deadlocks are resolved automatically by InnoDB by rolling back one transaction.”

---

## 10.9 WHY Deadlocks Happen in Real Life

Most common reasons:

1. Different index usage
2. Missing index → range locks
3. Different update order
4. Batch updates

💬 Senior-level line:

> “Most deadlocks are caused by missing or inconsistent index usage.”

---

## 10.10 How YOU Reduce Deadlocks (INTERVIEW GOLD)

✔ Always access tables in same order
✔ Use proper indexes
✔ Keep transactions short
✔ Avoid large range updates

---

## 10.11 One FULL Interview Scenario (MEMORIZE)

**Q:** Why does MySQL deadlock even on simple updates?

**Answer:**

> “Because under REPEATABLE READ, range queries acquire next-key locks, and concurrent transactions can lock overlapping ranges in different orders.”

🔥 That answer alone screams *senior*.

---

## ✅ STEP 10 CHECKPOINT

You must confidently explain:

✔ What a transaction is
✔ MySQL default isolation
✔ Dirty / non-repeatable / phantom reads
✔ Why MySQL RR prevents phantoms
✔ How deadlocks occur and are resolved

If yes → **you are now database-engineer level**

---

## 🚀 NEXT STEP

Next we go **even deeper but still interview-safe**:

👉 **STEP 11: MVCC (Undo Logs, Read Views, Timelines)**
This is where most candidates fail — and you won’t.

Say **“Start Step 11”** when ready 💪
