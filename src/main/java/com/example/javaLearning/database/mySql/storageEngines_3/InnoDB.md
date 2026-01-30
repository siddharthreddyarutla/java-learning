## 1. Big Picture: How InnoDB Stores Data

InnoDB stores data in **pages** (typically 16KB) and uses **B+Tree indexes**.

Key idea:

* Every **table is stored as a B+Tree**, called the **clustered index**.
* Each **leaf node** of this B+Tree holds the **actual row**.

So:
**Table data = Clustered index on the PRIMARY KEY.**

If you don’t define a primary key:

1. InnoDB looks for a **non-null UNIQUE** index and uses that as the clustered index, or
2. If none exists, it creates a hidden 6-byte column (`row_id`) and uses that as the clustered index.

---

## 2. Clustered Index & PRIMARY KEY

### What is a clustered index?

* Index where the **index order = physical row order** on disk.
* In InnoDB, the **PRIMARY KEY is always clustered**.

So if you do:

```sql
CREATE TABLE employee (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100),
  dept_id INT,
  created_on DATETIME
) ENGINE=InnoDB;
```

The B+Tree for `PRIMARY KEY (id)`:

* Internal nodes: store key ranges (id values).
* Leaf nodes: store `{id, name, dept_id, created_on}` as the *actual row*.

### Why PRIMARY KEY choice matters

Because **all secondary indexes point to the primary key**, a **wide or random PK** can:

* Make secondary indexes bigger
* Cause more random I/O
* Unnecessarily slow joins

That’s why people like:

* Auto-increment integer PK → **monotonic**, good for clustered index.

---

## 3. Secondary Indexes, UNIQUE, and Normal Indexes

### Secondary index

Example:

```sql
CREATE INDEX idx_employee_dept ON employee(dept_id);
```

This creates a B+Tree too, but:

* Leaf nodes **do NOT store the full row**.
* They store:

  ```text
  (dept_id, PRIMARY_KEY_VALUE)
  ```

So with PK = `id`:

* Leaf = `(dept_id, id)`

To get the full row:

1. Use the secondary index to locate `(dept_id, id)`.
2. Then go back to the **clustered index on (id)** to find the row.
   This is the famous **“back to the table” / bookmark lookup**.

### UNIQUE index

Example:

```sql
CREATE UNIQUE INDEX idx_email ON employee(email);
```

This is just like a secondary index, **but**:

* InnoDB enforces that `(email)` must be unique.
* Technically, uniqueness is checked on `(email, primary_key)` under the hood.

### PRIMARY KEY vs UNIQUE

| Feature              | PRIMARY KEY         | UNIQUE Index                        |
| -------------------- | ------------------- | ----------------------------------- |
| Null allowed?        | ❌ No                | ✔ Yes (but uniqueness per non-null) |
| Clustered index?     | ✔ Yes (exactly one) | ❌ No                                |
| Used as row pointer? | ✔ Yes               | Indirect (stores PK)                |

---

## 4. Why Indexed Queries Are Fast

### Without index (full table scan)

```sql
SELECT * FROM employee WHERE dept_id = 10;
```

If no index on `dept_id`:

* InnoDB scans every row in clustered index (whole table).
* Pages read = almost all data pages in that table.
* Time complexity ≈ O(N).

### With index on `dept_id`

```sql
CREATE INDEX idx_employee_dept ON employee(dept_id);
```

Now:

1. InnoDB walks the **B+Tree of `dept_id`** to find all rows where `dept_id = 10`.

    * Cost: O(log N) to find first, then cheap to scan contiguous leaves.
2. For each match, it uses the stored `id` (PK) to fetch rows from clustered index.

If the index **covers** all requested columns:

```sql
SELECT dept_id FROM employee WHERE dept_id = 10;
```

Then it **doesn’t even need to go back to the table** → **covering index** → very fast.

---

## 5. “Partial Indexing” – Why It “Doesn’t Work” Like You Expect

People usually mean *one of two things* when they say “partial index”:

### 5.1. Meaning 1: “Index only part of the rows (like WHERE active = 1)”

Example from other DBs (like PostgreSQL):

```sql
CREATE INDEX idx_active ON employee(id) WHERE active = 1;
```

This is called a **filtered/partial index** (only some rows are indexed).

👉 **MySQL/InnoDB does NOT support this kind of partial index.**
Any normal index in MySQL always indexes **all rows**.

So attempts to “partially index” using WHERE clause in `CREATE INDEX` simply **don’t work** (syntax error or ignored); MySQL does not have filtered indexes (as of now).

**Workarounds**:

* Use generated columns (e.g., index expression like `IF(active = 1, id, NULL)`) – but usage is tricky.
* Use separate table for active rows.

---

### 5.2. Meaning 2: “Prefix index (INDEX(col(10)))”

Example:

```sql
CREATE INDEX idx_name_prefix ON employee(name(10));
```

This indexes only the **first 10 characters** of `name`.

It **does work**, but with caveats:

* Uniqueness:
  `UNIQUE INDEX name(10)` only guarantees **uniqueness on the first 10 chars**, not the full string.
* Sorting / range operations:
  For queries like:

  ```sql
  SELECT * FROM employee ORDER BY name;
  ```

  MySQL sometimes **cannot use** the index to satisfy `ORDER BY exactly`, because it only knows order of first 10 chars, not the full string.

So you may feel “partial indexing doesn’t work” because:

* Either you tried filtered indexes → not supported.
* Or prefix indexes don’t help certain queries or uniqueness like you expected.

---

## 6. Locking in InnoDB – Row-Level Locking, Gap Locks, Inserts

