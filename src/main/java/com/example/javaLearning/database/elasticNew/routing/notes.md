# 🚀 Elasticsearch Writing & Searching – Interview Notes

---

## 1️⃣ How Elasticsearch Writes a Document (Indexing Flow)

### 🔹 What Happens When You Index a Document?

1. Client sends document → coordinating node
2. Elasticsearch calculates:

```
shard_number = hash(_routing) % number_of_primary_shards
```

3. Document is written to:

    * One **primary shard**
    * Then replicated to **replica shards**

---

### 🔹 Default Routing

* By default:

  ```
  routing = document _id
  ```
* So shard is determined using `_id`.

---

### 🔥 Interview Point:

> Elasticsearch does NOT randomly store documents.
> It uses hashing on routing value to deterministically select a shard.

---

## 2️⃣ How Searching Works Internally

### 🔹 Search Execution Steps

1. Query sent to coordinating node.
2. Query forwarded to **all relevant primary or replica shards**.
3. Each shard:

    * Executes query locally
    * Returns top results
4. Coordinating node:

    * Merges results
    * Sorts
    * Returns final response

---

### ⚠ Important Clarification

By default:

* **Search queries go to ALL shards of that index**

BUT:

If you provide routing:

```json
GET student/_search?routing=123
{
  "query": {
    "match": {
      "name": "Rahul"
    }
  }
}
```

Then:

* Only the shard responsible for routing=123 is searched.

---

### 🔥 Interview Trick Question

❓ Does Elasticsearch search only one shard?

✔ Answer:

* By default → searches all shards.
* With routing → can search specific shard.

---

## 3️⃣ Why Routing Is Important

Routing ensures:

* Document always goes to same shard
* Search can be optimized
* Reduced shard fan-out
* Better performance in large clusters

---

### 🔹 Custom Routing Example

```json
PUT student/_doc/1?routing=student_2024
{
  "name": "Rahul",
  "year": 2024
}
```

Now:

* Document is stored using `student_2024` for shard calculation.
* When searching, must use same routing:

```json
GET student/_doc/1?routing=student_2024
```

---

### 🚨 Interview Warning

If you indexed with routing and search without routing:

* Elasticsearch searches all shards.
* Performance degrades.

---

## 4️⃣ Shards – Critical Interview Concepts

### 🔹 Primary Shards

* Store actual data
* Number defined at index creation
* CANNOT be changed later

```json
PUT student
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  }
}
```

---

### 🔥 Why Shards Cannot Be Changed?

Because:

```
hash(_routing) % number_of_shards
```

If shard count changes:

* Hash result changes
* Existing documents would belong to different shards
* Data becomes inconsistent

---

### 🚨 Interview Question

❓ Can we increase primary shards later?

✔ Answer:
No.
You must reindex into a new index with new shard count.

---

## 5️⃣ Writing vs Searching Optimization

| Operation      | Behavior                                               |
| -------------- | ------------------------------------------------------ |
| Indexing       | Document goes to ONE primary shard                     |
| Replication    | Copied to replica shards                               |
| Searching      | Query executed on all shards (unless routing provided) |
| Custom routing | Search limited to specific shard                       |

---

## 6️⃣ Performance Insight (Very Important)

If you have:

* 20 shards
* 1 query

Elasticsearch will:

* Execute query 20 times (one per shard)

More shards ≠ always better.

---

### 🔥 Interview Golden Rule

Too many shards → high overhead
Too few shards → scalability problem

---

## 7️⃣ Real-World Design Advice

✔ Plan shard count carefully
✔ Use routing for multi-tenant systems
✔ Use time-based indices for logs (e.g., yearly/monthly indices)
✔ Reindex if shard strategy changes

---

## 8️⃣ Common Interview Questions

### ❓ How does Elasticsearch decide which shard stores a document?

Using:

```
hash(routing) % primary_shard_count
```

---

### ❓ What happens if shard count increases?

Existing data does NOT redistribute.
Must create new index and reindex.

---

### ❓ Does search always hit all shards?

Yes by default.
No if routing is used.

---

### ❓ What is the role of coordinating node?

* Routes indexing request to correct shard.
* Collects search results from all shards.
* Merges and returns final response.

---

# 🧠 Final Interview Summary

* Elasticsearch uses **deterministic hashing** for shard routing.
* Shard count is immutable.
* Search is distributed across shards.
* Routing optimizes search.
* Poor shard planning = major performance issue.