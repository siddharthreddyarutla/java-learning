## 1️⃣ What & Why Elasticsearch

* What is Elasticsearch?
* Why not MySQL for search?
* Use cases vs non-use cases
* Near Real Time (NRT)

🎯 Interviewer wants:

> Can you justify *why ES exists*.

---

## 2️⃣ Core Data Model

* Index vs Table
* Document vs Row
* Mapping
* `text` vs `keyword`

🔥 Very common:

> Why term query fails on text field?

---

## 3️⃣ How Search Works (Inverted Index)

* What is inverted index?
* Why ES is fast for search?
* Tokenization (high level only)

⚠️ No Lucene internals needed.

---

## 4️⃣ Shards & Replicas (MOST IMPORTANT)

* Primary shard
* Replica shard
* Data distribution
* Node failure handling
* Shard sizing basics

🔥 Almost guaranteed question.

---

## 5️⃣ Write Path & Refresh

* What happens when you index data?
* refresh vs flush
* Why document is not immediately searchable?

🎯 Interview line:

> ES is near real-time, not real-time.

---

## 6️⃣ Query vs Filter + Important Queries

* Query context vs Filter context
* Scoring vs non-scoring
* match vs term
* bool query

🔥 Daily usage questions.

---

## 7️⃣ Updates, Versioning & Consistency

* index vs update API
* Partial updates
* `_version`
* Optimistic locking idea

---

## 8️⃣ Cluster & Node Roles

* Master node
* Data node
* Coordinating node
* What happens when master goes down?

Senior signal topic ✔️

---

## 9️⃣ Scaling, Performance & Pitfalls

* Horizontal scaling
* Too many shards problem
* Hot shards
* Common mistakes

🔥 “Have you used ES in prod?” detector.

---

## 🔟 Real System Design (DB → Kafka → ES)

* ES is NOT source of truth
* Reindex strategy
* Alias based zero-downtime reindex
* Maxwell / Kafka pipeline (your strength 💪)

💎 This step differentiates you.

---

# 🧠 This 10-Step Plan Covers:

✅ 90% interview questions
✅ System design rounds
✅ Production experience signals
✅ Elasticsearch + Kafka flow