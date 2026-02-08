# 🧠 STEP 4 — Bootstrap Mode (Initial Load & Backfill)

### The Interview Question Behind This Step

> “CDC starts from *now*. What about existing data?”

If you answer this well, you score serious points.

---

## 4.1 The Core Problem Bootstrap Solves

Scenario:

* MySQL already has **millions of rows**
* Kafka topic is **empty**
* Maxwell starts reading binlog **from current position**

👉 Result:

* Only *future* changes flow
* Historical data is missing

❌ Not acceptable for:

* Search indexes
* Analytics
* Cache warm-up

---

## 4.2 What “Bootstrap” Means in CDC

**Bootstrap = controlled initial snapshot of existing data**, emitted as events.

Important:

* Data is emitted **as events**, not dumps
* Downstream systems don’t need special logic

📌 Interview line:

> *“Bootstrap converts existing rows into CDC events so downstream systems can be built uniformly.”*

---

## 4.3 How Maxwell Bootstrap Works (Conceptual Flow)

```
Existing MySQL Tables
        ↓
(Maxwell SELECTs data ONCE)
        ↓
Emits "bootstrap" events
        ↓
Kafka
        ↓
Consumers build initial state
        ↓
Maxwell switches to binlog streaming
```

⚠️ Note:

* This is the **only time Maxwell queries tables**
* Normal CDC = **binlog only**

📌 Interview nuance:

> Bootstrap is optional and explicit — never implicit.

---

## 4.4 Bootstrap Event Characteristics

Bootstrap events look like normal events, except:

```json
"type": "bootstrap"
```

* `data` → full row
* No `old` field
* Same primary key
* Same topic

👉 Consumers **don’t need separate logic**.

---

## 4.5 Important Ordering Guarantee (INTERVIEW TRAP)

### Key guarantee:

> **Bootstrap events are emitted BEFORE binlog events for the same rows.**

How?

* Maxwell tracks binlog position
* Snapshot happens **up to that position**
* Then streaming resumes

📌 Interview-safe explanation:

> *“Bootstrap establishes a consistent starting point before live CDC begins.”*

---

## 4.6 Types of Bootstrap

You don’t need to memorize commands — just concepts.

### 1️⃣ Full Database Bootstrap

* All tables
* Rare in prod
* Heavy

### 2️⃣ Table-Level Bootstrap

* Selected tables
* Most common

### 3️⃣ Async Bootstrap

* CDC continues
* Bootstrap happens in parallel
* Requires careful consumer handling

📌 Interview insight:

> Async bootstrap trades consistency for availability.

---

## 4.7 Bootstrap + Duplicates (Very Important)

Yes — **duplicates WILL happen**.

Example:

* Row updated during bootstrap
* Update also appears in binlog

Solution:

* Idempotent consumers
* Upsert semantics

📌 Interview line:

> *“Bootstrap correctness relies on consumer idempotency, not producer guarantees.”*

---

## 4.8 When Bootstrap Is Used in Real Life

✔ New Kafka consumer group
✔ Rebuilding Elasticsearch index
✔ Migrating analytics pipeline
✔ Disaster recovery
✔ Replaying history

---

## 4.9 When NOT to Bootstrap

❌ Very large tables without throttling
❌ Hot production DB without replica
❌ If historical data isn’t needed

Then:

* Start CDC from “now”

---

## 4.10 Interview One-Liner (Memorize This)

> **“Bootstrap allows Maxwell to emit existing database rows as CDC events so downstream systems can be built before live binlog streaming begins.”**

If you say this calmly, interviewer *knows* you’ve done real CDC thinking.

---

## Common Follow-Up Interview Questions (Now You’re Ready)

**Q: Does bootstrap block CDC?**
👉 Depends — sync vs async

**Q: Is bootstrap transactional?**
👉 No — relies on binlog position consistency

**Q: Can bootstrap be repeated?**
👉 Yes, for rebuilds

---

## Lock-in Mental Model 🧠

> **Bootstrap = one-time snapshot as events, CDC = continuous stream**