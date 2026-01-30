# 1️⃣ Why does a **Foreign Key add overhead on writes**?

A foreign key is **not just a rule** — it is **extra work the storage engine must do on every write**.

Let’s take a simple FK:

```sql
orders.user_id → users.id
```

Now look at what happens internally.

---

## 1.1 INSERT into child table (orders)

```sql
INSERT INTO orders(user_id, amount) VALUES (101, 500);
```

### What InnoDB must do (extra work):

1. **Lookup in parent table**

    * Check if `users.id = 101` exists
    * Requires an **index lookup** on parent PK

2. **Acquire shared lock (S-lock) on parent row**

    * Prevents parent row from being deleted while insert is happening

3. **Insert child row**

    * Write undo
    * Write redo
    * Lock child row

📌 Without FK → step 1 & 2 don’t exist
📌 With FK → every insert does **extra index lookup + locking**

That’s the write overhead.

---

## 1.2 UPDATE on child FK column

```sql
UPDATE orders SET user_id = 102 WHERE id = 5001;
```

InnoDB must:

* Validate **new parent exists**
* Lock old parent row
* Lock new parent row

This is **significantly more expensive** than a normal update.

---

## 1.3 DELETE from parent table (users)

```sql
DELETE FROM users WHERE id = 101;
```

InnoDB must:

1. Search child table for referencing rows
2. Depending on rule:

    * RESTRICT → block
    * CASCADE → delete child rows
    * SET NULL → update child rows

This can:

* Scan large child indexes
* Lock many rows
* Create long transactions

🔥 This is where FK becomes dangerous at scale.

---

# 2️⃣ If parent row is updated, does it lock child table?

Short answer: **No, not the entire child table** — but **yes, related rows may be affected depending on what you update**.

Let’s be precise.

---

## 2.1 Updating parent **non-PK column**

```sql
UPDATE users SET name = 'John' WHERE id = 101;
```

✔ No FK checks
✔ No child table access
✔ Only parent row is locked

👉 **No extra FK overhead**

---

## 2.2 Updating parent **PRIMARY KEY** (rare but possible)

```sql
UPDATE users SET id = 201 WHERE id = 101;
```

Now FK rules kick in.

InnoDB must:

1. Find all child rows referencing `101`
2. Depending on FK rule:

    * CASCADE → update child rows
    * RESTRICT → block update

This requires:

* Scanning child FK index
* Locking child rows

📌 **This is why updating PKs is strongly discouraged**

---

## 2.3 Does InnoDB lock the entire child table?

❌ No table-level lock
✔ Row-level locks on:

* Parent row
* Matching child rows

But:

* If many child rows exist → many locks
* Can *feel* like table lock under load

---

# 3️⃣ Why does Primary Key choice affect INSERT speed?

This is **pure InnoDB internals**.

Remember:

> **InnoDB stores data sorted by PRIMARY KEY**

---

## 3.1 Sequential PK (AUTO_INCREMENT)

```text
1, 2, 3, 4, 5 …
```

### Insert behavior:

* Always goes to **rightmost leaf page**
* Minimal page splits
* Excellent cache locality
* Mostly sequential I/O

✔ Fast inserts
✔ Low fragmentation

---

## 3.2 Random PK (UUID)

```text
A9F3…, 1B72…, FF09…, 003A…
```

### Insert behavior:

* Insert happens **anywhere in B-Tree**
* Frequent page splits
* Pages evicted from cache
* Random I/O

❌ Slower inserts
❌ Larger indexes
❌ Fragmentation

📌 This is not theoretical — this kills write throughput.

---

## 3.3 Secondary Index Amplification

Every INSERT must:

* Insert into clustered index
* Insert into **every secondary index**

So:

* Larger PK → larger secondary indexes
* Random PK → random writes everywhere

This is why:

> PK choice affects **every index**

---

# 4️⃣ Why Foreign Keys may hurt performance (Big Picture)

Let’s combine everything.

---

## 4.1 Write Amplification

With FK:

* Every insert/update/delete touches **multiple tables**
* More index lookups
* More locks
* More undo/redo

Without FK:

* Single-table operation

---

## 4.2 Lock Contention

FKs cause:

* Parent row locks
* Child row locks
* Longer transactions

Under concurrency:

* Increased lock waits
* Higher deadlock probability

---

## 4.3 Cascading Operations (Worst Case)

```sql
ON DELETE CASCADE
```

Deleting **one row** can:

* Delete thousands of child rows
* Hold locks for long time
* Cause replication lag
* Cause app timeouts

Senior rule:

> Cascades are convenient but dangerous at scale.

---

## 4.4 Replication & CDC Cost

FK cascades:

* Produce large binlog entries
* Increase replication lag
* Increase Kafka CDC traffic

This matters a lot in distributed systems.

---

# 5️⃣ Senior Engineer Mental Model (Lock This In)

```
Primary Key:
  → Determines data layout
  → Affects all indexes
  → Affects insert speed

Foreign Key:
  → Adds validation work
  → Adds locks
  → Adds write amplification
```

---

# 6️⃣ Interview-Perfect Answers (Short & Sharp)

### ❓ Why do foreign keys add overhead?

> Because the database must validate parent existence and enforce referential integrity on every write, which requires additional index lookups and locking.

### ❓ Does updating parent lock child table?

> No table-level locks, but row-level locks on related child rows may be acquired depending on the FK rule.

### ❓ Why does PK choice affect insert speed?

> Because InnoDB stores rows in primary key order, and sequential keys avoid page splits while random keys cause fragmentation and random I/O.

### ❓ Why may FKs hurt performance?

> They increase write amplification, lock contention, and cascading operations, which reduces throughput in high-write systems.

---

## Final Check (Important)

Make sure these are clear in your head:

1. FK overhead happens mainly on **writes**, not reads
2. PK choice affects **data layout**, not just identity
3. FK ≠ table lock, but can cause **many row locks**
4. Cascades are the real danger, not FK itself