# 1️⃣ Create an Index

### Basic Index Creation

```json
PUT student
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "age": {
        "type": "integer"
      },
      "last_name": {
        "type": "keyword"
      }
    }
  }
}
```

### ✅ What Happens?

* Creates index `student`
* 1 primary shard
* 1 replica shard
* Defines structure (schema)

---

# 2️⃣ Insert Documents (POST)

## 🔹 Option 1: Auto-generated ID

```json
POST student/_doc
{
  "name": "Rahul",
  "age": 5
}
```

Elasticsearch auto-generates `_id`.

---

## 🔹 Option 2: Manual ID (PUT)

```json
PUT student/_doc/1
{
  "name": "Mohit",
  "age": 30
}
```

Here:

* `_id = 1`
* If ID exists → document gets replaced

---

# 3️⃣ Fetch Documents

## 🔹 Get by ID

```json
GET student/_doc/1
```

---

## 🔹 Search All Documents

```json
GET student/_search
{
  "query": {
    "match_all": {}
  }
}
```

---

## 🔹 Search with Condition (Bool + Must)

```json
GET student/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "name": "Rahul" }},
        { "range": { "age": { "gte": 5 }}}
      ]
    }
  }
}
```

---

# 4️⃣ Update Document (Partial Update)

## 🔹 Simple Field Update

```json
POST student/_update/1
{
  "doc": {
    "age": 31
  }
}
```

✅ Only updates `age`
❌ Does NOT replace entire document

---

## 🔹 Add New Field

```json
POST student/_update/1
{
  "doc": {
    "last_name": "Sharma"
  }
}
```

Now document becomes:

```json
{
  "name": "Mohit",
  "age": 31,
  "last_name": "Sharma"
}
```

---

# 5️⃣ Update Using Script

## 🔹 Increment Age Using Script

```json
POST student/_update/1
{
  "script": {
    "source": "ctx._source.age += 1",
    "lang": "painless"
  }
}
```

---

## 🔹 Multi-line Script (Triple Quotes)

```json
POST student/_update/1
{
  "script": {
    "source": """
      ctx._source.age = 25;
      ctx._source.last_name = 'Verma';
    """,
    "lang": "painless"
  }
}
```

✅ Used for complex logic
✅ Can include conditions

---

## 🔹 Conditional Script

```json
POST student/_update/1
{
  "script": {
    "source": """
      if (ctx._source.age < 18) {
        ctx._source.status = 'minor';
      } else {
        ctx._source.status = 'adult';
      }
    """,
    "lang": "painless"
  }
}
```

---

# 6️⃣ Replace Entire Document (PUT vs UPDATE)

### 🔹 Replace (Full Overwrite)

```json
PUT student/_doc/1
{
  "name": "Amit",
  "age": 22
}
```

⚠️ This removes old fields like `last_name`.

---

### 🔹 Partial Update (Safer)

```json
POST student/_update/1
{
  "doc": {
    "age": 23
  }
}
```

Only modifies specific field.

---

# 7️⃣ Delete Document

## 🔹 Delete by ID

```json
DELETE student/_doc/1
```

---

## 🔹 Delete by Query

```json
POST student/_delete_by_query
{
  "query": {
    "range": {
      "age": {
        "lt": 10
      }
    }
  }
}
```

Deletes all students younger than 10.

---

# 8️⃣ Update Multiple Documents

```json
POST student/_update_by_query
{
  "script": {
    "source": "ctx._source.age += 2",
    "lang": "painless"
  },
  "query": {
    "match_all": {}
  }
}
```

Updates ALL documents.

---

# 9️⃣ Check Index Details

```json
GET student
```

---

# 🔟 Delete Index

```json
DELETE student
```

⚠️ Deletes everything inside index.

---

# 🔥 Interview-Level Differences

| Operation               | Behavior                        |
| ----------------------- | ------------------------------- |
| `POST index/_doc`       | Create document (auto ID)       |
| `PUT index/_doc/id`     | Create or replace full document |
| `POST index/_update/id` | Partial update                  |
| `_update_by_query`      | Update multiple docs            |
| `_delete_by_query`      | Delete multiple docs            |

---

# 🚀 Important Real-World Tips

### ✔ Always use update instead of PUT when modifying few fields

### ✔ Use scripts for atomic updates (like increment counters)

### ✔ Be careful with `_delete_by_query` in production

### ✔ Understand shard + replica impact on performance

