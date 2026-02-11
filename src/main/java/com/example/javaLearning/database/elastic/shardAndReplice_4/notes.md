
# 📘 STEP 4 — Shards & Replicas (MOST IMPORTANT)

---

## 1️⃣ Why Shards Exist

* Elasticsearch indexes can become **very large**
* A single machine cannot:

    * Store all data
    * Handle all queries

📌 Solution:

> Split index into **shards** and distribute them across nodes.

---

## 2️⃣ What Is a Shard

* A shard is a **physical partition** of an index
* Each shard is:

    * An independent Lucene index
    * Can be placed on any node

📌 Think:

> Index = logical
> Shard = physical

---

## 3️⃣ Primary Shards

* Original shards where data is written
* Each document is stored in **exactly one primary shard**
* Number of primary shards:

    * Defined at index creation
    * **Cannot be changed later**

📌 Interview line:

> Primary shard count decides data distribution.

---

## 4️⃣ Replica Shards

* Copy of primary shards
* Used for:

    * High availability
    * Read scalability

Rules:

* Replica never lives on same node as its primary
* Replicas can be increased anytime

📌 Interview line:

> Replicas improve reads, not writes.

---

## 5️⃣ Write Flow (Very Important)

1. Client sends request to any node
2. Request routed to **primary shard**
3. Primary shard indexes document
4. Change is copied to **replica shards**
5. Acknowledgement sent back

📌 ES ensures:

> Primary → Replica consistency

---

## 6️⃣ Read Flow

* Search request hits **any shard copy**
* Both primary and replica shards can serve reads
* Queries are executed **in parallel**

📌 Result:

> Faster searches at scale

---

## 7️⃣ What Happens If a Node Goes Down

* If node with primary shard fails:

    * Replica is promoted to primary
* Cluster continues serving requests
* New replica is created automatically

📌 Interview confidence line:

> Elasticsearch self-heals using replicas.

---

## 8️⃣ Shard Count Design (Interview Favorite)

Bad:

* Too many shards → memory waste
* Too few shards → scaling problems

Rule of thumb:

* 1 shard ≈ 10–50 GB
* Avoid thousands of tiny shards

📌 Interview line:

> Shards are not free.

---

## 9️⃣ Hot Shards Problem

* Uneven data distribution
* One shard gets most traffic
* Causes:

    * Time-based data
    * Poor routing

📌 Fix:

* Better shard strategy
* Time-based indices

---

## 🔟 Why Primary Shards Cannot Be Changed

* Documents are hashed to shard
* Changing shard count changes routing
* Requires full reindex

📌 Interview killer line:

> Changing primary shards breaks document routing.

---

## 🧠 Step 4 Memory Hook

> Shards split data, replicas protect data and scale reads.

---

## 🎯 Interview Gold Summary

> Elasticsearch distributes data using primary shards and ensures availability and scalability using replica shards, with parallel search execution across nodes.