# 🔥 STEP 12: LOCKING INTERNALS (ROW, GAP, NEXT-KEY)

> MVCC gives **non-blocking reads**
> **Locks protect writes and ranges**

MySQL correctness = **MVCC + Locks**

---

## 🧠 First: Why Locks Exist If MVCC Exists?

MVCC handles:
✔ reading old versions
❌ **does NOT stop new rows from appearing**

So locks are needed to:

* Prevent lost updates
* Prevent phantom rows
* Enforce isolation guarantees

---

## 12.1 Types of Locks (ONLY THESE MATTER)

| Lock Type     | What it protects  |
| ------------- | ----------------- |
| Row lock      | Existing rows     |
| Gap lock      | Gaps between rows |
| Next-key lock | Row + gap         |

📌 **Next-key lock = Row lock + Gap lock**

---

## 12.2 ROW LOCK (Simple One)

### Example

```sql
UPDATE orders SET status='PAID' WHERE id = 10;
```

Locks:
🔒 Row with PK = 10

✔ Other rows unaffected
✔ Inserts allowed elsewhere

💬 Interview line:

> “Row locks protect individual records.”

---

## 12.3 GAP LOCK (THIS CONFUSES EVERYONE)

Gap lock protects a **range where rows may appear**, not rows themselves.

### Example data (PK index)

```
id: 5   10   20
```

Gaps:

```
(-∞,5) (5,10) (10,20) (20,+∞)
```

---

### Query

```sql
SELECT * FROM orders
WHERE id BETWEEN 10 AND 20
FOR UPDATE;
```

Locks:
🔒 Row 10
🔒 Row 20
🔒 Gap (10,20)

🚫 No one can insert id=15
✔ id=25 allowed

💬 Interview gold:

> “Gap locks prevent phantom inserts.”

---

## 12.4 NEXT-KEY LOCK (WHAT MYSQL REALLY USES)

MySQL rarely uses *only* row or gap locks.
It uses **next-key locks by default** under RR.

### Means:

```
(row lock) + (gap after row)
```

So locking id=10 also locks:

```
(10, next record)
```

This is why **range queries are dangerous**.

---

## 12.5 WHEN DOES MYSQL USE GAP / NEXT-KEY LOCKS?

### Required conditions:

✔ Isolation = REPEATABLE READ
✔ Indexed condition
✔ Range query
✔ SELECT … FOR UPDATE / UPDATE / DELETE

---

### Example

```sql
DELETE FROM orders
WHERE created_on >= '2024-01-01';
```

Locks:
🔒 All matching rows
🔒 Gaps between them

💥 High deadlock risk

---

## 12.6 READS THAT ACQUIRE LOCKS (IMPORTANT)

| Query                     | Locks?   |
| ------------------------- | -------- |
| SELECT                    | ❌ No     |
| SELECT FOR UPDATE         | ✅ X-lock |
| SELECT LOCK IN SHARE MODE | ✅ S-lock |

### Example

```sql
SELECT * FROM users WHERE id=1 FOR UPDATE;
```

🔒 Blocks:

* UPDATE
* DELETE
* Other FOR UPDATE

💬 Interview line:

> “SELECT FOR UPDATE is a locking read.”

---

## 12.7 LOCKING + MISSING INDEX (PRODUCTION KILLER)

### Query

```sql
UPDATE orders SET status='PAID'
WHERE status='NEW';
```

If `status` is **NOT indexed**:

What MySQL does:

1. Full table scan
2. Locks **every row**
3. Locks **entire table ranges**

🚨 Effectively table-level locking

💬 Interview killer:

> “Missing indexes cause unintended wide locking.”

---

## 12.8 HOW DEADLOCKS FORM WITH GAP LOCKS

### Timeline

**T1**

```sql
SELECT * FROM orders
WHERE id BETWEEN 10 AND 20
FOR UPDATE;
```

Locks:
🔒 Rows 10,20
🔒 Gap (10,20)

---

**T2**

```sql
INSERT INTO orders VALUES (15,...);
```

⏸ blocked by gap lock

---

**T1 later**

```sql
INSERT INTO orders VALUES (25,...);
```

⏸ blocked by T2’s insert intention lock

➡️ 💥 Deadlock

💬 Interview line:

> “Gap locks + insert intention locks commonly cause deadlocks.”

---

## 12.9 INSERT INTENTION LOCK (BONUS)

Before inserting, MySQL sets:
🔒 **Insert intention lock**

Purpose:

* Coordinate concurrent inserts
* Avoid chaos in gaps

This lock **can deadlock with gap locks**.

---

## 12.10 WHY READ COMMITTED HAS FEWER DEADLOCKS

In READ COMMITTED:
❌ No gap locks (mostly)
✔ Better concurrency
❌ Phantoms possible

This is why some teams:

* Use RC in high-write systems
* Accept phantoms

💬 Interview line:

> “Lower isolation reduces locking but weakens guarantees.”

---

## 12.11 HOW YOU REDUCE LOCKING ISSUES (MEMORIZE)

✔ Use correct indexes
✔ Avoid range updates
✔ Keep transactions short
✔ Access rows in same order
✔ Avoid SELECT FOR UPDATE unless required

---

## 12.12 VISUAL (LOCKING SNAPSHOT)

![Image](https://i.sstatic.net/3lfku.png)

![Image](https://www.brightbox.com/images/blog/mysql_locks.png)

![Image](https://framerusercontent.com/images/vsxLxwBIPBY2RpNMVmhK0HI4AQ.png?height=1024\&scale-down-to=512\&width=1536)

Mentally remember:

* Row lock = dot
* Gap lock = space
* Next-key = dot + space

---

## 🎯 INTERVIEW POWER ANSWERS

* “MySQL uses next-key locks under REPEATABLE READ.”
* “Gap locks prevent phantom inserts.”
* “Missing indexes cause wide locking.”
* “SELECT FOR UPDATE participates in deadlocks.”
* “READ COMMITTED reduces gap locking.”

---

## ✅ STEP 12 CHECKPOINT

You should now clearly explain:

✔ Difference between row, gap, next-key
✔ Why range queries lock gaps
✔ How reads can lock
✔ Why missing indexes cause deadlocks
✔ Why RC behaves differently

If yes → **you are now *very* strong in MySQL internals**