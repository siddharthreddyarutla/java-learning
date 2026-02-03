## 0.1 Why Databases Exist (The Real Reason)

Before databases, applications stored data in:

* Files
* Memory
* Flat text formats

### Problems with that:

* ❌ Data lost on crash
* ❌ Multiple users overwrite each other
* ❌ No guarantee data is correct
* ❌ Very slow search at scale

👉 **Databases exist to solve these problems systematically**

---

## 0.2 Core Responsibilities of a Database

A real database must guarantee **ALL of these**:

### 1️⃣ Persistence

* Data survives:

    * App crash
    * Server restart
    * Power failure (eventually)

Example:

> User applies leave → system crashes → data must still exist

---

### 2️⃣ Correctness (Consistency)

* Data must obey rules:

    * No duplicate primary keys
    * No orphan foreign keys
    * No invalid states

Example:

> Leave cannot be approved if it doesn’t exist

---

### 3️⃣ Concurrency

* Multiple users read/write **at the same time**
* No corruption
* No lost updates

Example:

> Two managers approving same leave request

---

### 4️⃣ Performance

* Fast reads
* Reasonable writes
* Predictable under load

Example:

> Attendance dashboard loading in <200ms with millions of rows

---

### 5️⃣ Recoverability

* If system crashes:

    * No partial writes
    * No corrupted data
    * Automatic recovery

This leads to:
👉 logs, transactions, WAL, redo/undo (we’ll get there)

---

## 0.3 What a Database Is *NOT*

Important for interviews.

❌ A database is NOT:

* Just tables
* Just SQL
* Just storage

✅ A database **is a system**:

* Algorithms
* Data structures
* Concurrency control
* Failure handling

Interview-ready statement:

> “A database is a transactional, concurrent, persistent data management system.”

---

## 0.4 Why SQL Databases Still Dominate (Even in 2025)

Despite NoSQL, Redis, Mongo, etc.

### Because SQL databases provide:

* Strong consistency
* Declarative querying
* Mature optimization
* Proven correctness

Most companies:

* Use **MySQL/Postgres** as source of truth
* Use NoSQL as **derived / cached / specialized** storage

👉 MySQL is often **the backbone**, not the UI cache.

---

## 0.5 The Fundamental Trade-offs (Very Important)

Databases always balance:

| Aspect        | Trade-off      |
| ------------- | -------------- |
| Speed         | vs Consistency |
| Reads         | vs Writes      |
| Simplicity    | vs Flexibility |
| Normalization | vs Performance |

Everything you’ll learn later (indexes, MVCC, denormalization)
👉 is about **managing these trade-offs intelligently**.

---

## 0.6 Mental Model You Should Keep Forever

Think of a database as:

> “A highly concurrent, crash-safe, optimized data structure engine that happens to speak SQL.”

If you think this way:

* Indexes = data structures
* Transactions = concurrency protocols
* Logs = safety nets
* Optimizer = query compiler

---

## 0.7 How Interviewers Expect You to Think at Senior Level

If asked:

> “What is a database?”

Junior answer ❌:

> “It stores data in tables.”

Senior answer ✅:

> “A database guarantees correct, concurrent, durable access to data while optimizing read and write performance under failures.”

---

## 0.8 Why STEP 0 Matters for MySQL Mastery

Because:

* Normalization → correctness
* Indexes → performance
* Transactions → concurrency
* Logs → durability
* Replication → scalability

Nothing in MySQL is random.
Everything exists because of **Step 0 problems**.

---

## Quick Check (For You)

Answer these **in your own words** (mentally or here):

1. Why can’t we just use files instead of databases?
2. Which is harder for databases: concurrency or persistence?
3. Why is performance always a trade-off?

You don’t need to write essays — clarity is what matters.
