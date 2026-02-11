# 📘 Mapping Breakdown (Concept-Wise, Short)

## 1️⃣ `dynamic: false`

* Elasticsearch will **NOT auto-create fields**
* Any unknown field in incoming document is **ignored**
* Prevents:

    * Mapping explosion
    * Wrong field types

📌 Interview note:

> `dynamic:false` is a production safety feature.

---

## 2️⃣ `runtime` fields (Computed Fields)

These fields are **NOT stored or indexed**.

### What they are:

* Calculated **at query time**
* Use **painless scripts**
* Derived from existing fields

### Examples here:

* `CLOCK_IN_DEVIATION_IN_MINUTES`
* `CLOCK_OUT_DEVIATION_IN_MINUTES`

They compute:

```
(actual time - expected shift time) in minutes
```

📌 When to use runtime fields:

* Derived values
* Avoid reindexing
* Low query volume

📌 Interview line:

> Runtime fields trade performance for flexibility.

---

## 3️⃣ `properties` — Actual Indexed Fields

This is where **text vs keyword** matters.

---

## 4️⃣ `text` Fields (Full-Text Search)

Example:

```json
"ATTENDANCE_COMMENTS": {
  "type": "text"
}
```

### Meaning:

* Field is **analyzed**
* Tokenized for search
* Used for:

    * match query
    * full-text search

❌ Not good for:

* sorting
* aggregations
* exact match

---

## 5️⃣ `keyword` Fields (Exact Match)

Example:

```json
"STATUS": { "type": "keyword" }
```

### Meaning:

* Stored **as-is**
* Used for:

    * term query
    * filtering
    * sorting
    * aggregations

📌 Typical keyword fields here:

* STATUS
* USER_STATUS
* EMAIL
* SHIFT_CODE
* NAME

---

## 6️⃣ Multi-Fields (`text` + `keyword`)

Example:

```json
"ATTENDANCE_COMMENTS": {
  "type": "text",
  "fields": {
    "raw": {
      "type": "keyword"
    }
  }
}
```

### Why this is used:

* `ATTENDANCE_COMMENTS` → search
* `ATTENDANCE_COMMENTS.raw` → filter / aggregation

📌 Interview favorite:

> This pattern supports search + exact match on same field.

---

## 7️⃣ `date` Fields

Example:

```json
"START_TIME": {
  "type": "date",
  "format": "yyyy-MM-dd HH:mm:ss||epoch_millis"
}
```

### Meaning:

* Supports **range queries**
* Supports **date math**
* Multiple formats allowed

Used for:

* Time-based filtering
* Analytics
* Sorting

---

## 8️⃣ Numeric Fields (`long`, `integer`, `float`)

Examples:

* `USER_ID` → long
* `MONTH_NUMBER` → integer
* `NO_OF_HOURS` → float

### Purpose:

* Fast range queries
* Aggregations
* Sorting

📌 Correct usage — numeric values are **never text**.

---

## 9️⃣ `flattened` Field

Example:

```json
"METADATA": {
  "type": "flattened"
}
```

### Meaning:

* Semi-structured JSON
* Unknown keys
* Indexed as **key–value pairs**

📌 Why flattened:

* Avoid mapping explosion
* Flexible metadata storage

---

## 🔟 `store: true`

* Field value stored separately from `_source`
* Rarely needed
* Slightly increases storage

📌 Interview note:

> `_source` is usually enough; `store:true` is optional.

---

## 🧠 Big Interview Takeaways

* `text` → search
* `keyword` → filter / sort / aggregate
* Multi-fields → best of both
* Runtime fields → computed at query time
* `dynamic:false` → production safety

---

If interviewer shows **this mapping**, they’re checking:
✅ Do you understand **search vs filter**
✅ Do you know **why keyword exists**
✅ Can you reason about **production decisions**