# 🧩 STEP 5: KEYS, CONSTRAINTS & TABLE DESIGN (INNODB)

This step answers:

* How rows are **identified**
* How correctness is **enforced**
* Why PK choice affects **performance**
* When constraints help — and when they hurt

---

## 5.1 Why Keys Exist (Not Just “Because SQL”)

A key exists to answer **three fundamental questions**:

1. How do I **uniquely identify** a row?
2. How do I **prevent duplicates**?
3. How do I **relate rows across tables**?

If these aren’t answered cleanly → bugs + performance pain.

---

## 5.2 PRIMARY KEY (PK) — THE MOST IMPORTANT DECISION

### What a Primary Key Really Is

A PK is:

* A **logical identifier**
* A **physical ordering mechanism** (InnoDB)
* A **performance decision**

In InnoDB:

> **Primary key = clustered index**

That means:

* Table data is stored **sorted by PK**
* PK choice affects:

    * Insert speed
    * Index size
    * Cache locality
    * Page splits

---

## 5.3 Properties of a Good Primary Key

A good PK should be:

| Property    | Why                      |
| ----------- | ------------------------ |
| Immutable   | Never changes            |
| Short       | Smaller indexes          |
| Sequential  | Fewer page splits        |
| Meaningless | Avoids business coupling |

---

## 5.4 AUTO_INCREMENT vs UUID (Senior Topic)

### AUTO_INCREMENT (Recommended)

✔ Sequential
✔ Cache-friendly
✔ Minimal page splits

### UUID (Dangerous by default)

❌ Random inserts
❌ Page fragmentation
❌ Larger indexes

If UUID is required:

* Use **UUIDv7 / ordered UUID**
* Or store UUID as **BINARY(16)**

Senior rule:

> Default to AUTO_INCREMENT unless you have a **clear distributed reason**

---

## 5.5 Surrogate Key vs Natural Key

### Natural Key

Example:

```text
email
employee_code
```

❌ Can change
❌ Business-coupled

### Surrogate Key

```sql
id BIGINT AUTO_INCREMENT
```

✔ Stable
✔ Simple
✔ Fast

Senior rule:

> **Use surrogate PKs + enforce natural keys via UNIQUE constraints**

---

## 5.6 UNIQUE KEY (Correctness Without Clustering)

### What UNIQUE Does

* Prevents duplicate values
* Creates a secondary index
* Does **not** affect row order

Example:

```sql
UNIQUE (email)
```

Internally:

```
(email, primary_key)
```

---

### Composite UNIQUE Key

Example:

```sql
UNIQUE (customer_id, user_id)
```

Guarantees:

* Same user cannot appear twice in same customer

Used heavily in:

* Mapping tables
* Junction tables
* Business rules

---

## 5.7 FOREIGN KEY (FK) — POWERFUL BUT CONTROVERSIAL

### What FK Does

* Enforces referential integrity
* Prevents orphan rows
* Automatically validates on INSERT/UPDATE/DELETE

Example:

```sql
FOREIGN KEY (user_id) REFERENCES users(id)
```

---

### FK Costs (Very Important)

FKs:

* Add overhead on writes
* Add locks
* Can slow deletes/updates

In high-scale systems:

* Many teams enforce FK **at application level**
* Use DB FK only for **core integrity**

Senior rule:

> Use FK where correctness > write throughput

---

## 5.8 ON DELETE / ON UPDATE Rules

Options:

* CASCADE
* SET NULL
* RESTRICT

Example:

```sql
ON DELETE CASCADE
```

Danger:

* Large cascades
* Unexpected mass deletes

Senior habit:

> Be extremely cautious with CASCADE in large tables

---

## 5.9 Composite Primary Keys (When to Use)

Example:

```sql
PRIMARY KEY (order_id, product_id)
```

Pros:

* Natural uniqueness
* No surrogate needed

Cons:

* Large PK
* Secondary indexes grow
* Joins slower

Senior rule:

> Composite PKs are fine for **junction tables**, not core entities

---

## 5.10 Indexes vs Constraints (Subtle but Important)

| Feature | Purpose               |
| ------- | --------------------- |
| PRIMARY | Identity + clustering |
| UNIQUE  | Uniqueness            |
| INDEX   | Performance           |
| FK      | Integrity             |

Constraints exist for **correctness**
Indexes exist for **performance**

Sometimes both overlap — but intent matters.

---

## 5.11 NULL + UNIQUE (Classic Trap)

```sql
UNIQUE (email)
```

Allows:

```
NULL
NULL
NULL
```

Why?

* NULL ≠ NULL (unknown)

If you want:

> “Only one NULL allowed”

You must:

* Use NOT NULL
* Or handle at app level

---

## 5.12 Covering Keys in Real Design

Good design:

* PK = surrogate
* UNIQUE = business rule
* INDEX = query paths

Example:

```sql
PRIMARY KEY (id)
UNIQUE (email)
INDEX (customer_id, created_on)
```

Each serves a **different purpose**.

---

## 5.13 Keys & Locking (Preview)

Important:

* PK lookups → precise row locks
* Non-indexed updates → range locks
* Missing index → table scan + lock escalation

This directly affects:

* Deadlocks
* Write contention

---

## 5.14 Common Senior-Level Mistakes

🚫 Using UUID PK without ordering
🚫 Using natural key as PK
🚫 Missing UNIQUE on business constraints
🚫 Too many FKs on hot tables
🚫 Composite PKs everywhere

---

## 5.15 Interview-Grade Summary

If asked:

> “How do you design keys in MySQL?”

Answer:

> “I use a surrogate primary key for clustering and performance, enforce business rules with unique constraints, add indexes for access patterns, and use foreign keys selectively based on consistency and write-throughput trade-offs.”

That answer is **very strong**.

---

## Self-Check Before Moving On

Answer mentally:

1. Why PK choice affects insert speed?
2. Why surrogate PKs are preferred?
3. When are composite PKs acceptable?
4. Why FK may hurt performance?

---

### Next Step

If you’re ready:
👉 **Say: “Ready for STEP 6”**

STEP 6 = **Normalization & Schema Design (1NF → 3NF → Denormalization in real systems)**

This is a big one — but now you’re perfectly prepared.
