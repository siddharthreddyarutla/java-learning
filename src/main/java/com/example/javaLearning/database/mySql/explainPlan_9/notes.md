# 🔥 STEP 9: EXPLAIN PLAN (READING QUERIES LIKE A STORY)

> If indexing is **building weapons**,
> **EXPLAIN is knowing when and how they’re used.**

---

## 🧠 First: What EXPLAIN Really Is

`EXPLAIN` shows **how MySQL plans to execute your query**, not the result.

It answers:

* Which table is read first?
* Which index is chosen?
* How many rows are scanned?
* Where performance is wasted?

💬 Interview line:

> “I don’t guess performance issues — I read the execution plan.”

---

## 9.1 Basic Syntax (Enough for Interviews)

```sql
EXPLAIN SELECT ...
```

or

```sql
EXPLAIN ANALYZE SELECT ...
```

📌 `EXPLAIN ANALYZE` = **actual runtime**, but basic `EXPLAIN` is usually enough in interviews.

---

## 9.2 The EXPLAIN Columns That ACTUALLY MATTER

Ignore the rest. Focus on **only these**.

| Column      | Why it matters                     |
| ----------- | ---------------------------------- |
| id          | Execution order                    |
| select_type | Query type                         |
| table       | Table accessed                     |
| type        | **Access method (MOST IMPORTANT)** |
| key         | Index used                         |
| rows        | Estimated rows scanned             |
| Extra       | Red flags                          |

---

## 9.3 `type` Column (MAKE OR BREAK)

This tells you **how good or bad** the query is.

| type   | Meaning          | Verdict  |
| ------ | ---------------- | -------- |
| system | Single row       | 🔥 best  |
| const  | 1 row            | 🔥       |
| eq_ref | PK/unique join   | ✅        |
| ref    | Non-unique index | ✅        |
| range  | Range scan       | ⚠️       |
| index  | Full index scan  | ❌        |
| ALL    | Full table scan  | 🚨 worst |

💬 Interview line:

> “I always aim to avoid ALL and index scans on large tables.”

---

## 9.4 `key` Column (Which Index Was Picked)

### Important truths:

* Optimizer may **ignore your index**
* Using *some* index ≠ good plan

```text
key = idx_user_email
```

Ask:

* Is it the right index?
* Is it composite?
* Is order correct?

💬 Interview line:

> “Index existence doesn’t guarantee index usage.”

---

## 9.5 `rows` Column (Cost Indicator)

This is **estimated rows scanned**, not returned.

| rows   | Meaning     |
| ------ | ----------- |
| 1–10   | Excellent   |
| 100–1K | OK          |
| 10K+   | Investigate |
| 100K+  | 🚨 problem  |

💬 Interview line:

> “I optimize to reduce rows scanned, not rows returned.”

---

## 9.6 `Extra` Column (RED FLAGS 🚨)

This is where performance issues hide.

| Extra                         | Meaning        |
| ----------------------------- | -------------- |
| Using where                   | Normal         |
| Using index                   | Covering index |
| Using temporary               | 🚨 temp table  |
| Using filesort                | 🚨 disk sort   |
| Range checked for each record | 🚨 terrible    |

🔥 Worst combo:

```text
Using temporary; Using filesort
```

💬 Interview line:

> “Temporary tables and filesort usually indicate missing or wrong indexes.”

---

## 9.7 Reading EXPLAIN as a STORY (IMPORTANT)

### Example Query

```sql
SELECT *
FROM orders
WHERE customer_id = 101
  AND status = 'PAID'
ORDER BY created_on DESC;
```

### Bad EXPLAIN

* type: ALL
* rows: 500k
* Extra: Using filesort

### What you say:

> “MySQL scans the whole table, filters rows, then sorts on disk.”

### Fix:

```sql
INDEX (customer_id, status, created_on)
```

### Good EXPLAIN

* type: ref
* rows: 12
* Extra: Using index

💬 Final line:

> “Now MySQL filters and sorts directly using the index.”

---

## 9.8 Common Interview Traps

### ❌ “Index exists, but EXPLAIN shows ALL”

Reasons:

* Low cardinality
* Wrong column order
* Function on column
* Too many rows anyway

💬 Interview-safe answer:

> “The optimizer decided index access was more expensive than a full scan.”

---

## 9.9 EXPLAIN + JOIN (VERY IMPORTANT)

### Join EXPLAIN rule:

* MySQL reads tables **top to bottom**
* Each row joins with next table

Bad:

```text
Large table first
```

Good:

```text
Small filtered table first
```

💬 Interview line:

> “Join order matters — especially in nested loop joins.”

---

## 9.10 EXPLAIN ANALYZE (BONUS POINTS)

```sql
EXPLAIN ANALYZE SELECT ...
```

Shows:

* Actual execution time
* Real row counts

💬 Interview brownie:

> “I use EXPLAIN ANALYZE when estimates don’t match reality.”

---

## 9.11 Visual: How EXPLAIN Fits Indexing

![Image](https://dev.mysql.com/doc/workbench/en/images/wb-visual-explain-example-sakila.png)

![Image](https://miro.medium.com/1%2AEowuEFcORu-AEW0CBUQXPA.png)

![Image](https://planetscale.com/assets/blog/content/mysql-explains/mysql-explain-analyze.png)

Visualize:

* Optimizer → plan
* Index choice → cost
* Execution → rows

---

## ✅ STEP 9 CHECKPOINT

You must confidently explain:

✔ What `type` means
✔ Why `ALL` is bad
✔ What `rows` represents
✔ Why `Using filesort` is dangerous
✔ How index order fixes EXPLAIN

If yes → **you are interview-strong**