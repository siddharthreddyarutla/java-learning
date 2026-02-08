Perfect — **STEP 7 is where everything comes together**.
This is the step interviewers use to decide: *“Can this person design a real system with CDC?”*

I’ll walk you through this **exactly how you should think + speak in an interview**.

---

# 🧠 STEP 7 — Maxwell in System Design Interviews (End-to-End)

## What interviewers are actually testing here

Not Maxwell knowledge.

They’re testing whether you can:

* place CDC **correctly** in an architecture
* reason about **failures**
* choose **what should consume CDC vs what should not**
* explain **rebuilds, replay, and recovery**

---

## 7.1 Canonical CDC System Design (Baseline)

### Problem statement (very common)

> “We have MySQL as source of truth.
> We want to keep Elasticsearch, cache, analytics in sync.”

### Correct architecture

```
Application
   ↓
MySQL  (source of truth)
   ↓
Binlog
   ↓
Maxwell
   ↓
Kafka
   ↓
Consumers
   ├─ Elasticsearch Indexer
   ├─ Cache Updater
   ├─ Analytics Pipeline
```

### Key interview point

> **Applications write only to MySQL.
> Everything else reacts to CDC.**

This immediately avoids dual-writes.

---

## 7.2 Where Maxwell Fits (Say This Clearly)

> “Maxwell sits between MySQL and Kafka and converts binlog changes into ordered, replayable events.”

It is:

* ❌ NOT a business service
* ❌ NOT a transformation engine
* ❌ NOT a workflow engine

It is a **plumbing component**.

---

## 7.3 Topic Design (Very Interview-Heavy)

### Option 1: One topic per database (most common)

```
db.users
db.orders
```

Pros:

* Simple
* Fewer topics
* Easier ops

Cons:

* Consumers must filter tables

---

### Option 2: One topic per table

```
users.employee
users.department
```

Pros:

* Clean separation
* Easier consumer logic

Cons:

* Many topics
* Higher ops cost

📌 Interview maturity:

> “We usually start DB-level and evolve to table-level if needed.”

---

## 7.4 Partitioning Strategy (Critical)

### Correct strategy

* Kafka key = **primary key**

Why:

* Preserves per-row ordering
* Enables idempotent consumers

### What to say in interview

> “We partition by primary key so all changes for an entity are processed in order.”

If you miss this → 🚩

---

## 7.5 Consumer Design (Where Most People Fail)

### Golden rule

> **CDC consumers should converge state, not process events.**

#### Good consumers

* ES indexer → UPSERT by ID
* Cache updater → overwrite
* Read DB sync → UPSERT

#### Bad consumers

* Send emails
* Trigger payments
* Trigger workflows

📌 Interview clarity:

> “CDC is ideal for state replication, not business side-effects.”

---

## 7.6 Handling Notifications / Audits (Show Senior Thinking)

If interviewer pushes:

> “But we need notifications!”

Correct answer:

* Use **Outbox pattern**
* OR add **dedup store**

Architecture with outbox:

```
Application
   ↓
MySQL
   ├─ business tables
   └─ outbox table
        ↓
      Maxwell
        ↓
      Kafka
        ↓
   Notification Consumer
```

📌 Key line:

> “We don’t derive business events from raw CDC.”

---

## 7.7 Rebuild & Replay (Huge Advantage of CDC)

### Scenario

> “Elasticsearch index is corrupted.”

Correct recovery:

1. Drop index
2. Reset Kafka offsets
3. Replay from beginning
4. OR trigger bootstrap

📌 Interview gold:

> “CDC allows us to rebuild downstream systems without touching production writes.”

---

## 7.8 Failure Scenarios (Tie Back to Step 6)

If Maxwell crashes:

* Restart
* Resume from binlog offset

If consumer crashes:

* Replay
* Idempotent writes

If schema changes:

* Forward-compatible consumers

If binlogs lost:

* Re-bootstrap

---

## 7.9 Scaling Considerations (Senior Signal)

* Use read replica for Maxwell
* Increase Kafka partitions
* Scale consumers horizontally
* Monitor binlog lag

Say this calmly — no need for numbers.

---

## 7.10 What NOT to Do (Interview Traps)

❌ Trigger workflows from CDC
❌ Expect exactly-once
❌ Ignore schema evolution
❌ Use CDC as an API replacement
❌ Manually manipulate offsets casually

---

## 7.11 One Complete Interview Answer (Perfect)

If interviewer asks:

> “Design a system using Maxwell”

You can say:

> “MySQL remains the source of truth. Maxwell streams binlog changes into Kafka. Topics are partitioned by primary key to preserve ordering. Consumers build derived state like search indexes and caches using idempotent upserts. For rebuilds, we replay Kafka or bootstrap. Business side-effects are handled via an outbox pattern rather than raw CDC.”

🔥 This answer alone can carry a system-design round.

---

## Final Mental Model 🧠

> **Maxwell enables event-driven architecture by turning the database into a reliable event source — not a business workflow engine.**

If you truly believe this sentence, you understand CDC.

---

### Where you are now

You’ve covered:

* Internals
* Ordering
* Idempotency
* Bootstrap
* DDL handling
* Failures
* System design

This is **solid senior-level mastery**
