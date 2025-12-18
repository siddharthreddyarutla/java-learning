# 📘 Elasticsearch Complete Notes

This document explains **Elasticsearch concepts** in detail:  
indexes, shards, mappings, queries, aggregations, storage, search internals, and practical examples.  

---

## 🔹 1. What is Elasticsearch?
- Elasticsearch (**ES**) is a **distributed search and analytics engine** built on **Apache Lucene**.  
- Stores **JSON documents** and allows:
  - Full-text search
  - Structured queries (filters, ranges)
  - Aggregations (analytics)
  - Near real-time results

✅ Use cases: logging, analytics, metrics, full-text search, recommendation engines.

---

## 🔹 2. Core Concepts
### Index
- Equivalent to a **database**.
- Contains documents.
- Example: `user_leave_attendance`.

### Document
- Equivalent to a **row** in RDBMS.
- Stored as JSON.
- Example:
```json
{
  "USER_ID": 123,
  "STATUS": "PRESENT",
  "MARKED_ON": "2025-08-19",
  "METADATA": {
    "CLOCK_IN_SOURCE": "MOBILE",
    "COMMENT": "Late check-in"
  }
}
````

### Field

* Equivalent to a **column**.
* Example: `USER_ID`, `STATUS`, `MARKED_ON`.

### Shard

* A **partition of an index** (implemented as Lucene index).
* Types: **primary** (original data), **replica** (copy).

### Node

* An **Elasticsearch server instance** (Java process).
* Stores shards.

### Cluster

* A group of nodes working together.

---

## 🔹 3. Shards and Replication

* **Primary shard**: holds original data.
* **Replica shard**: copy of a primary shard.
* Benefits:

    * High availability (failover).
    * Parallel search queries (load balancing).
* Shard allocation uses formula:

  ```
  shard = hash(_routing) % number_of_primary_shards
  ```

---

## 🔹 4. Mapping

* Defines **schema** of an index (field types, analyzers, etc.).
* Created at index creation or dynamically inferred.
* Types of field mappings:

    * **keyword** → exact value, not analyzed (good for IDs, status codes).
    * **text** → analyzed for full-text search (split into tokens).
    * **date** → date/time fields, support range queries.
    * **numeric** → integer, long, float, double.
    * **boolean** → true/false.
    * **flattened** → dynamic key-value pairs (all values treated as strings).
    * **nested** → arrays of objects that need independent querying.
    * **object** → JSON objects (default).

### Example Mapping

```json
PUT user_leave_attendance
{
  "mappings": {
    "properties": {
      "USER_ID": { "type": "long" },
      "STATUS": { "type": "keyword" },
      "MARKED_ON": { "type": "date", "format": "yyyy-MM-dd" },
      "METADATA": { "type": "flattened" }
    }
  }
}
```

---

## 🔹 5. Querying Data

Elasticsearch uses **Query DSL** (JSON-based).

### Basic Queries

* Match all:

```json
{ "query": { "match_all": {} } }
```

* Term query (exact match):

```json
{ "query": { "term": { "STATUS": "PRESENT" } } }
```

* Match query (full-text):

```json
{ "query": { "match": { "NAME": "john doe" } } }
```

### Boolean Queries

```json
{
  "query": {
    "bool": {
      "must": [
        { "term": { "USER_ID": 43649 }},
        { "term": { "ENTITY_TYPE": "CHECKIN_CHECKOUT" }}
      ],
      "filter": [
        { "range": { "MARKED_ON": { "gte": "2025-08-01", "lte": "2025-08-31" }}}
      ]
    }
  }
}
```

---

## 🔹 6. Filters vs Queries

* **Query** → affects relevance score (used for full-text search).
* **Filter** → binary yes/no, faster, cacheable (e.g., `date > X`).

---

## 🔹 7. Sorting and Pagination

```json
{
  "query": { "match_all": {} },
  "sort": [
    { "MARKED_ON": { "order": "desc" }}
  ],
  "from": 0,
  "size": 10
}
```

* `from` = offset.
* `size` = number of results.

---

## 🔹 8. Aggregations

Powerful analytics engine in ES.

### Example: Count by status

```json
{
  "aggs": {
    "by_status": {
      "terms": { "field": "STATUS" }
    }
  }
}
```

### Example: Count by month

```json
{
  "aggs": {
    "by_month": {
      "date_histogram": {
        "field": "MARKED_ON",
        "calendar_interval": "month"
      }
    }
  }
}
```

### Example: Avg working hours

```json
{
  "aggs": {
    "avg_hours": {
      "avg": { "field": "EXPECTED_WORKING_HOURS" }
    }
  }
}
```

---

## 🔹 9. Storage & Search Internals

1. **Indexing**

    * JSON document → analyzed (tokenized if `text` field).
    * Stored in an inverted index (Lucene structure).

2. **Searching**

    * Query → converted into terms.
    * ES finds matching terms in inverted index.
    * Returns matching docs, with relevance scoring (if applicable).

3. **Near Real Time (NRT)**

    * Docs are first stored in memory buffer, then flushed to disk.
    * Refresh interval (default 1s) makes them searchable.

---

## 🔹 10. Important APIs

### Index document

```bash
POST user_leave_attendance/_doc/1
{ "USER_ID": 1, "STATUS": "PRESENT" }
```

### Get document

```bash
GET user_leave_attendance/_doc/1
```

### Update document

```bash
POST user_leave_attendance/_update/1
{ "doc": { "STATUS": "ABSENT" }}
```

### Delete document

```bash
DELETE user_leave_attendance/_doc/1
```

---

## 🔹 11. Useful `_cat` APIs

* List all indices:

```bash
GET /_cat/indices?v
```

* See shards:

```bash
GET /_cat/shards/user_leave_attendance?v
```

* Nodes:

```bash
GET /_cat/nodes?v
```

Example (QA):

```
index                 shard prirep state   docs     store ip          node
user_leave_attendance 1     p      STARTED 12,057,064 2.2gb 10.1.3.90   new-es-node1
user_leave_attendance 1     r      STARTED 12,057,064 2.1gb 10.1.41.198 new-es-node2
user_leave_attendance 0     p      STARTED 12,043,098 2.2gb 10.1.3.90   new-es-node1
user_leave_attendance 0     r      STARTED 12,043,098   2gb 10.1.68.107 new-es-node3
```

* Here:

    * `p` = primary, `r` = replica.
    * Primaries are on node1.
    * Replicas are distributed across node2 and node3.
    * Ensures high availability.

---

## 🔹 12. When to Use Field Types

* **keyword** → IDs, tags, enums (filter/sort/agg).
* **text** → full-text search (names, comments).
* **date** → timestamps (range queries, histograms).
* **numeric** → counts, amounts (aggregations).
* **boolean** → flags.
* **nested** → arrays of objects.
* **flattened** → dynamic key-value pairs, unknown fields.

---

## 🔹 13. Best Practices

* Decide shard count carefully (too many small shards = overhead).
* Always have replicas in production.
* Use filters for exact matches (faster).
* Use `keyword` for aggregations, `text` for search.
* Monitor with `_cat/indices` and `_cat/shards`.

---

## 🔹 14. Analogy

* **Cluster** = whole library.
* **Node** = bookshelf.
* **Shard** = section of books.
* **Replica** = duplicate section on another shelf.
* **Index** = a category of books.
* **Document** = a book.

---

## 🔹 15. Summary

* Elasticsearch = distributed search engine.
* Stores documents in JSON.
* Scales via **shards** and **replicas**.
* Schema controlled via **mapping**.
* Query DSL = powerful JSON-based query language.
* Aggregations = real-time analytics engine.
* Near Real Time (NRT) search \~1s delay.

```

---

✅ This `.md` file covers **everything from scratch to advanced**:  
- Index, doc, shard, replica, node, cluster.  
- Mapping & field types.  
- Query DSL (queries, filters, bool).  
- Sorting, pagination.  
- Aggregations.  
- Storage internals (inverted index).  
- `_cat` APIs for monitoring.  
- Best practices.  

---
```
