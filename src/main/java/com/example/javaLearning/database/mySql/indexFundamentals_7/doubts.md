These are **excellent internals questions** — you’re basically asking *“show me the machine, not the brochure”*.
I’ll answer **slowly, visually (with text diagrams), and precisely**, and I’ll correct a couple of subtle misconceptions on the way.

We’ll go in this order:

1. Are B+Trees stored in pages? (short answer + why)
2. What does a B+Tree actually look like (root / internal / leaf)
3. How clustered vs secondary index trees differ
4. Where secondary indexes live (pages & tablespace)
5. How a query works when WHERE uses a **non-indexed column**
6. Why INSERT/UPDATE must touch **all secondary indexes**
7. What happens if you update a column that is **not indexed**

---

# 1️⃣ Are B+Trees stored in pages?

✅ **YES — 100%**

In InnoDB:

* Every B+Tree node = **one or more 16KB pages**
* Root, internal nodes, and leaf nodes are **all pages**
* Pages live inside the **tablespace (.ibd)**

Important rule:

> **InnoDB never reads “nodes” — it reads pages**

---

# 2️⃣ What does a B+Tree ACTUALLY look like?

Let’s build a **realistic mental picture**.

Assume:

* Primary Key = `id`
* Values: 10, 20, 30, 40, 50, 60

---

## 2.1 Logical B+Tree (Conceptual)

```
                [ 30 | 50 ]        ← ROOT PAGE
               /      |      \
        [10 | 20]  [30 | 40]  [50 | 60]
           ↑           ↑           ↑
        LEAF PAGES (data lives here)
```

---

## 2.2 Physical Reality (Pages)

Each box above is a **page**:

```
Page #100  (ROOT)
Page #201  (LEAF)
Page #202  (LEAF)
Page #203  (LEAF)
```

Each page contains:

* Page header
* Pointers to child pages
* Sorted key values

---

## 2.3 What “leaf page” means (IMPORTANT)

**Leaf page = bottom level of B+Tree**

* In **clustered index**:

    * Leaf page contains **FULL ROW DATA**
* In **secondary index**:

    * Leaf page contains **(indexed columns + PK)**

Leaf pages are:

* Doubly linked (next/prev)
* Used for range scans

---

# 3️⃣ Clustered Index vs Secondary Index (Trees are Separate)

This is critical.

---

## 3.1 Clustered Index (Primary Key Tree)

```
PRIMARY KEY B+TREE
Root Page
  ↓
Internal Pages
  ↓
LEAF PAGES → FULL ROW
```

Leaf page example:

```
[id=10 | status=PENDING | user_id=5 | date=2024-01-01]
[id=11 | status=APPROVED | user_id=6 | date=2024-01-02]
```

👉 **This is the table itself**

---

## 3.2 Secondary Index (Separate Tree)

Example:

```sql
INDEX idx_user_date (user_id, marked_on)
```

```
SECONDARY B+TREE
Root Page
  ↓
Internal Pages
  ↓
LEAF PAGES → (user_id, marked_on, PRIMARY_KEY)
```

Leaf page example:

```
(5, '2024-01-01', PK=10)
(5, '2024-01-02', PK=11)
```

❌ No full row here
✔ Only references to clustered index

---

## 3.3 Are secondary indexes stored in other pages?

✅ YES

* Each index = **its own B+Tree**
* Each tree = its own set of pages
* All pages live in the same tablespace file

So:

```
.ibd file
 ├── clustered index pages
 ├── secondary index A pages
 ├── secondary index B pages
```

---

# 4️⃣ How does a query work if WHERE uses a NON-indexed column?

Example:

```sql
SELECT * FROM leave_request WHERE status = 'PENDING';
```

Assume:

* `status` is NOT indexed

---

## 4.1 What the optimizer decides

Optimizer sees:

* No usable index
* Only option = full table scan

---

## 4.2 How full table scan works internally

1. Executor asks InnoDB:

   ```
   give me next row
   ```
2. InnoDB:

    * Reads **clustered index leaf pages sequentially**
    * Row-by-row:

        * Apply WHERE condition
        * Return matching rows

Important:

* It scans **leaf pages of clustered index**
* Not random disk access
* Sequential I/O (still expensive at scale)

📌 This is why:

* Full scans aren’t always catastrophic
* But dangerous on large tables

---

# 5️⃣ Why INSERT/UPDATE must update ALL secondary B+Trees

This is a **very important internal rule**.

---

## 5.1 Why secondary indexes exist

Secondary index exists to answer queries like:

```sql
WHERE user_id = 5
```

So the index must always reflect:

* Current column values
* Correct PK references

---

## 5.2 INSERT case

```sql
INSERT INTO attendance(id, user_id, marked_on)
VALUES (101, 5, '2024-01-01');
```

InnoDB must:

1. Insert row into **clustered index**
2. Insert entry into **each secondary index**

Example secondary index entry:

```
(5, '2024-01-01', 101)
```

If it didn’t:

* Index would be incorrect
* Queries would return wrong results

---

## 5.3 UPDATE case — indexed column

```sql
UPDATE attendance
SET user_id = 6
WHERE id = 101;
```

InnoDB must:

1. Remove old secondary index entry:

   ```
   (5, '2024-01-01', 101)
   ```
2. Insert new entry:

   ```
   (6, '2024-01-01', 101)
   ```

This is **B+Tree delete + insert**.

---

# 6️⃣ What if updated column is NOT indexed?

This is where your confusion was — good catch.

Example:

```sql
UPDATE attendance
SET remarks = 'Late'
WHERE id = 101;
```

Assume:

* `remarks` is NOT indexed

### What happens:

✔ Clustered index row updated
✔ Undo log written
✔ Redo log written

❌ **NO secondary index updates**

Why?

* Secondary indexes don’t store `remarks`
* Nothing in index structure changes

👉 Only indexes that include the changed column are updated.

---

# 7️⃣ Why PK UPDATE is the worst case

If you update PK:

```sql
UPDATE attendance SET id = 999 WHERE id = 101;
```

Then:

* Row must move in clustered index
* ALL secondary index entries must be updated
* Massive cost

This is why:

> **Primary keys should never change**

---

# 8️⃣ One Diagram That Ties It All Together

```
INSERT / UPDATE
     ↓
Clustered Index B+Tree (FULL ROW)
     ↓
For EACH secondary index:
     └── Update (indexed cols + PK)
```

Indexes are **not copies of data**
They are **search structures that must stay consistent**

---

# 9️⃣ Final Mental Model (Lock This In)

* ✅ B+Trees are stored in pages
* ✅ Root / internal / leaf = different page levels
* ✅ Clustered index leaf = row data
* ✅ Secondary index leaf = (indexed cols + PK)
* ✅ Full scan = sequential scan of clustered leaf pages
* ✅ UPDATE touches only indexes that include changed columns

---

# 10️⃣ Interview-Grade One-Liners

**Q: Are B+Trees stored in pages?**

> Yes, each B+Tree node is stored in fixed-size InnoDB pages.

**Q: Why do inserts update all secondary indexes?**

> Because each secondary index must maintain correct mappings from indexed columns to the primary key.

**Q: What happens if updated column is not indexed?**

> Only the clustered index row is updated; secondary indexes remain unchanged.

---