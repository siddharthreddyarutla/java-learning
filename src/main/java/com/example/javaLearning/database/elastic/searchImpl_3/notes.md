# 📘 STEP 3 — How Search Works (Inverted Index)

This step explains **why Elasticsearch search is fast**.

---

## 1️⃣ What Problem Inverted Index Solves

In databases:

* Searching text = scan rows
* `LIKE %word%` → slow
* Indexes work well only for **exact match**

Elasticsearch is built for:

* Searching **words inside text**
* Searching across **multiple fields**
* Ranking results by relevance

---

## 2️⃣ What Is an Inverted Index

Instead of:

```
Document → Text
```

Elasticsearch stores:

```
Word → List of Documents
```

Example:

Documents:

```
Doc1: "user clocked in late"
Doc2: "user clocked out early"
```

Inverted Index:

```
user   → [Doc1, Doc2]
clocked→ [Doc1, Doc2]
late   → [Doc1]
early  → [Doc2]
```

📌 Search becomes:

> Lookup, not scan

---

## 3️⃣ Why Inverted Index Is Fast

* Direct word-to-document mapping
* No full table scan
* Works efficiently across shards
* Parallel search on multiple nodes

📌 Interview line:

> Elasticsearch searches terms, not rows.

---

## 4️⃣ Role of Analyzer (High Level Only)

Analyzer controls:

* How text is broken into tokens
* Lowercasing
* Removing stop words

Example:

```
"Clocked In Late" → ["clocked", "in", "late"]
```

📌 Important:

* `text` fields → analyzed
* `keyword` fields → NOT analyzed

---

## 5️⃣ Why `match` Works but `term` Fails on `text`

* `match` query:

    * Uses analyzer
    * Matches tokens

* `term` query:

    * Exact lookup
    * No analysis

📌 Hence:

> `term` works on keyword, `match` works on text

---

## 6️⃣ Relevance & Scoring (Very High Level)

* Elasticsearch calculates a **score**
* Score decides result order
* Based on:

    * Term frequency
    * Field relevance

📌 You don’t need formulas in interviews.

---

## 7️⃣ What Inverted Index Is NOT Good At

* Frequent updates on same document
* Transactions
* Joins

📌 Reason:

> Updating text means rebuilding index entries.

---

## 🧠 Step 3 Memory Hook

> Elasticsearch uses inverted index where terms point to documents, making search fast and scalable.

---

## 🎯 Interview Confidence Line

If asked *“How does Elasticsearch search work internally?”*:

> Elasticsearch uses an inverted index that maps terms to documents, allowing fast lookups instead of scanning data.