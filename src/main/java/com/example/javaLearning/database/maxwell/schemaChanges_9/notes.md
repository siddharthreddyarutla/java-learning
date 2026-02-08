# 🧠 STEP 9 — Schema Changes (DDL Handling in Maxwell)

## What interviewers are *really* asking

When they ask about DDL, they want to know:

* Can CDC survive schema evolution?
* Will consumers break?
* What guarantees exist?
* Where does Maxwell stop helping?

---

## 9.1 First Principle (say this first)

> **DDL is serialized in the MySQL binlog, and Maxwell reads it in order.**

This single sentence eliminates 80% of confusion.

---

## 9.2 What Types of DDL Maxwell Sees

Maxwell sees **all schema-changing DDL** that goes to the binlog:

* `ALTER TABLE ADD / DROP COLUMN`
* `ALTER TABLE MODIFY COLUMN`
* `RENAME TABLE`
* `CREATE TABLE`
* `DROP TABLE`

📌 Important:

> DDL is NOT row-level — it’s schema-level metadata.

---

## 9.3 How Maxwell Handles DDL Internally

### Step-by-step:

1. MySQL writes DDL to binlog
2. Maxwell reads the DDL event
3. Maxwell updates its **internal schema cache**
4. Maxwell emits a **DDL metadata event**
5. Subsequent row events are parsed using **new schema**

There is **no race condition** here because:

* Binlog order is total
* Table-map events define schema per row event

---

## 9.4 What a DDL Event Looks Like (Conceptual)

Example:

```json
{
  "type": "table-alter",
  "database": "users",
  "table": "employee",
  "sql": "ALTER TABLE employee ADD COLUMN age INT"
}
```

📌 Interview nuance:

> Maxwell exposes DDL as informational events — it does not enforce compatibility.

---

## 9.5 What Guarantees Maxwell Provides

| Guarantee                     | Provided |
| ----------------------------- | -------- |
| DDL order                     | ✅        |
| Correct schema for row events | ✅        |
| No partial schema             | ✅        |
| Schema compatibility          | ❌        |
| Consumer safety               | ❌        |

📌 Interview line:

> “Maxwell guarantees schema correctness, not schema safety.”

---

## 9.6 Why Consumers Break on Schema Changes

Common reasons:

* Strict JSON deserialization
* Expecting fixed columns
* Not handling nulls
* Assuming column order

📌 Interview tip:

> “CDC consumers must be forward-compatible.”

---

## 9.7 Safe Schema Change Patterns (Interview Gold)

### ✅ Safe

* ADD COLUMN (nullable)
* ADD COLUMN with default
* RENAME COLUMN (carefully)
* ADD TABLE

### ⚠ Risky

* DROP COLUMN
* CHANGE COLUMN type
* RENAME TABLE (consumer impact)

📌 Senior insight:

> “Schema evolution must be designed, not discovered.”

---

## 9.8 How to Protect Consumers

### Best practices:

* Ignore unknown fields
* Treat missing fields as null
* Avoid positional mapping
* Use dynamic deserialization

📌 Interview line:

> “Consumers should tolerate both older and newer schemas.”

---

## 9.9 Maxwell vs Debezium for Schema Evolution

### Maxwell

* Emits DDL
* No schema registry
* Consumer-managed evolution

### Debezium

* Tracks schema history
* Integrates with Schema Registry
* Safer for long-lived pipelines

📌 Interview framing:

> “Maxwell is simpler but shifts schema responsibility downstream.”

---

## 9.10 What Happens If DDL Breaks Maxwell?

If Maxwell cannot parse:

* It **stops**
* Offset is NOT advanced
* No silent corruption

Recovery:

* Fix schema or config
* Restart
* Resume safely

📌 Interview line:

> “Maxwell fails fast on ambiguous schema changes.”

---

## 9.11 Can You Ignore DDL Events?

Yes — and many systems do.

But:

* You still must handle schema changes in row events

📌 Interview nuance:

> “Ignoring DDL events does not mean ignoring schema evolution.”

---

## 9.12 One Perfect Interview Answer (Memorize)

If interviewer asks:

> “How does Maxwell handle schema changes?”

Say:

> “DDL is serialized in the binlog. Maxwell reads schema change events in order, updates its internal schema cache, emits DDL metadata events, and parses subsequent row events using the new schema. It guarantees correctness but not compatibility, so consumers must be forward-compatible.”

🔥 That answer is **senior-level**.

---

## Final Mental Model 🧠

> **Maxwell guarantees that row data matches the schema at that point in time — nothing more, nothing less.**

If you truly understand this sentence, you understand DDL handling.

---

### Where you are now

You’ve covered:

* CDC internals
* Ordering
* Idempotency
* Bootstrap
* Topic design
* Failures
* Exactly-once vs at-least-once
* Schema evolution

That’s a **complete Maxwell interview mastery**.---