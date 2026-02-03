# 🔥 STEP 8: INDEXING (INTERVIEW-GRADE, NO FLUFF)

> If Step 7 decides *whether* you get shortlisted,
> **Step 8 decides your level.**

---

## 🧠 Mental Model (Read This First)

**Index ≠ performance magic**

An index is:

* A **separate data structure**
* **Sorted**
* Stores **pointers** to data

👉 Every index speeds up **reads** and slows down **writes**

---

## 8.1 Index Types You MUST Know (Exactly This Much)

| Index Type | Reality                       |
| ---------- | ----------------------------- |
| Primary    | **Clustered index** (special) |
| Secondary  | Non-clustered                 |
| Composite  | Multi-column                  |
| Unique     | Constraint + index            |
| Fulltext   | Rarely used in OLTP           |

Everything else is noise.

---

## 8.2 Clustered Index (InnoDB SUPERPOWER)

🔥 **This alone separates juniors from seniors**

### What it is

* Primary key = clustered index
* **Data IS the index**

```
PRIMARY KEY → full row data
```

### Consequences (INTERVIEW GOLD)

* Table rows are **physically ordered by PK**
* Leaf nodes contain **entire row**
* Only **ONE clustered index per table**

💬 Say this confidently:

> “InnoDB tables are organized by primary key.”

---

## 8.3 Secondary Index (Hidden Cost)

### Structure

```
secondary_index_key → primary_key
```

### Lookup flow

1. Scan secondary index
2. Get PK
3. Go to clustered index
4. Fetch row

🔥 This is called **back-to-table lookup**

💬 Killer interview line:

> “Secondary index access is always two-step unless it’s covering.”

---

## 8.4 Covering Index (VERY IMPORTANT)

### What it means

All required columns exist **inside the index itself**.

```sql
SELECT user_id, email
FROM users
WHERE email = 'x';
```

Index: `(email, user_id)`

✅ No table lookup
✅ Faster
✅ Less IO

💬 Interview line:

> “Covering indexes avoid touching the clustered index.”

---

## 8.5 Composite Index (MOST COMMON MISTAKE)

### Index:

```sql
INDEX idx_user (customer_id, status, created_on)
```

### What works:

```sql
customer_id
customer_id + status
customer_id + status + created_on
```

### What DOES NOT:

```sql
status
created_on
status + created_on
```

🚨 **Leftmost Prefix Rule**

💬 Interview line:

> “Composite indexes work only from the leftmost column.”

---

## 8.6 Column Order in Composite Index (CRITICAL)

### Rule of thumb:

1. Equality conditions first
2. Range conditions last
3. High selectivity earlier

❌ Bad:

```sql
(status, customer_id)
```

✅ Good:

```sql
(customer_id, status)
```

💬 Interview explanation:

> “Once a range condition is used, columns after it can’t be used for filtering.”

---

## 8.7 Cardinality (WHY SOME INDEXES ARE IGNORED)

### Cardinality = uniqueness

| Column  | Cardinality | Index usefulness  |
| ------- | ----------- | ----------------- |
| gender  | Low         | ❌ useless         |
| status  | Low         | ❌ usually ignored |
| user_id | High        | ✅ great           |
| email   | High        | ✅ great           |

💬 Interview line:

> “Low-cardinality indexes are often ignored by the optimizer.”

---

## 8.8 When MySQL DOES NOT Use Index (VERY COMMON)

❌ These break index usage:

```sql
WHERE DATE(created_on) = '2024-01-01'
```

```sql
WHERE status + 1 = 2
```

```sql
WHERE name LIKE '%abc'
```

```sql
SELECT * FROM table
```

💬 Interview line:

> “Functions on indexed columns prevent index usage.”

---

## 8.9 Page Splits & Primary Key Choice

### Why AUTO_INCREMENT PK is best

* Sequential inserts
* Minimal page splits
* Better cache locality

### Why UUID PK is bad

* Random inserts
* Frequent page splits
* Fragmented B-Tree
* Bigger secondary indexes

💬 Interview killer:

> “Large primary keys bloat every secondary index.”

---

## 8.10 Visual: How Indexes Actually Look

![Image](https://jcole.us/blog/files/innodb/20130109/72dpi/B_Tree_Structure.png)

![Image](https://i.sstatic.net/Bousa.png)

![Image](https://media.licdn.com/dms/image/v2/D4D12AQEWnoRDnyuRfw/article-cover_image-shrink_720_1280/article-cover_image-shrink_720_1280/0/1677612015022?e=2147483647\&t=9exx5uDsBS4vUjKdEMaq_rXJIZjlieAIooxrkmT4SNY\&v=beta)

![Image](https://planetscale-images.imgix.net/assets/btrees-and-database-indexes-inner-and-leaf-nodes-VP6Mw5cu.png?auto=compress%2Cformat)

Look at this and **mentally visualize**:

* PK → data
* Secondary → PK → data

Interviewers *feel* this clarity.

---

## 8.11 Interview Questions You WILL Get (With Answers)

### Q: Why are secondary indexes slower?

**Answer:**

> “Because they require an extra lookup to the clustered index.”

---

### Q: Why is UUID bad as PK?

**Answer:**

> “Random inserts cause page splits and inflate all secondary indexes.”

---

### Q: How do you design indexes?

**Answer:**

> “Based on query patterns, not columns.”

---

### Q: What’s a covering index?

**Answer:**

> “An index that satisfies the query without accessing the table.”

---

## ✅ STEP 8 CHECKPOINT (DON’T MOVE UNTIL YES)

You should be able to explain:

✔ Clustered vs secondary index
✔ Back-to-table lookup
✔ Covering index
✔ Composite index order
✔ Cardinality
✔ Why UUID PK is bad

If yes → you’re officially **mid–senior level** in MySQL.