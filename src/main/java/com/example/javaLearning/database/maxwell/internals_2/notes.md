# 🧠 STEP 2 — How Maxwell Works Internally (Deep but Clear)

We’ll answer **three interview-critical questions**:

1️⃣ Where does Maxwell read data from?
2️⃣ How does it know *what* changed?
3️⃣ How does it resume safely after failures?

---

## 2.1 Where Maxwell Reads From (Key Insight)

Maxwell **does NOT read tables**.

It reads **MySQL Binary Logs (binlogs)**.

### Think of binlog as:

* Append-only change log
* Written by MySQL itself
* Used for replication & recovery

```
INSERT / UPDATE / DELETE
        ↓
     Binlog
        ↓
   Maxwell
```

📌 Interview line:

> *“Maxwell listens to the same replication stream that MySQL replicas use.”*

That sentence alone shows deep understanding.

---

## 2.2 How Maxwell Connects to MySQL

Maxwell connects as a **replication client**.

It needs:

* `REPLICATION SLAVE`
* `REPLICATION CLIENT`

That’s why:

* It can read binlogs
* It tracks binlog filename + offset

📌 Important:

> Maxwell can connect to **master or replica** (replica is recommended in prod).

---

## 2.3 Binlog Formats (VERY IMPORTANT)

MySQL supports:

1. STATEMENT
2. ROW
3. MIXED

### Why Maxwell **requires ROW**

#### STATEMENT-based ❌

```sql
UPDATE users SET salary = salary * 1.1 WHERE dept = 'ENG';
```

Problems:

* Which rows changed?
* Old values?
* Non-deterministic

#### ROW-based ✅

MySQL writes:

```
Row ID 10: salary 100 → 110
Row ID 11: salary 120 → 132
```

📌 Interview killer line:

> *“ROW-based binlogs capture the effect of a change, not the SQL that caused it.”*

That’s why CDC tools **require ROW format**.

---

## 2.4 What Maxwell Extracts from Binlog

From each row event, Maxwell gets:

* Database name
* Table name
* Primary key
* Column values
* Old values (for updates)
* Event type (insert/update/delete)
* Timestamp

Then it converts this into **JSON events**.

---

## 2.5 Internal Flow (Memorize This)

```
MySQL
  ↓ (writes)
Binlog (ROW events)
  ↓
Maxwell Replication Client
  ↓
Event Parser
  ↓
JSON Serializer
  ↓
Kafka Producer
```

📌 Interviewers LOVE when you explain flow clearly.

---

## 2.6 How Maxwell Tracks Progress (Offsets)

This is huge for failure questions.

Maxwell tracks:

* Binlog file name
* Binlog position (offset)

It stores this in:

* Its own MySQL schema (`maxwell.positions`)
* (optionally) Kafka

### On restart:

* Reads last saved position
* Resumes from **exact same event**

📌 Interview-ready answer:

> *“Maxwell is fault-tolerant because it persists binlog offsets independently of Kafka.”*

---

## 2.7 What About Transactions?

Important nuance.

* Binlog events are written **after transaction commit**
* Maxwell only sees **committed data**
* No dirty reads

However:

* Maxwell emits **row-level events**
* Transaction boundaries are not explicit

📌 Say this carefully:

> *“Maxwell preserves data correctness but not transactional grouping.”*

---

## 2.8 Ordering Guarantees

* Binlog is **strictly ordered**
* Maxwell preserves binlog order
* Ordering is per:

    * Single MySQL instance
    * Single partition if keyed correctly

📌 Interview tip:

> Ordering across tables is preserved as written in binlog.

---

## Common Interview Traps (Now You Can Handle Them)

**Q: Does Maxwell miss fast updates?**
❌ No — binlog captures everything

**Q: Is there DB load?**
✅ Minimal — binlog already exists

**Q: Can Maxwell rewind?**
⚠ Only if binlog still exists

---

## One-line Summary (Lock This In)

> **“Maxwell works by acting as a MySQL replication client, parsing ROW-based binlogs into ordered JSON change events and publishing them to Kafka.”**

If you say this confidently, interviewer knows you’re solid.