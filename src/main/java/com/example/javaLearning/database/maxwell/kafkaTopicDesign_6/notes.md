Great catch 👌 — you’re right, we talked about topic routing *inside* system design, but we didn’t do a **clean, interview-ready STEP** just for it.

Let’s fix that properly.

---

# 🧠 STEP 6 (補完) — Kafka Topic Design with Maxwell (Interview-Specific)

This step is **frequently asked** because it tests:

* CDC understanding
* Kafka fundamentals
* Ordering & scaling trade-offs

Interviewers often phrase it as:

> “How would you design Kafka topics for Maxwell?”

---

## 6.1 First principle (say this first)

> **Topic design is about isolation, scalability, and ordering — not convenience.**

If you start with this, you sound senior.

---

## 6.2 What Maxwell emits (baseline reminder)

Every event already contains:

* `database`
* `table`
* `primary key`
* `event type`

So topic design is **not about losing information**, it’s about **consumer efficiency**.

---

## 6.3 Default Maxwell Topic Strategy

### ✅ One topic per database (most common)

Example:

```
db_users
db_orders
```

**Why teams choose this:**

* Simple setup
* Fewer topics
* Easy onboarding

**Trade-off:**

* Consumers must filter by table

📌 Interview line:

> “Database-level topics are a good default unless scale demands finer isolation.”

---

## 6.4 One Topic per Table (Very Common Follow-up)

Example:

```
users.employee
users.department
orders.payment
```

### Pros

* Clean separation
* Simple consumers
* Independent scaling

### Cons

* Topic explosion
* Higher ops overhead

📌 Interview maturity:

> “Table-level topics are great for hot or business-critical tables.”

---

## 6.5 Hybrid Strategy (Senior Answer)

This is what **real systems** do.

Example:

```
db_users           → low-traffic tables
users_employee     → hot table
users_attendance   → hot table
```

📌 Interview gold:

> “We start coarse and split hot tables into their own topics as traffic grows.”

---

## 6.6 Partitioning Strategy (MOST IMPORTANT PART)

### Golden rule

> **Kafka key = Primary Key**

Why:

* Same row → same partition
* Preserves per-entity ordering
* Enables idempotent consumers

Example:

```
key = employee_id
```

📌 Red-flag answer:

> “Random partitioning for load balancing”

🚩 Immediate fail.

---

## 6.7 Partition Count — How Many?

Interview-safe answer (don’t give exact numbers):

> “We choose partitions based on expected write throughput and consumer parallelism, and we can increase partitions later if needed.”

📌 Bonus:

> Partition count does **not** affect ordering for same key.

---

## 6.8 Multiple Databases with One Maxwell

### Supported and common

Maxwell can:

* Read multiple databases
* Publish to:

    * Separate topics
    * Or shared topics with DB field

Example:

```
db_hr
db_payroll
```

Interview clarity:

> “We avoid mixing unrelated databases unless consumers explicitly need it.”

---

## 6.9 What NOT to Do (Interview Traps)

❌ One topic for everything in the company
❌ Partition by table name
❌ Partition by timestamp
❌ Use round-robin producer
❌ Change partition key later casually

---

## 6.10 Ordering Guarantees (Tie-back)

Ordering is guaranteed:

* **Per partition**
* **Per primary key**

Not guaranteed:

* Across tables
* Across different primary keys

📌 Interview line:

> “CDC systems guarantee ordering per entity, not globally.”

---

## 6.11 Replay & Topic Design

Good topic design enables:

* Replay one table
* Pause one consumer
* Rebuild one index

Bad topic design:

* Forces full replay
* Couples unrelated consumers

---

## 6.12 Interview-Perfect Summary (Memorize)

> “With Maxwell, we typically start with one topic per database and partition by primary key to preserve ordering. For high-traffic or critical tables, we split into table-level topics. Topic design is driven by isolation, scalability, and replay requirements.”

🔥 That answer is **exactly what interviewers want**.

---

## Lock-in Mental Model 🧠

> **Topic boundaries define failure and replay boundaries.**

If you remember this, you’ll design Kafka topics correctly every time.
