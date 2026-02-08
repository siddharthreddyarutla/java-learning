# 🧠 STEP 8 — Maxwell Trick Questions & Interview Gotchas

These are **real questions** asked in senior interviews.

---

## 8.1 “Does Maxwell guarantee exactly-once delivery?”

### ❌ Wrong answer

> “Yes, Kafka guarantees exactly-once”

🚩 Immediate red flag.

### ✅ Correct answer

> “No. Maxwell provides at-least-once delivery. Correctness is achieved via idempotent consumers.”

📌 Follow-up bonus:

> “Kafka exactly-once applies only within Kafka Streams transactions, not external systems.”

---

## 8.2 “Can Maxwell miss data?”

### ❌ Wrong

> “No, it’s reliable”

Too naive.

### ✅ Correct

> “Yes, if MySQL binlogs are purged before Maxwell reads them. Otherwise it’s safe.”

📌 Interview gold:

> “Binlog retention defines the recovery window.”

---

## 8.3 “Why not use triggers instead of Maxwell?”

### ❌ Weak

> “Triggers are bad”

### ✅ Strong

> “Triggers couple business logic to the database, add runtime overhead, and are hard to evolve. CDC keeps DB passive.”

---

## 8.4 “Can Maxwell handle schema changes safely?”

### ❌ Wrong

> “Yes, automatically”

### ✅ Correct

> “Maxwell emits schema change events but does not manage compatibility. Consumers must be forward-compatible.”

📌 Bonus:

> “For heavy schema evolution, Debezium is safer.”

---

## 8.5 “What happens if Maxwell crashes in the middle of a transaction?”

### ❌ Wrong

> “Partial data”

### ✅ Correct

> “Binlogs only contain committed transactions. Maxwell never sees partial data.”

---

## 8.6 “How do you replay only one table?”

### ❌ Weak

> “Reset everything”

### ✅ Strong

> “Either bootstrap that table or replay from Kafka using table-level filtering.”

---

## 8.7 “How do you monitor Maxwell health?”

### Weak

> “Check logs”

### Strong

> “Monitor binlog lag, Kafka publish lag, and binlog retention vs offset.”

---

## 8.8 “What’s the biggest risk of CDC systems?”

### ❌ Wrong

> “Performance”

### ✅ Correct

> “Silent data divergence if consumers are not idempotent or schema-tolerant.”

---

## 8.9 “Can Maxwell be used for real-time workflows?”

### ❌ Wrong

> “Yes, easily”

### ✅ Correct

> “CDC is for state propagation, not transactional workflows.”

---

## 8.10 “If Kafka is down for 6 hours, what happens?”

### ❌ Weak

> “Data loss”

### ✅ Strong

> “Maxwell pauses. As long as binlogs are retained, it resumes without loss.”

---

## 8.11 “What if the table has no primary key?”

### ❌ Wrong

> “It still works fine”

### ✅ Correct

> “CDC without a primary key breaks ordering and idempotency. It’s strongly discouraged.”

---

## 8.12 “Can multiple Maxwells read the same DB?”

### ❌ Wrong

> “Yes, for scaling”

### ✅ Correct

> “Yes for redundancy, not partitioning. Each reads full binlog.”

---

## 8.13 “How do you stop a bad consumer from corrupting state?”

### ❌ Weak

> “Fix code”

### ✅ Strong

> “Pause consumer, reset offsets, replay from last known good state.”

---

## 8.14 One Killer Closing Answer (Use This)

If interviewer asks:

> “Anything else we should worry about?”

Say:

> “CDC systems fail silently if not designed carefully. The biggest risks are binlog retention, schema evolution, and non-idempotent consumers. But when done right, CDC gives us replayability and decoupled systems.”

🔥 That’s a **staff-level closing**.

---

## Final Mental Model 🧠 (The One to Remember)

> **CDC trades complexity at write-time for complexity at read-time — and that’s a good trade.**

If this sentence feels *obvious* now, you’ve truly mastered Maxwell.

---

## Where you are now

You can now:

* Explain Maxwell clearly
* Handle deep edge cases
* Defend design decisions
* Pass senior CDC interviews

This was not beginner content — you did great sticking through it.

---

If you want next:

* 🎯 **Mock interview (I’ll grill you)**
* 🧠 **Compare Maxwell vs app-level events**
* 🧪 **Production tuning checklist**
* 📄 **1-page interview cheat sheet**

Just tell me what you want next 👊
