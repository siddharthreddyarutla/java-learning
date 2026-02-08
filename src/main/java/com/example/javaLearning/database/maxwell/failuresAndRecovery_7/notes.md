# 🧠 STEP 6 — Failure Scenarios & Recovery (Interview-Critical)

The hidden interview question here is:

> **“What breaks in real life, and how does your system recover?”**

If you answer this cleanly, you’re clearly senior+.

---

## 6.1 First Principle (lock this in)

> **Maxwell is durable, Kafka is durable, consumers are not.**

So failures are expected — correctness comes from **replay + idempotency**.

---

## 6.2 Failure #1 — Maxwell Process Crashes

### What happens?

* Maxwell may have:

    * already read binlog
    * already sent event to Kafka
    * NOT yet saved binlog position

### Result

* On restart → same binlog events replayed
* Kafka gets **duplicate events**

### Why this is SAFE

* At-least-once delivery
* Ordering preserved
* Consumers must be idempotent

📌 Interview line:

> *“Maxwell favors durability over exactly-once guarantees.”*

---

## 6.3 Failure #2 — Kafka Is Down

### What happens?

* Maxwell cannot publish
* It **stops reading further binlogs**
* Binlog position is NOT advanced

### After Kafka recovers

* Maxwell resumes
* No data loss (if binlogs retained)

📌 Interview insight:

> *“Kafka backpressure naturally pauses CDC consumption.”*

---

## 6.4 Failure #3 — Binlogs Are Purged (MOST DANGEROUS)

### Scenario

* Maxwell is down
* MySQL purges old binlogs
* Maxwell restarts

### Result

❌ **Data loss — unrecoverable**

### Prevention

* Set binlog retention > worst-case downtime
* Monitor Maxwell lag

📌 Interview killer line:

> *“Binlog retention defines the recovery window of CDC systems.”*

---

## 6.5 Failure #4 — Consumer Crash

### What happens?

* Kafka replays from last committed offset
* Same events reprocessed

### Correct handling

* Idempotent sink operations
* Dedup for side-effects

📌 Interview truth:

> *“Consumer crashes are normal; correctness depends on replay safety.”*

---

## 6.6 Failure #5 — Partial Consumer Success (VERY COMMON)

Example:

* Notification sent
* Consumer crashes
* Offset not committed

Result:

* Notification sent AGAIN

### Correct solution

* Dedup store
* Or Outbox pattern

📌 Interview line:

> *“Offsets track reads, not business completion.”*

---

## 6.7 Failure #6 — Schema Change Mid-Stream

### Example

```sql
ALTER TABLE users ADD COLUMN age;
```

### What Maxwell does

* Emits schema change
* Then row events include new column

### Consumer responsibility

* Be forward-compatible
* Ignore unknown fields
* Avoid strict deserialization

📌 Interview insight:

> *“CDC consumers must be schema-tolerant.”*

---

## 6.8 Failure #7 — Ordering Breaks (Self-Inflicted)

Ordering breaks ONLY if:
❌ Kafka key ≠ primary key
❌ Parallel processing of same key
❌ Multiple consumers writing same entity

📌 Interview warning:

> Ordering bugs are configuration bugs, not CDC bugs.

---

## 6.9 Recovery Strategies (Interview Checklist)

| Scenario              | Recovery          |
| --------------------- | ----------------- |
| Maxwell crash         | Restart           |
| Consumer bug          | Reset offsets     |
| Data rebuild          | Bootstrap         |
| Downstream corruption | Replay from Kafka |
| Lost binlogs          | Full re-bootstrap |

📌 Interview insight:

> Replayability is the biggest advantage of CDC.

---

## 6.10 One Perfect Interview Answer (Memorize)

If interviewer asks:

> *“How does your system recover from failures?”*

Say:

> “Maxwell provides at-least-once CDC with durable binlog offsets. Kafka allows replay, and consumers are designed to be idempotent. As long as binlogs are retained, we can recover from crashes by replaying events.”

🔥 That answer checks **all boxes**.

---

## Lock-in Mental Model 🧠

> **CDC systems don’t prevent failures — they make failures recoverable.**

If this makes sense, you’re thinking like a production engineer.
