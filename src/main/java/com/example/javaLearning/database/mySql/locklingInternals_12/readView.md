# 🔐 LOCK TYPES + READ VIEWS (TOGETHER, NOT ISOLATED)

Think in **two layers**:

> **Read view = what version you see**
> **Locks = who you block**

They work **together**, not separately.

---

## 1️⃣ READ VIEW (MVCC SNAPSHOT) — FOUNDATION

### What is a read view?

A **read view** defines:

> “Which committed data versions are visible to my transaction”

Created:

* **REPEATABLE READ** → once per transaction
* **READ COMMITTED** → once per SELECT

📌 Read view controls **visibility**, not locking.

---

### Example (Read View in action)

**T1 (RR)**

```sql
START TRANSACTION;
SELECT balance FROM accounts WHERE id=1;
-- sees 100
```

**T2**

```sql
UPDATE accounts SET balance=200 WHERE id=1;
COMMIT;
```

**T1 again**

```sql
SELECT balance FROM accounts WHERE id=1;
-- still sees 100 (from undo log)
```

✔ No locks
✔ Read view + undo used

---

## 2️⃣ PLAIN SELECT (NO LOCKS)

```sql
SELECT * FROM orders WHERE id = 10;
```

What happens:

* Uses **read view**
* Reads from **data page or undo**
* ❌ No row lock
* ❌ No gap lock

✔ Does not block writes
✔ Does not participate in deadlocks

💡 **This is pure MVCC**

---

## 3️⃣ ROW LOCK (WRITE LOCK)

### Example

```sql
UPDATE orders SET status='PAID' WHERE id=10;
```

Locks:

* 🔒 **Row lock on id = 10**

Behavior:

* Blocks other UPDATE/DELETE on id=10
* Plain SELECT still allowed (MVCC)

📌 Row locks protect **existing rows only**

---

## 4️⃣ LOCKING READS (READ + LOCK)

### SELECT … FOR UPDATE

```sql
SELECT * FROM orders WHERE id=10 FOR UPDATE;
```

Locks:

* 🔒 **Exclusive row lock (X-lock)**

Uses:

* When you plan to update after read

✔ Uses **read view**
✔ Also acquires **locks**

💬 Key insight:

> “Locking reads still use MVCC for visibility.”

---

### SELECT … LOCK IN SHARE MODE

```sql
SELECT * FROM orders WHERE id=10 LOCK IN SHARE MODE;
```

Locks:

* 🔒 **Shared lock (S-lock)**

Behavior:

* Blocks UPDATE/DELETE
* Allows other shared reads

---

## 5️⃣ GAP LOCK (RANGE PROTECTION)

Gap locks protect **ranges where rows could appear**.

### Data

```
id: 5    10    20
```

Gaps:

```
(5,10) (10,20)
```

---

### Example

```sql
SELECT * FROM orders
WHERE id BETWEEN 10 AND 20
FOR UPDATE;
```

Locks:

* 🔒 Row lock on 10
* 🔒 Row lock on 20
* 🔒 Gap lock on (10,20)

🚫 Prevents insert of id=15
✔ Prevents phantom rows

📌 Gap locks are used **only in RR**

---

## 6️⃣ NEXT-KEY LOCK (WHAT INNODB REALLY USES)

> **Next-key lock = Row lock + Gap lock**

InnoDB default behavior:

* Uses **next-key locks** for range operations

### Example

```sql
UPDATE orders
SET status='PAID'
WHERE id >= 10 AND id <= 20;
```

Locks:

* Rows: 10, 20
* Gaps between them

🚨 High deadlock potential

---

## 7️⃣ INSERT INTENTION LOCK (SPECIAL)

Before inserting, MySQL sets:

* 🔒 **Insert intention lock** on gap

Purpose:

* Coordinate concurrent inserts

This can deadlock with:

* Gap locks
* Next-key locks

---

## 8️⃣ READ VIEW + LOCKS (HOW THEY COMBINE)

### Plain SELECT

✔ Read view
❌ Locks

### SELECT FOR UPDATE

✔ Read view
✔ Row / next-key locks

### UPDATE / DELETE

❌ Read view (for target rows)
✔ Row / gap locks

💡 Important:

> Locks control **concurrency**, read view controls **visibility**

---

## 9️⃣ WHY PHANTOMS ARE PREVENTED IN INNODB RR

* Read view → same snapshot
* Gap / next-key locks → prevent inserts

➡️ Together:
✔ No new rows appear
✔ No phantom reads

---

## 🔟 QUICK TABLE (MEMORIZE)

| Operation         | Read View | Row Lock | Gap Lock       |
| ----------------- | --------- | -------- | -------------- |
| SELECT            | ✅         | ❌        | ❌              |
| SELECT FOR UPDATE | ✅         | ✅        | ⚠️ (range)     |
| UPDATE PK         | ❌         | ✅        | ❌              |
| UPDATE range      | ❌         | ✅        | ✅              |
| INSERT            | ❌         | ❌        | ⚠️ (intention) |

---

## 🎯 INTERVIEW ONE-LINERS

Use these **exactly**:

* “Read views control visibility; locks control concurrency.”
* “Plain SELECT uses MVCC and acquires no locks.”
* “SELECT FOR UPDATE is a locking read.”
* “InnoDB uses next-key locks under Repeatable Read.”
* “Gap locks prevent phantom inserts.”

---

If this is clear, we’re perfectly aligned.

👉 Next: **STEP 14 – SUBQUERIES, EXISTS, IN (Optimizer traps & interview tricks)**
Just say **“Start Step 14”** 💪
