# 🧩 STEP 7: INDEXING FUNDAMENTALS & DESIGN (INNODB)

This step answers:

* What an index *really* is
* How MySQL uses indexes internally
* Why some indexes help and others don’t
* How to design indexes like a senior engineer

---

## 7.1 What an Index Really Is (Not the Textbook Definition)

Forget “index = faster search”.

An index is:

> **An ordered data structure that reduces the number of rows the database must examine.**

Everything about indexing is about:

* Reducing row scans
* Reducing page reads
* Improving cache locality

---

## 7.2 Indexes in InnoDB (Reality)

In InnoDB:

* All indexes are **B+Trees**
* Stored as **pages**
* Managed by storage engine

There are only two real categories:

1. **Clustered index** (Primary Key)
2. **Secondary indexes**

No hash indexes (except adaptive internally).

---

## 7.3 Clustered Index (Quick Recap)

Primary key index:

```
PK B+Tree
 └── Leaf pages → FULL 
```

Implications:

* Only ONE clustered index per table
* Table data is physically ordered by PK
* PK lookups are fastest possible access

---

## 7.4 Secondary Index (Quick Recap)

Secondary index:

```
Secondary B+Tree
 └── Leaf pages → (indexed_columns, primary_key)
```

Implications:

* No row duplication
* Requires extra PK lookup
* Wider PK = wider secondary indexes

This is why PK choice matters so much.

---

## 7.5 What Makes an Index “Useful”

An index is useful only if it:

* Significantly reduces rows scanned
* Matches query predicates
* Is selective

This brings us to **cardinality**.

---

## 7.6 Cardinality (Most Important Index Concept)

### Cardinality = number of distinct values

| Column         | Cardinality | Index usefulness |
| -------------- | ----------- | ---------------- |
| id             | Very high   | Excellent        |
| email          | High        | Excellent        |
| status (A/P/R) | Low         | Poor             |
| gender (M/F)   | Very low    | Bad              |

Senior rule:

> **Index high-cardinality columns first**

---

## 7.7 Why Low-Cardinality Indexes Often Fail

Example:

```sql
INDEX (status)
```

Query:

```sql
SELECT * FROM leave_request WHERE status='PENDING';
```

If:

* 70% rows are PENDING

Optimizer thinks:

* Index scan → too many rows
* Table scan → cheaper

Result:
❌ Index ignored

This is expected behavior.

---

## 7.8 Composite Indexes (Critical for Real Systems)

Composite index = **one index, multiple columns**

```sql
INDEX idx_customer_user_date (customer_id, user_id, marked_on)
```

Internally sorted as:

```
customer_id → user_id → marked_on
```

---

## 7.9 Leftmost Prefix Rule (Non-Negotiable)

Index can be used if query uses:

* `customer_id`
* `customer_id, user_id`
* `customer_id, user_id, marked_on`

❌ Cannot efficiently use:

* `user_id`
* `marked_on`

Senior habit:

> Always design composite indexes around query patterns.

---

## 7.10 Equality vs Range (Order Matters)

Given:

```sql
INDEX (customer_id, created_on)
```

Query:

```sql
WHERE customer_id = 10 AND created_on >= '2024-01-01';
```

✔ Good index usage

But:

```sql
WHERE created_on >= '2024-01-01' AND customer_id = 10;
```

Optimizer may still reorder, but rule is:

> **Equality columns first, range columns last**

---

## 7.11 Covering Index (Huge Performance Win)

If index contains **all columns needed by query**:

```sql
SELECT customer_id, user_id
FROM attendance
WHERE customer_id = 10;
```

Index:

```sql
INDEX (customer_id, user_id)
```

Then:

* No PK lookup
* No table access
* Data returned from index alone

EXPLAIN shows:

```
Using index
```

This is one of the biggest optimizations you can do.

---

## 7.12 Indexes and ORDER BY

Index can avoid sorting if:

* ORDER BY matches index order
* Same direction (ASC/DESC)
* No conflicting WHERE clause

Example:

```sql
INDEX (customer_id, created_on)
```

Query:

```sql
WHERE customer_id = 10
ORDER BY created_on DESC;
```

✔ Uses index order
❌ No filesort

---

## 7.13 Indexes and GROUP BY

GROUP BY can use index if:

* Group columns match index prefix
* No additional sorting required

Otherwise:

* Temporary table
* Filesort

---

## 7.14 When Indexes HURT Performance

Indexes are not free.

Each index:

* Consumes disk
* Consumes buffer pool
* Must be updated on INSERT/UPDATE/DELETE

Too many indexes:

* Slow writes
* More lock contention
* Cache pollution

Senior rule:

> Index only for real queries, not “just in case”.

---

## 7.15 Indexes and Writes (Write Amplification)

INSERT into table with:

* PK
* 3 secondary indexes

Results in:

* 4 B+Tree inserts
* 4 undo records
* 4 redo records

This matters in:

* High-write systems
* Kafka consumers
* Bulk imports

---

## 7.16 Common Indexing Mistakes (Senior-Level)

🚫 Indexing low-cardinality columns alone
🚫 Wrong column order in composite index
🚫 Too many indexes
🚫 Missing covering index
🚫 Indexing TEXT without prefix
🚫 Using functions on indexed columns

Example:

```sql
WHERE DATE(created_on) = '2024-01-01'
```

❌ Breaks index usage

---

## 7.17 EXPLAIN (Preview, Deep Dive Later)

You must check:

* `key` → which index used
* `rows` → estimated rows scanned
* `type` → access method
* `Extra` → red flags

We’ll deep-dive EXPLAIN in STEP 9.

---

## 7.18 Interview-Grade Summary

If asked:

> “How do you design indexes in MySQL?”

Answer:

> “I design indexes based on query patterns, prioritize high-cardinality columns, follow the leftmost prefix rule for composite indexes, use covering indexes where possible, and avoid over-indexing to protect write performance.”

That’s a **strong senior answer**.

---

## Final Self-Check (Critical)

Make sure you can answer:

1. Why secondary indexes are slower than PK?
2. Why column order matters in composite indexes?
3. Why low-cardinality indexes are often ignored?
4. Why too many indexes hurt writes?

---