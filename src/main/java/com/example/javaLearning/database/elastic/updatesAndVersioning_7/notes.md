Perfect 👍
**Step 7** is where interviewers check whether you understand **immutability + consistency trade-offs** in Elasticsearch.

Same **concept-wise notes**, no Q&A.

---

# 📘 STEP 7 — Updates, Versioning & Consistency

---

## 1️⃣ Document Immutability

* Elasticsearch documents are **immutable**
* An update:

    * Reads the old document
    * Applies changes
    * Writes a **new document version**

📌 Important:

> ES never updates a document in place.

---

## 2️⃣ `index` vs `update`

### `index` API

* Replaces the whole document
* Faster for full writes
* Common in CDC pipelines (Maxwell → Kafka → ES)

### `update` API

* Partial update
* Uses script or doc merge
* Internally still rewrites document

📌 Interview line:

> Update is a read-modify-write operation.

---

## 3️⃣ Partial Updates

* Used to update specific fields
* Still creates a new version internally
* More expensive than full replace in bulk cases

📌 Best practice:

> Use full document indexing for bulk ingestion.

---

## 4️⃣ Versioning (`_version`)

* Every document has a version
* Version increments on every update
* Used to detect conflicting updates

📌 Interview note:

> Versioning enables optimistic locking.

---

## 5️⃣ Optimistic Locking

* Prevents overwriting newer updates
* Update succeeds only if version matches

Concept:

```
Update doc only if version == X
```

📌 When useful:

* Concurrent updates
* Multiple writers

---

## 6️⃣ Consistency Model

* Elasticsearch is **eventually consistent**
* Writes go:

    * Primary shard
    * Then replicas
* Reads may temporarily see old data

📌 Interview line:

> Elasticsearch favors availability and performance over strict consistency.

---

## 7️⃣ Read-After-Write Behavior

* Newly indexed data may not be visible immediately
* Due to refresh cycle

📌 This explains:

> “I indexed but search didn’t return it”

---

## 8️⃣ When Updates Are a Problem

* High update rate on same document:

    * Causes segment churn
    * Affects performance

📌 Anti-pattern:

> Using ES like a transactional DB.

---

## 🧠 Step 7 Memory Hook

> Elasticsearch updates rewrite documents and rely on versioning for conflict control.

---

## 🎯 Interview Gold Summary

> Elasticsearch uses immutable documents with versioning and optimistic locking, trading strong consistency for scalability and performance.