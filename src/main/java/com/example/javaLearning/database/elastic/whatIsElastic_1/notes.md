# 📘 STEP 1 — Elasticsearch Basics (Interview Notes)

## 1. What Elasticsearch Is

* Elasticsearch is a **distributed search and analytics engine**
* Built for **fast full-text search**, filtering, and aggregations
* Designed to work on **large datasets**
* It is **schema-flexible** and **horizontally scalable**

🔹 Think of Elasticsearch as:

> A system optimized for **searching data**, not **storing truth**

---

## 2. Why Elasticsearch Exists

Traditional databases (MySQL, PostgreSQL):

* Optimized for **transactions**
* Use **B-Tree indexes**
* Poor performance for:

    * `LIKE %text%`
    * Multi-field search
    * Relevance-based ranking

Elasticsearch solves:

* Fast **text search**
* Relevance scoring
* Filtering + sorting on millions of records
* Aggregations on large datasets

---

## 3. Elasticsearch vs Database (High-Level)

| Aspect      | Database         | Elasticsearch      |
| ----------- | ---------------- | ------------------ |
| Purpose     | Transactions     | Search & analytics |
| Index type  | B-Tree           | Inverted Index     |
| Consistency | Strong (ACID)    | Eventual           |
| Writes      | Frequent updates | Append-heavy       |
| Search      | Exact            | Full-text + fuzzy  |

📌 Interview takeaway:

> Elasticsearch **complements** DBs, it doesn’t replace them.

---

## 4. Near Real Time (NRT)

* Elasticsearch is **not real-time**
* Indexed documents become searchable **after refresh**
* Refresh happens periodically (default ~1s)
* This design improves write throughput

📌 Important interview line:

> Elasticsearch is near real-time because refresh is asynchronous.

---

## 5. When NOT to Use Elasticsearch

Do **not** use ES:

* As a **primary database**
* For **high-frequency updates**
* When **strong consistency** is required
* For transactional workloads (payments, balances)

📌 Golden rule:

> DB = source of truth, ES = search layer

---

## 6. Typical Elasticsearch Use Cases

* Search bars (users, products, logs)
* Filtering & sorting large datasets
* Analytics dashboards
* Log & event search

---

## 7. Elasticsearch in Real Systems (Context)

Common architecture:

```
MySQL → Maxwell → Kafka → Elasticsearch
```

* DB stores truth
* Kafka streams changes
* Elasticsearch serves search APIs
* Reindexing is possible without touching DB

(This will be expanded later)

---

## 🧠 Step 1 Interview Memory Hook

If you remember only **one thing**:

> Elasticsearch is built for fast, scalable search using inverted index and near-real-time indexing, not transactions.