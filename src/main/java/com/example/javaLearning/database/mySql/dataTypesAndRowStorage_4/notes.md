# 🧩 STEP 4: DATA TYPES & ROW STORAGE INTERNALS (INNODB)

This step answers:

* How rows are **physically stored**
* Why some tables are **slow or bloated**
* Why `NULL`, `VARCHAR`, `TEXT`, `INT`, etc. matter
* How data type choices affect **indexes, memory, and IO**

---

## 4.1 First Mental Model (Very Important)

> InnoDB does **not store rows as “objects”**
> It stores rows as **compact binary records inside pages**

Everything is about:

* Bytes
* Offsets
* Pages
* Cache efficiency

---

## 4.2 InnoDB Row Format (High-Level)

A row in InnoDB is stored as:

```
[ Record Header ]
[ NULL Bitmap ]
[ Variable-length field metadata ]
[ Fixed-length fields ]
[ Variable-length fields ]
```

We’ll break this down one by one.

---

## 4.3 Record Header (Hidden, but Always There)

Each row has hidden metadata:

* Row ID (if no PK)
* Transaction ID (who last modified it)
* Rollback pointer (points to undo log)

📌 This is how:

* MVCC works
* Visibility is determined
* Undo chain is followed

You never see these — but they **exist for every row**.

---

## 4.4 NULL Bitmap (Why NULL Isn’t Free)

Every nullable column consumes **1 bit** in the NULL bitmap.

Example:

```sql
status VARCHAR(20) NULL
remarks TEXT NULL
approved_by INT NULL
```

This row has:

* NULL bitmap with at least **3 bits**
* Rounded up to bytes

### Important implications:

* Many nullable columns = wider rows
* Wider rows = fewer rows per page
* Fewer rows per page = more I/O

📌 **NULL is cheap, but not free**

---

## 4.5 Fixed-Length vs Variable-Length Columns

### Fixed-length types:

* INT
* BIGINT
* DATE
* DATETIME

Stored:

* Inline
* Predictable offsets
* Fast access

---

### Variable-length types:

* VARCHAR
* VARBINARY

Stored:

* Length + data
* Inline if small
* Slightly more overhead

---

## 4.6 VARCHAR vs CHAR (Real Difference)

### CHAR(10)

* Always stores 10 characters
* Pads with spaces
* Fast but wasteful if data is short

### VARCHAR(10)

* Stores only actual length
* Saves space
* Slight overhead for length byte(s)

📌 In modern systems:

> **VARCHAR is almost always better**

---

## 4.7 TEXT / BLOB (Very Important)

TEXT and BLOB behave differently.

### Key rule:

> **TEXT/BLOB values are NOT fully stored in the row**

What happens:

* Row stores a **20-byte pointer**
* Actual data stored in **overflow pages**

This is called:

> **Off-page storage**

---

### Implications of TEXT:

* More page reads
* Slower access
* Cannot be fully indexed (prefix only)

This is why:

* TEXT should not be used casually
* Especially in hot tables

---

## 4.8 How Many Rows Fit in a Page (Why This Matters)

Page size = **16 KB**

Example:

* Row size = 200 bytes
* Rows per page ≈ 80

If row size becomes:

* 400 bytes → ~40 rows
* 800 bytes → ~20 rows

📌 Larger rows = more I/O = slower queries

This directly affects:

* Index traversal speed
* Cache efficiency
* Join performance

---

## 4.9 Data Types & Index Size

Indexes store:

* Indexed column values
* Primary key (for secondary indexes)

So:

* Wider column = wider index
* Wider index = fewer entries per page
* Fewer entries = deeper B-Tree

Example:

```sql
INDEX idx_email (email VARCHAR(255))
```

vs

```sql
INDEX idx_email (email VARCHAR(64))
```

The second:

* Smaller index
* Faster traversal
* Better cache usage

📌 **Index width matters a LOT**

---

## 4.10 ENUM (Use with Caution)

ENUM stores:

* Internal integer
* Maps to string value

Pros:

* Compact
* Fast comparisons

Cons:

* Schema change required to add values
* Can break deployments

Senior rule:

> ENUM is fine for **very stable domains only**

---

## 4.11 DECIMAL vs FLOAT (Critical)

### FLOAT / DOUBLE

* Approximate
* Fast
* Not exact

### DECIMAL

* Exact
* Slightly slower
* Correct for money

📌 **Money = DECIMAL always**

---

## 4.12 DATETIME vs TIMESTAMP (Revisited Internally)

### DATETIME

* Stored as literal value
* No timezone conversion

### TIMESTAMP

* Stored in UTC
* Converted on read/write

Internal difference:

* TIMESTAMP is smaller
* But timezone-aware

Senior rule:

> Use TIMESTAMP when timezone matters
> Use DATETIME when you want literal value

---

## 4.13 Hidden Cost of “SELECT *”

Why it’s bad internally:

* Reads all columns
* Fetches TEXT pointers
* Touches more pages
* Breaks covering index usage

Senior habit:

> Always select only what you need

---

## 4.14 Row Overflow & Page Splits (Preview)

Large rows:

* Spill to overflow pages
* Cause more page splits
* Fragment indexes

This becomes very relevant in:

* UUID PKs
* Wide composite indexes

We’ll deep-dive page splits later.

---

## 4.15 Common Senior-Level Schema Mistakes

🚫 Too many nullable columns
🚫 TEXT everywhere
🚫 Oversized VARCHAR
🚫 UUID as PK without reason
🚫 ENUM for frequently changing values

---

## 4.16 Interview-Grade Summary

If asked:

> “Why data types matter in MySQL?”

Answer:

> “Because InnoDB stores rows in fixed-size pages, and data type choices directly affect row width, index size, cache efficiency, and disk I/O, which impacts performance and scalability.”

That’s a **senior answer**.

---

## Final Self-Check (Important)

Answer mentally:

1. Why wide rows slow down queries?
2. Why TEXT is dangerous in hot tables?
3. Why index column width matters?
4. Why SELECT * is bad internally?