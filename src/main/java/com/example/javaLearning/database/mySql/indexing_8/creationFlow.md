# 🧠 How MySQL Creates an Index (UNIQUE / COMPOSITE) on an Existing Table

> Short answer: **MySQL does NOT magically “attach” an index**
> It must **scan data, validate rules, and build a new structure**.

But *how* it does that depends on:

1. **Index type** (normal / unique / composite)
2. **MySQL version**
3. **DDL algorithm** (`INPLACE` vs `COPY`)
4. **Storage engine** (we assume InnoDB)

---

## 1️⃣ What Actually Happens When You Run

```sql
ALTER TABLE users ADD INDEX idx_email (email);
```

### Internally (InnoDB):

1. **Scan the clustered index** (PK → full rows)
2. Extract `email` values
3. Sort them
4. Build a **new B-Tree structure**
5. Attach index metadata to table

📌 **Table data is NOT rewritten**
📌 Only the **new index tree** is built

---

## 2️⃣ Does MySQL Use a Temp Table?

### ❗ Depends on DDL algorithm

### Old behavior (MySQL ≤ 5.5)

✅ YES — **full table copy**

```
Old table → temp table (with index) → rename
```

🚨 Problems:

* Long table locks
* Huge IO
* Downtime

---

### Modern behavior (MySQL 5.6+ / 8.0)

```sql
ALTER TABLE ... ADD INDEX ...
```

Defaults to:

```
ALGORITHM=INPLACE
LOCK=NONE
```

✅ **No temp table**
✅ **No full table rewrite**
✅ Reads & writes continue (mostly)

💬 Interview line:

> “Modern InnoDB builds secondary indexes online without copying the table.”

---

## 3️⃣ What About UNIQUE Index Creation?

```sql
ALTER TABLE users ADD UNIQUE INDEX uk_email (email);
```

This is where things get interesting 👇

---

### Step-by-step:

1. Scan all existing rows
2. Extract `email`
3. **Sort**
4. **Check for duplicates**
5. Build index ONLY IF validation passes

---

### ❌ If duplicate values exist?

```text
ERROR 1062: Duplicate entry 'x@email.com'
```

🚫 Index creation **fails**
🚫 No partial index
🚫 Table remains unchanged

💬 Interview killer line:

> “UNIQUE index creation validates existing data before the index is committed.”

---

### 🔥 Important nuance

* Validation happens **before index becomes visible**
* No rows are modified
* No automatic cleanup of duplicates

👉 **You must clean data first**

---

## 4️⃣ Composite Index Creation (How It Really Works)

```sql
ALTER TABLE orders 
ADD INDEX idx_comp (customer_id, status, created_on);
```

### Internally:

1. Full scan of clustered index
2. Extract `(customer_id, status, created_on, PK)`
3. Sort **by composite key order**
4. Build B-Tree
5. Attach index

📌 Existing queries **don’t use it until build completes**

---

### Does column order matter at creation time?

✅ YES — sorting order is **exactly index order**

This is why:

```sql
(customer_id, status)
```

and

```sql
(status, customer_id)
```

are **completely different indexes**

---

## 5️⃣ Does Adding an Index Block Writes?

### With INPLACE algorithm:

| Operation | Allowed |
| --------- | ------- |
| SELECT    | ✅       |
| INSERT    | ✅       |
| UPDATE    | ✅       |
| DELETE    | ✅       |

⚠️ But:

* Slight performance degradation
* Extra CPU & IO
* Metadata lock at start/end (very short)

💬 Interview line:

> “Online index creation still adds overhead but avoids downtime.”

---

## 6️⃣ What Happens to NEW Rows While Index Is Building?

🔥 **This is advanced and impressive**

While index is building:

* New rows are written to table
* Changes are captured
* Applied to index before final commit

This ensures:
✅ Index is consistent
✅ No missed rows

💬 Interview brownie:

> “InnoDB tracks concurrent writes during online index creation.”

---

## 7️⃣ What About PRIMARY KEY or UNIQUE CONSTRAINT Changes?

🚨 **Primary key changes are different**

```sql
ALTER TABLE t DROP PRIMARY KEY, ADD PRIMARY KEY(new_col);
```

❌ Requires **table rebuild**
❌ Full data reorganization
❌ Heavy operation

Why?
👉 PK = clustered index
👉 Data itself must be reordered

💬 Interview line:

> “Changing primary key always requires rebuilding the clustered index.”

---

## 8️⃣ Summary Table (MEMORIZE THIS)

| Operation           | Table Copy? | Duplicate Check? | Locking |
| ------------------- | ----------- | ---------------- | ------- |
| Add normal index    | ❌           | ❌                | Minimal |
| Add composite index | ❌           | ❌                | Minimal |
| Add UNIQUE index    | ❌           | ✅                | Minimal |
| Add PRIMARY KEY     | ✅           | ✅                | Heavy   |
| Drop index          | ❌           | ❌                | Minimal |

---

## 9️⃣ Common Production Gotchas (VERY REAL)

🚨 Adding index on huge table:

* Eats IO
* Slows writes
* Can cause replication lag

Best practice:

```sql
SET SESSION lock_wait_timeout = ...
ALTER TABLE ...
```

Or:

* Run during low traffic
* Use pt-online-schema-change (very senior)

---

## 🎯 Interview-Ready One-Liners

Use these **exactly**:

* “InnoDB builds indexes by scanning the clustered index.”
* “UNIQUE index creation validates existing data first.”
* “Primary key changes require table rebuild.”
* “Online DDL avoids table copy but still adds IO cost.”

---

## ✅ Your Doubt — Answered Directly

> ❓ *Does MySQL use a temp table?*
> ➡️ **Modern MySQL: No (INPLACE)**

> ❓ *Does UNIQUE index break on duplicates?*
> ➡️ **Yes — creation fails before commit**

> ❓ *How does composite index creation work later?*
> ➡️ **Full scan → sort by index order → build new B-Tree**