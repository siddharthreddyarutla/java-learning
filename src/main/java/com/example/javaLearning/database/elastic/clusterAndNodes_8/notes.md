Great, we’re almost at the finish line now 👍
**Step 8** is a **senior-signal step** — interviewers use this to separate users from operators.

Same **concept-wise notes**, crisp.

---

# 📘 STEP 8 — Cluster & Node Roles

---

## 1️⃣ Elasticsearch Cluster

* A cluster is a **group of nodes**
* Nodes work together to:

    * Store data
    * Serve search
    * Maintain availability

📌 Every node belongs to **one cluster**.

---

## 2️⃣ Master Node

* Responsible for **cluster coordination**
* Manages:

    * Index creation/deletion
    * Shard allocation
    * Node membership

📌 Important:

> Master node does NOT handle search or indexing data.

---

## 3️⃣ Data Node

* Stores:

    * Primary shards
    * Replica shards
* Handles:

    * Indexing
    * Search queries

📌 This is where:

> CPU, disk, and memory are heavily used.

---

## 4️⃣ Coordinating Node

* Entry point for client requests
* Responsibilities:

    * Distribute search requests to shards
    * Collect and merge results
    * Return final response

📌 Important:

> Every node can act as a coordinating node.

---

## 5️⃣ Ingest Node

* Used for:

    * Pre-processing documents
    * Enrichment
    * Transformations

Examples:

* Add fields
* Parse values
* Modify structure

📌 Used before indexing data.

---

## 6️⃣ Dedicated Node Roles (Production Setup)

In production:

* Dedicated master nodes
* Dedicated data nodes
* Optional ingest nodes

📌 Why:

> Prevents heavy queries from affecting cluster stability.

---

## 7️⃣ What Happens If Master Node Goes Down

* Cluster elects a new master
* Uses **quorum**
* No data loss

📌 Interview line:

> Master election ensures cluster availability.

---

## 8️⃣ Split Brain Problem (High-Level)

* Happens when:

    * Multiple masters are elected
* Prevented using:

    * Quorum-based election

📌 Modern ES handles this automatically.

---

## 🧠 Step 8 Memory Hook

> Master manages cluster state, data nodes store data, coordinating nodes handle requests.

---

## 🎯 Interview Gold Summary

> Elasticsearch separates cluster management from data storage using node roles to improve scalability and stability.