Now the fun bit: **how InnoDB locks rows**, and what happens on `INSERT`, `UPDATE`, `DELETE`, `SELECT`.

### 6.1 Autocommit and transactions

* If `autocommit = 1` (default):

    * Each individual statement is its own transaction.
* If `autocommit = 0` or you use `START TRANSACTION`:

    * Multiple statements run inside one transaction until `COMMIT` or `ROLLBACK`.

Locks are held **until transaction commits/rolls back**.

---

### 6.2 Types of locks (simplified)

* **Row locks** (record locks):

    * **Shared (S)** → read lock (used in `SELECT ... LOCK IN SHARE MODE`)
    * **Exclusive (X)** → write lock (`UPDATE`, `DELETE`, `SELECT ... FOR UPDATE`)
* **Intention locks** on table (IS, IX, etc.):

    * Internal, used to coordinate row locks with table-level operations.
* **Gap locks**:

    * Lock a range **between** index records.
* **Next-key locks**:

    * Lock a record **and** the gap before it.
* **Insert intention locks**:

    * For concurrent inserts into the same index gap without blocking each other.

---

### 6.3 Does an INSERT take locks?

**Yes.**

When you do:

```sql
INSERT INTO employee (id, name, dept_id) VALUES (101, 'Alex', 10);
```

InnoDB:

1. Acquires an **insert intention lock** on the gap where the new record will go.
2. Inserts it into the B+Tree.
3. Acquires an **exclusive lock** on the newly inserted row (record lock).
4. Checks **uniqueness** for any unique indexes (PK, unique key).
5. Holds these locks until the transaction commits.

Other sessions can:

* Still insert other rows into different gaps.
* But if they insert the **same unique key**, they will block on the **unique index check**.

---

### 6.4 How row-level locking works with UPDATE/DELETE

Example:

```sql
UPDATE employee SET dept_id = 20 WHERE id = 101;
```

Steps:

1. Use **index** (on PK `id`) to find the record.
2. Acquire an **exclusive (X) row lock** on that record (next-key lock if needed).
3. Perform the update.
4. Lock is held until COMMIT.

Other sessions trying to UPDATE the same row will:

* Wait (block) until the first transaction commits or rolls back.

**Row-level locking** = only the affected rows are locked, not the whole table → good concurrency.

---

### 6.5 What about SELECT – does it lock rows?

Depends:

#### Non-locking SELECT

```sql
SELECT * FROM employee WHERE id = 101;
```

* This uses **MVCC (Multi-Version Concurrency Control)**.
* It **does not lock** the row.
* It reads a **consistent snapshot** using **undo logs**.
* Other transactions can still update the row concurrently.

#### Locking SELECT

```sql
SELECT * FROM employee WHERE id = 101 FOR UPDATE;
```

* Acquires **exclusive (X) row locks** (or next-key locks).
* Prevents other transactions from updating or deleting these rows until commit.

```sql
SELECT * FROM employee WHERE id = 101 LOCK IN SHARE MODE;
```

* Acquires **shared (S) row locks**.
* Other transactions can also acquire shared locks, but **cannot update/delete** until you commit.

---

### 6.6 Gap locks & Next-key locks (why they exist)

Default isolation level in MySQL InnoDB = **REPEATABLE READ**.

To avoid **phantom reads**, InnoDB uses **next-key locking**:

* When you run a range query with `SELECT ... FOR UPDATE` or `UPDATE` with non-unique conditions, it locks:

    * Existing records that match.
    * The gaps between them.

Example:

```sql
SELECT * FROM employee 
WHERE dept_id BETWEEN 10 AND 20
FOR UPDATE;
```

InnoDB may lock:

* All existing rows with dept_id in [10, 20],
* Gaps between them,
* So that **no new rows** with dept_id in [10, 20] can be inserted by other transactions → prevents phantoms.

For **INSERT**, InnoDB uses **insert intention locks** so that multiple inserts in different parts of the range can still proceed concurrently, as long as they don't conflict in unique key or locked gaps.

---

## 7. Key Things You “Should Know” About InnoDB

Let me summarize the important mental model:

1. **Table = Clustered index on PRIMARY KEY**

    * Choose small, stable, auto-increment PKs when possible.

2. **Secondary indexes store PK as pointer**

    * So PK width affects all indexes.

3. **Primary vs Unique vs Normal**

    * PK = clustered + not null + unique.
    * Unique index = enforce uniqueness, but not clustered.
    * Normal index = for speed, no uniqueness.

4. **Indexes make queries fast** because:

    * They avoid scanning entire table.
    * B+Trees give O(log N) lookups and efficient range scans.
    * Covering indexes can avoid extra lookups.

5. **Partial / filtered index is not supported in MySQL**

    * You cannot index only rows where condition is true.
    * Prefix index works but has limitations (uniqueness, sorting).

6. **Row-level locking**

    * Writes lock only the rows they affect.
    * Non-locking reads use MVCC and don’t block writes.
    * Locking reads (`FOR UPDATE` / `LOCK IN SHARE MODE`) do lock rows.

7. **INSERT acquires locks**

    * Gap/insert-intention locks on index range.
    * Exclusive lock on the new row.
    * Can still be highly concurrent unless unique/PK conflicts or gap locks.

---

If you want, next we can:

* Take a **sample table** (maybe from your leave/attendance DB),
* Design **proper indexes** for its common queries,
* And I can show you **what locks happen step-by-step** for a given scenario (`INSERT`, `UPDATE`, `SELECT FOR UPDATE`) with that real example.
