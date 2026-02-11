# 📘 STEP 2 — Index, Document & Mapping (Core Data Model)

This step is **VERY important**.
Most Elasticsearch interview mistakes happen here.

---

## 1. Index

* An **Index** is a logical collection of documents
* Comparable to a **table** in a database (conceptually, not technically)
* Each index has:

    * Mapping (schema)
    * Settings (shards, replicas, analyzers)

📌 Interview note:

> Index is not just storage; it defines how data is **searched**.

---

## 2. Document

* A **Document** is a JSON object stored in an index
* Comparable to a **row**
* Each document:

    * Has a unique `_id`
    * Is immutable (updates create a new version internally)

📌 Important:

> Elasticsearch rewrites documents on update, it doesn’t update in place.

---

## 3. Field

* Fields are **key–value pairs** inside a document
* Each field has a **data type**
* Field type decides:

    * How data is indexed
    * How it can be queried

---

## 4. Mapping (VERY IMPORTANT)

* Mapping defines:

    * Field names
    * Field data types
    * How fields are indexed

### Types of Mapping

#### a) Dynamic Mapping

* Elasticsearch auto-detects field types
* Convenient but **dangerous in production**

Problems:

* Wrong data types
* Field explosion
* Mapping conflicts

#### b) Explicit Mapping (Recommended)

* Fields are predefined
* Safer and predictable
* Preferred in production systems

📌 Interview line:

> Always prefer explicit mapping in production.

---

## 5. `text` vs `keyword` (MOST ASKED)

### `text`

* Used for **full-text search**
* Value is **analyzed** (tokenized, lowercased, etc.)
* Cannot be used for:

    * Sorting
    * Aggregations
    * Exact match

Example:

```
"John Doe" → ["john", "doe"]
```

---

### `keyword`

* Used for **exact match**
* Value is stored **as-is**
* Supports:

    * term query
    * sorting
    * aggregations

Example:

```
"John Doe" → "John Doe"
```

---

## 6. Why `term` Query Fails on `text`

* `term` query does **no analysis**
* `text` fields are already tokenized
* Exact match fails due to token mismatch

📌 Interview trap:

> Use `match` for text, `term` for keyword.

---

## 7. Multi-Fields (Very Common Pattern)

A single field can be indexed in **multiple ways**:

Example concept:

* `name` → text (search)
* `name.keyword` → keyword (sort/filter)

📌 This solves:

* Search + sort on same field

---

## 8. Mapping Is Hard to Change

* Field types **cannot be modified**
* Requires:

    * New index
    * Reindex data
    * Switch alias

📌 Interview line:

> Wrong mapping means reindex.

---

## 🧠 Step 2 Memory Hook

> Index = collection, Document = JSON, Mapping controls search behavior, text is analyzed, keyword is exact.