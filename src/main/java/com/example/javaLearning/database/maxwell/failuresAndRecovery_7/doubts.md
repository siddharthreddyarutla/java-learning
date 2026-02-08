## 1️⃣ How does Maxwell handle **DDL commands**?

### Short truth

✅ **Yes, DDLs are captured and published**
❌ But **they are NOT first-class citizens like DML**

---

### What happens internally

* DDL statements (ALTER, CREATE, DROP, RENAME) **do appear in MySQL binlog**
* Maxwell detects them
* It emits a **DDL event** (schema change event)

Example (conceptual):

```json
{
  "type": "table-alter",
  "database": "users",
  "table": "employee",
  "sql": "ALTER TABLE employee ADD COLUMN age INT"
}
```

📌 Interview-important nuance:

> Maxwell emits DDL **as metadata events**, not as row events.

---

### What Maxwell does NOT do with DDL

❌ It does NOT:

* enforce schema compatibility
* version schemas
* block incompatible changes
* manage consumer deserialization

That’s why:

* Consumers must be **schema-tolerant**
* Or you choose Debezium + Schema Registry

📌 Interview line:

> *“Maxwell surfaces DDLs but leaves schema evolution responsibility to consumers.”*

---

## 2️⃣ Can Maxwell break because of queries / DDLs?

### Yes — but let’s be precise.

Maxwell can stop if:

* It encounters an **unsupported binlog event**
* Schema change is too complex
* Binlog is corrupted
* Table metadata mismatch

Typical cases:

* Table without primary key (older Maxwell versions)
* Exotic DDL sequences
* Binlog format misconfiguration

---

### What happens when Maxwell breaks?

* Maxwell **stops**
* It does **NOT advance binlog offset**
* No silent data loss

📌 This is critical:

> Maxwell is **fail-fast**, not fail-open.

---

### Do you need to manually update offsets?

❌ **Almost never in production**

Correct recovery:

1. Fix root cause (schema / config)
2. Restart Maxwell
3. It resumes from **last saved binlog position**

Manual offset manipulation is:

* Dangerous
* Interview red flag
* Used only in emergencies

📌 Interview line:

> *“Manual offset changes are a last resort and risk data loss.”*

---

## 3️⃣ How does Maxwell publish to Kafka? Where is it configured?

### Architecture truth

Maxwell has a **built-in Kafka producer**.

There is:

* No Kafka Connect
* No external sink

```
Maxwell → KafkaProducer → Kafka
```

---

### Where Kafka is configured (conceptually)

In Maxwell config:

* Kafka brokers
* Producer configs
* Topic naming strategy

Interview-safe explanation:

> “Kafka configuration is provided directly to Maxwell, which produces events using an embedded Kafka producer.”

You are **NOT expected** to recite config keys.

---

## 4️⃣ How does Maxwell decide **which topic to publish to**?

This is important and often asked.

### Default behavior

📌 **One topic per database**

Example:

```
db.users
db.orders
```

---

### Other supported strategies

* Topic per table

  ```
  users.employee
  users.department
  ```

* Custom routing

* Regex filters

📌 Interview clarity:

> *“Topic routing is configurable; the most common strategy is database-level topics.”*

---

## 5️⃣ How does Maxwell handle multiple databases?

* Single Maxwell instance
* Can read **multiple databases**
* Emits events with:

    * database name
    * table name

Kafka topics:

* Either shared
* Or separated per DB

Ordering guarantee:

* Preserved per primary key
* Across DBs ordering is irrelevant

---

## 6️⃣ Difference: **Maxwell vs Dynamic Maxwell**

This is a GREAT question — many people don’t even know this exists.

### Maxwell (standard)

* Static configuration
* Restart needed for:

    * filters
    * topic changes
    * bootstrap config

---

### Dynamic Maxwell

* Supports **runtime changes**
* Filters, bootstraps, routing updated dynamically
* No restart required

Use cases:

* Large multi-tenant systems
* Frequent table onboarding
* Operational flexibility

📌 Interview positioning:

> *“Dynamic Maxwell improves operability, not CDC semantics.”*

CDC guarantees are the **same**.

---

## 7️⃣ Final clarity checklist (you should be 100% clear now)

✔ DML → row events
✔ DDL → metadata events
✔ Maxwell does NOT enforce schemas
✔ Crashes are safe (offsets preserved)
✔ Kafka producer is embedded
✔ Topic routing is configurable
✔ Dynamic Maxwell = operational enhancement

---

## Interview-Perfect Summary (Memorize)

If interviewer compresses all your questions into one, say:

> “Maxwell captures both DML and DDL from MySQL binlogs. DMLs become row events, while DDLs are emitted as schema metadata events. It fails fast on unsupported changes without advancing offsets. Kafka publishing is done via an embedded producer with configurable topic routing, commonly one topic per database. Dynamic Maxwell adds runtime configurability without changing CDC guarantees.”

🔥 That answer is **rock solid**.
