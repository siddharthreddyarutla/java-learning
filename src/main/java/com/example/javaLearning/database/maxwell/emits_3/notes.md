# 🧠 STEP 3 — What Exactly Maxwell Emits (Events & Payloads)

Interviewers *love* this step because it reveals:

* whether you understand CDC **practically**
* whether you can design **consumers correctly**

---

## 3.1 Maxwell Emits **Events**, Not Queries

This is the **first thing** to say if asked.

❌ Maxwell does **not** emit SQL
❌ Maxwell does **not** emit table snapshots (except bootstrap)

✅ Maxwell emits **row-level change events**

---

## 3.2 Basic Event Structure (High-Level)

Every Maxwell event contains:

* **Metadata**
* **Change type**
* **Row data**

Conceptually:

```json
{
  "database": "...",
  "table": "...",
  "type": "...",
  "ts": "...",
  "data": { ... },
  "old": { ... }
}
```

📌 Interview insight:

> *“Maxwell events are self-describing — consumers don’t need DB access.”*

---

## 3.3 Event Types (Must Know)

### 1️⃣ INSERT

```json
"type": "insert"
```

* Contains full row data
* No `old` field

Use case:

* Index document
* Create cache entry

---

### 2️⃣ UPDATE

```json
"type": "update"
```

Contains:

* `data` → new values
* `old` → only changed columns

Example:

```json
"old": { "salary": 100 }
"data": { "salary": 120 }
```

📌 Interview trap:

> `old` does NOT contain full row — only changed columns.

---

### 3️⃣ DELETE

```json
"type": "delete"
```

* `data` contains **last known values**
* Used to delete from ES, cache, etc.

---

### 4️⃣ BOOTSTRAP (Special)

Used when:

* Starting CDC on existing DB
* Rebuilding downstream systems

```json
"type": "bootstrap"
```

📌 Interview keyword:

> Bootstrap = controlled initial snapshot

---

## 3.4 Why This Event Design Matters

Because:

* Consumers don’t need DB joins
* No extra DB queries
* Events are replayable
* Stateless consumers possible

👉 This is **event-driven architecture done right**.

---

## 3.5 Primary Key Is Everything (VERY IMPORTANT)

Maxwell includes **primary key values** in every event.

Why interviewers care:

* Enables idempotency
* Enables partitioning
* Enables upserts

📌 Interview line:

> *“CDC consumers must treat primary keys as event identity.”*

---

## 3.6 Ordering & Kafka Partitions

### Correct setup:

* Kafka key = primary key
* Ensures:

    * All updates for same row go to same partition
    * Order is preserved per entity

### Wrong setup:

* Random partitioning
* Causes:

    * Out-of-order updates
    * Corrupted downstream state

📌 Interview warning:

> Ordering is guaranteed **only within a Kafka partition**.

---

## 3.7 Why Duplicates Can Happen

Even in Step 3, this matters.

Reasons:

* Maxwell crash after publish, before offset save
* Kafka retries
* Consumer retries

Therefore:

* **At-least-once delivery**

📌 Interview answer:

> *“Maxwell prioritizes durability over exactly-once semantics.”*

---

## 3.8 How Consumers Should Behave (Interview Bonus)

Correct consumer behavior:

* Idempotent writes
* Upserts instead of inserts
* Use PK + timestamp/version

Example:

* ES → index by primary key
* Cache → overwrite
* Analytics → deduplicate

---

## 3.9 One-Liner Summary (Memorize)

> **“Maxwell emits ordered, row-level change events with before/after data, enabling downstream systems to stay in sync without querying the database.”**

---

## Quick Self-Test (You should answer instantly)

* Does update event include full old row? ❌
* Can delete event be used to remove ES docs? ✅
* Is ordering guaranteed across partitions? ❌

If these feel obvious now — you’re doing great.