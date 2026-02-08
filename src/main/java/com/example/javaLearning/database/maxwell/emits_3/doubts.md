## 1️⃣ How does Maxwell handle **ordering in the same table**?

### Core truth

**Ordering comes from the MySQL binlog, not from Maxwell.**

* MySQL writes binlog events **sequentially**
* Each row change has:

    * binlog file
    * binlog position
* Maxwell reads **in that exact order**

So for a **single MySQL instance**:

```
UPDATE row A
UPDATE row B
UPDATE row A
```

Maxwell will always see:

```
A → B → A
```

👉 **Table name does NOT matter for ordering**
👉 **Binlog order is global across all tables**

📌 Interview-ready line:

> *“Ordering is guaranteed by MySQL binlog, and Maxwell preserves it.”*

---

## 2️⃣ How does `Kafka key = primary key` actually work?

Good — this is not magic.

### What Maxwell does

* For every row event, Maxwell extracts:

    * **Primary key column(s)**
* When producing to Kafka:

    * **Kafka message key = primary key value**

Example:

```json
{
  "id": 42,
  "name": "Rahul"
}
```

Kafka record:

```
key = "42"
value = {...event...}
```

### Why this matters

Kafka guarantees:

* **Same key → same partition**
* **Same partition → ordered messages**

So:

```
UPDATE user 42
UPDATE user 42
UPDATE user 42
```

→ always processed **in order** by consumers.

📌 Interview line:

> *“We use the primary key as Kafka key to preserve per-entity ordering.”*

---

## 3️⃣ On UPDATE: how does Maxwell get `old` and `data`?

🔥 This is the **most important misunderstanding** people have.

### ❌ Maxwell does NOT query the table

❌ Maxwell does NOT run SELECT
❌ Maxwell does NOT fetch rows from DB

### ✅ Everything comes from the **ROW binlog**

When MySQL is configured with:

```
binlog_format = ROW
binlog_row_image = FULL
```

MySQL itself writes:

* **Before image**
* **After image**

Example internally (conceptual):

```
BEFORE: salary = 100
AFTER:  salary = 120
```

Maxwell simply parses this.

### Why `old` has only changed fields?

Because Maxwell:

* Compares BEFORE vs AFTER
* Emits:

    * `old` → only fields that changed
    * `data` → full new row (or configured subset)

📌 Interview killer sentence:

> *“Maxwell never queries tables; it derives old and new values entirely from row-based binlogs.”*

---

## 4️⃣ Does Maxwell itself push data to Kafka?

### Yes. Directly.

Maxwell has:

* Embedded Kafka producer
* Configured via `config.properties`

Conceptually:

```
Maxwell → KafkaProducer → Kafka Broker
```

No external connector needed.

---

## 5️⃣ Where is Kafka configured in Maxwell?

High level (interview-safe, not config-heavy):

Maxwell config includes:

* Kafka brokers
* Topic naming strategy
* Acks / retries
* Key format (PK-based)

Example (conceptual, **not required to memorize**):

```
producer=kafka
kafka.bootstrap.servers=broker1:9092
topic.naming.strategy=database
```

📌 Interview expectation:

> They don’t want config files — they want architecture understanding.

---

## 6️⃣ Does Maxwell do any duplicate handling?

### ❌ No. And this is intentional.

Maxwell guarantees:

* **At-least-once delivery**
* **Durability > exactly-once**

Duplicates can happen if:

* Maxwell crashes **after** producing to Kafka
* But **before** saving binlog position

On restart:

* Same event is re-emitted

📌 Interview line (VERY IMPORTANT):

> *“CDC systems push idempotency responsibility to consumers, not producers.”*

---

## 7️⃣ Then how do systems safely handle duplicates?

Correct patterns:

* Use **primary key**
* Use **upserts**
* Use **event version / timestamp**
* Make consumers **idempotent**

Examples:

* Elasticsearch → `PUT /index/{id}`
* Cache → overwrite
* DB sink → UPSERT

---

## 8️⃣ One mental model to lock everything 🧠

> **MySQL binlog already contains ordered, before/after row changes.
> Maxwell simply translates that stream into Kafka messages and never queries the database.**

If you say this in an interview — that’s senior-level clarity.

---

## Summary (Answer-ready)

If interviewer asks *any* of your questions, you can say:

> “Ordering comes from the MySQL binlog. Maxwell preserves it and uses the primary key as Kafka key to maintain per-row ordering. Old and new values are extracted directly from row-based binlogs — Maxwell never queries tables. It pushes events directly to Kafka with at-least-once semantics, so consumers must be idempotent.”

🔥 That is a **perfect answer**.

