### MVCC (Multi-Version Concurrency Control)

# 🔥 STEP 11: MVCC INTERNALS (INNODB — REAL, NOT TEXTBOOK)

> MVCC = **how MySQL gives you fast reads + correctness at the same time**

---

## 🧠 Why MVCC Exists (ONE LINE YOU SHOULD SAY)

> “MVCC allows concurrent reads and writes without blocking.”

Without MVCC:

* Readers block writers ❌
* Writers block readers ❌
* Throughput dies

---

## 11.1 The 3 CORE BUILDING BLOCKS

InnoDB MVCC is built on **only three things**:

1. **Undo Logs**
2. **Transaction IDs**
3. **Read Views**

That’s it. Everything else is derived.

---

## 11.2 Hidden Columns in Every Row (CRITICAL)

Every InnoDB row **logically** has:

| Field          | Purpose                    |
| -------------- | -------------------------- |
| `trx_id`       | Last modifying transaction |
| `roll_pointer` | Pointer to undo log        |
| Row data       | Actual columns             |

📌 These aren’t visible, but they exist.

---

## 11.3 Undo Logs (VERSION HISTORY)

Undo logs store **older versions of rows**, not just for rollback.

### On UPDATE:

```sql
UPDATE users SET salary = 1000 WHERE id = 1;
```

Internally:

1. Old row → written to undo log
2. Data page updated
3. `trx_id` updated
4. `roll_pointer` points to undo

💬 Interview line:

> “Undo logs store previous row versions for MVCC and rollback.”

---

## 11.4 Read View (THE SNAPSHOT)

A **read view** defines:

> “Which transactions are visible to me?”

Created:

* At **first SELECT** in transaction (RR)
* At **every SELECT** (RC)

---

### Read View contains:

| Field             | Meaning                   |
| ----------------- | ------------------------- |
| `up_limit_id`     | Oldest active transaction |
| `low_limit_id`    | Next transaction ID       |
| `active trx list` | Running transactions      |

---

## 11.5 VERSION VISIBILITY RULE (MOST IMPORTANT)

When reading a row with `trx_id = X`:

| Condition            | Visible? |
| -------------------- | -------- |
| X < up_limit_id      | ✅ Yes    |
| X ≥ low_limit_id     | ❌ No     |
| X in active trx list | ❌ No     |
| Else                 | ✅ Yes    |

If not visible:
➡️ Follow `roll_pointer`
➡️ Read undo version
➡️ Repeat until visible

💬 Interview killer:

> “InnoDB walks the undo chain until it finds a visible version.”

---

## 11.6 WHERE DOES READ ACTUALLY COME FROM?

**IMPORTANT CLARIFICATION**

Reads always:

1. Start from **data page**
2. Check visibility
3. Use undo log **only if needed**

✔ Not all reads hit undo
✔ Only older snapshots do

---

## 11.7 MVCC + ISOLATION LEVELS

### READ COMMITTED

* Read view per SELECT
* Sees latest committed data

### REPEATABLE READ (default)

* One read view per transaction
* Same snapshot throughout

💬 Interview line:

> “Repeatable Read uses a consistent snapshot for the entire transaction.”

---

## 11.8 WHY MVCC DOES NOT PREVENT PHANTOMS ALONE

MVCC:
✔ Handles row versions
❌ Cannot stop new rows

So MySQL adds:
🔒 **Gap locks + next-key locks**

👉 MVCC + locks = full isolation

---

## 11.9 PURGE PROCESS (IMPORTANT BUT MISSED)

Undo logs don’t live forever.

### Purge thread:

* Removes undo records
* Only after no active read views need them

🚨 Long-running transactions:

* Prevent purge
* Grow undo tablespace
* Cause performance issues

💬 Interview gold:

> “Long-running transactions delay undo purge.”

---

## 11.10 COMMON MVCC PRODUCTION ISSUES

| Issue           | Cause             |
| --------------- | ----------------- |
| High undo space | Long transactions |
| Slow purge      | Open snapshots    |
| Old data reads  | RR isolation      |
| Deadlocks       | Locks + MVCC      |

---

## 11.11 ONE FULL MVCC TIMELINE (MEMORIZE)

### T10 starts (RR)

```sql
SELECT salary FROM users WHERE id=1; -- salary = 100
```

### T20 updates

```sql
UPDATE users SET salary=200 WHERE id=1;
COMMIT;
```

### T10 reads again

```sql
SELECT salary FROM users WHERE id=1;
```

➡️ Still sees **100**
➡️ Reads from undo log

### New transaction T30

```sql
SELECT salary FROM users WHERE id=1;
```

➡️ Sees **200**
➡️ Reads from data page

---

## 🎯 INTERVIEW ONE-LINERS (MEMORIZE)

* “MVCC uses undo logs and read views.”
* “Reads don’t block writes.”
* “Undo logs store old row versions.”
* “Repeatable Read uses a single snapshot.”
* “Long transactions delay purge.”

---

## ✅ STEP 11 CHECKPOINT

You should now clearly explain:

✔ How undo logs work
✔ What read view is
✔ How visibility is checked
✔ When undo is used
✔ Why long txns are bad

If yes → **you are now solid senior level**