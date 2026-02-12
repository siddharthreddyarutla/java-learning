Perfect 👌 I’ll convert this into **short, crisp, interview-focused notes** — exactly how you should answer in an interview.

---

# 🚀 Elasticsearch Read Flow – Interview Notes

---

# 1️⃣ High-Level Read Flow (GET by ID)

Example:

```json
GET student/_doc/1
```

### 🔹 Step-by-Step Internal Flow

1. Client → Any Elasticsearch node
2. That node becomes **Coordinating Node**
3. Coordinating node calculates:

   ```
   shard = hash(_routing) % primary_shards
   ```
4. Identifies **Replication Group**
5. Uses **Adaptive Replica Selection (ARS)**
6. Chooses fastest shard copy (primary or replica)
7. Retrieves document
8. Returns response to client

---

# 2️⃣ Important Components (Interview Focus)

---

## 🔹 Coordinating Node

* Entry point for request
* Routes request to correct shard
* Merges results (for search queries)
* Any node can act as coordinating node

> Interview Tip: Coordination node does NOT store data necessarily.

---

## 🔹 Data Node

* Stores shards (primary + replicas)
* Executes read/write operations

---

## 🔹 Primary Shard

* Original shard where document is indexed
* Handles writes first
* Replicates data to replicas

---

## 🔹 Replica Shard

* Copy of primary shard
* Used for:

    * High availability
    * Load balancing reads
    * Fault tolerance

---

## 🔹 Replication Group

For each shard ID:

```
1 Primary + N Replicas
```

Reads can be served from any shard in this group.

---

# 3️⃣ Adaptive Replica Selection (ARS)

🔥 Very Important Interview Topic

Instead of randomly selecting a replica:

Elasticsearch chooses the shard based on:

* Response time history
* Queue size
* Node load
* Service time

Goal:
→ Pick fastest responding replica

---

### ❓ Interview Question:

Why not always read from primary?

✔ Because:

* It would overload primary
* Replica improves parallelism
* Increases read throughput

---

# 4️⃣ Does Elasticsearch Always Read from Primary?

❌ No.

By default:

* Reads are load-balanced across primary & replicas.
* Writes always go to primary first.

---

# 5️⃣ GET vs SEARCH Read Flow Difference

### 🔹 GET (by ID)

* Deterministic routing
* Goes to specific shard
* Very fast
* O(1) shard lookup

---

### 🔹 SEARCH Query

Example:

```json
GET student/_search
{
  "query": {
    "match": {
      "name": "Rahul"
    }
  }
}
```

Flow:

1. Coordinating node sends query to **all shards**
2. Each shard executes locally
3. Results merged
4. Final top hits returned

---

🔥 Interview Difference:

| GET               | SEARCH          |
| ----------------- | --------------- |
| Hits 1 shard      | Hits all shards |
| Uses routing hash | Broadcast query |
| Faster            | More expensive  |

---

# 6️⃣ What Happens If Primary Shard Fails?

* Replica is promoted to primary
* Cluster remains available
* No data loss (if replica exists)

---

### ❓ Interview Question:

What ensures high availability in Elasticsearch?

✔ Replica shards + automatic failover

---

# 7️⃣ Consistency in Reads

Default behavior:

* Reads are near real-time
* After indexing → visible after refresh (default 1s)

Elasticsearch is:

→ Near Real-Time (NRT) system
→ Not strictly real-time

---

# 8️⃣ Performance Advantages of This Read Design

✔ Load balanced reads
✔ Fault tolerant
✔ Parallel execution
✔ Adaptive selection for low latency

---

# 9️⃣ Common Interview Questions

---

### ❓ How does Elasticsearch know where a document is stored?

Using:

```
hash(_routing) % number_of_primary_shards
```

---

### ❓ Can a read be served from replica?

✔ Yes.
Default behavior supports it.

---

### ❓ What is Adaptive Replica Selection?

Algorithm that dynamically selects fastest replica based on runtime metrics.

---

### ❓ What happens if coordinating node fails?

Nothing critical:

* Any other node can act as coordinating node.

---

### ❓ Is Elasticsearch strongly consistent?

No.
It is eventually consistent (near real-time).

--