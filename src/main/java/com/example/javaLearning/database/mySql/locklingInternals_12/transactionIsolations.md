## 1️⃣ What isolation levels MySQL (InnoDB) uses

* **Default isolation level (InnoDB)**: ✅ **REPEATABLE READ**
* Yes, **you can change it**:

```sql
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

(or `SERIALIZABLE`, etc.)

---

## 2️⃣ READ COMMITTED vs REPEATABLE READ (CORE DIFFERENCE)

### Table

```sql
orders(id, status)
```

Initial data:

```
1 | PAID
2 | PAID
```

---

## 3️⃣ READ COMMITTED (RC)

👉 **Each SELECT sees latest committed data**

### T1 (READ COMMITTED)

```sql
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT COUNT(*) FROM orders WHERE status='PAID';
-- Result: 2
```

### T2

```sql
INSERT INTO orders VALUES (3,'PAID');
COMMIT;
```

### T1 again

```sql
SELECT COUNT(*) FROM orders WHERE status='PAID';
-- Result: 3  ✅ changed
```

✔ **Non-repeatable reads allowed**
✔ **Phantom rows appear**

---

## 4️⃣ REPEATABLE READ (RR – DEFAULT)

👉 **Same snapshot for entire transaction**

### T1 (REPEATABLE READ)

```sql
START TRANSACTION;

SELECT COUNT(*) FROM orders WHERE status='PAID';
-- Result: 2
```

### T2

```sql
INSERT INTO orders VALUES (3,'PAID');
COMMIT;
```

### T1 again

```sql
SELECT COUNT(*) FROM orders WHERE status='PAID';
-- Result: 2  ✅ unchanged
```

✔ **Repeatable reads guaranteed**
✔ **No phantom reads (InnoDB)**

---

## 5️⃣ What Are Phantom Rows? (VERY SIMPLE)

A **phantom row** is:

> A row that **appears in a later SELECT** within the same transaction.

Example:

```sql
SELECT * FROM orders WHERE status='PAID';
```

Later:

```sql
SELECT * FROM orders WHERE status='PAID';
```

If a **new matching row appears**, that’s a **phantom**.

---

## 6️⃣ Why InnoDB RR Prevents Phantoms

InnoDB uses:

* **MVCC** → consistent snapshot
* **Gap / next-key locks** → block inserts in range

So:

```sql
SELECT ... FOR UPDATE
```

➡️ Locks gaps
➡️ Prevents phantom inserts

📌 This is **why MySQL RR is stronger than other DBs**

---

## 7️⃣ SERIALIZABLE (VERY SHORT)

```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

* Converts SELECT into **locking reads**
* Highest safety
* Lowest concurrency

Use case:
✔ Financial batch jobs
❌ High-traffic OLTP

---

## 8️⃣ When to Use Which (INTERVIEW-READY)

| Isolation       | Use case                 |
| --------------- | ------------------------ |
| READ COMMITTED  | High write throughput    |
| REPEATABLE READ | Default OLTP correctness |
| SERIALIZABLE    | Critical financial ops   |

💬 Interview line:

> “Most systems use Repeatable Read; some switch to Read Committed to reduce deadlocks.”

---

## ✅ Final One-Liners (MEMORIZE)

* “InnoDB default isolation is Repeatable Read.”
* “Read Committed creates a new snapshot per SELECT.”
* “Repeatable Read uses one snapshot per transaction.”
* “Phantom rows are new rows appearing in the same transaction.”
* “InnoDB prevents phantoms using gap locks.”