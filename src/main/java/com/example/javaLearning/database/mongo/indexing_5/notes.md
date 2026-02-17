Excellent 🔥
Now we go into one of the **most important MongoDB interview topics**.

This is where mid-level candidates fail and senior candidates shine.

---

# 🏅 STEP 4 — Indexing Deep Dive (Concept + Interview Thinking)

In MongoDB, indexes are:

* B-Tree based
* Stored separately from documents
* Used by the query planner to avoid collection scans

If you understand indexing deeply → you can debug performance issues confidently.

---

# 1️⃣ Why Indexes Matter in Mongo

Without index:

```
COLLSCAN → Scan entire collection
```

With index:

```
IXSCAN → Traverse B-tree → Fetch document
```

Performance difference:
O(n) → O(log n)

Same principle as MySQL.

---

# 2️⃣ Single Field Index

```js
db.users.createIndex({ age: 1 })
```

* 1 = ascending
* -1 = descending

Important:
Ascending vs descending only matters for sorting optimization.

---

# 3️⃣ Compound Index (VERY IMPORTANT)

```js
db.users.createIndex({ age: 1, name: 1 })
```

Mongo follows **Left Prefix Rule**:

Index: `{ age:1, name:1 }`

Works for:

* age
* age + name

Does NOT efficiently work for:

* name alone

Same concept as MySQL composite index.

---

## 🔹 Sort Optimization

If query:

```js
find({ age: 30 }).sort({ name: 1 })
```

Compound index `{ age:1, name:1 }` helps both filter and sort.

If order mismatched → index may not be used for sorting.

---

# 4️⃣ Multikey Index (Arrays)

If field contains array:

```js
{
  skills: ["Java","Mongo"]
}
```

Mongo creates one index entry per element.

This is called **Multikey index**.

Important restriction:

You cannot create compound index on multiple array fields.

Bad:

```js
{ skills: 1, tags: 1 } // both arrays
```

Interview favorite question.

---

# 5️⃣ Unique Index

```js
db.users.createIndex(
   { email: 1 },
   { unique: true }
)
```

Prevents duplicate values.

Important:
Unique constraint works at index level.

---

# 6️⃣ Sparse Index

```js
db.users.createIndex(
   { phone: 1 },
   { sparse: true }
)
```

Only indexes documents that contain the field.

Without sparse:
Documents missing field get indexed as null.

Use case:
Optional fields.

---

# 7️⃣ Partial Index (Advanced & Powerful)

```js
db.users.createIndex(
   { age: 1 },
   { partialFilterExpression: { active: true } }
)
```

Only indexes documents matching condition.

Much better than sparse in many cases.

Use case:

* Active users only
* Soft deleted filter

---

# 8️⃣ TTL Index (Auto Expiry)

```js
db.sessions.createIndex(
   { createdAt: 1 },
   { expireAfterSeconds: 3600 }
)
```

Mongo automatically deletes expired documents.

Used for:

* Sessions
* Logs
* Temporary tokens

Important:
TTL works only on date field.

---

# 9️⃣ Text Index

```js
db.products.createIndex({ description: "text" })
```

Used for full-text search.

Limitations:

* Only one text index per collection
* Not as powerful as Elasticsearch

Interview maturity:
Mongo text search is basic. For advanced search → use Elasticsearch.

---

# 🔟 Hashed Index (Sharding Use Case)

```js
db.users.createIndex({ userId: "hashed" })
```

Used for:

* Even data distribution in sharding

Not good for range queries.

---

# 1️⃣1️⃣ Covered Queries (Performance Gold)

If index contains:

* Filter fields
* Projection fields

Mongo can avoid fetching document.

Example:

Index:

```
{ age:1, name:1 }
```

Query:

```
find({age:30}, {name:1, _id:0})
```

→ Covered query.

No FETCH stage.

---

# 1️⃣2️⃣ Explain Plan Reading (Critical Skill)

```js
db.users.find({age:30}).explain("executionStats")
```

Look at:

* stage
* COLLSCAN vs IXSCAN
* totalDocsExamined
* totalKeysExamined
* nReturned

Healthy query:

```
totalDocsExamined ≈ nReturned
```

Bad query:

```
totalDocsExamined >> nReturned
```

---

# 1️⃣3️⃣ Index Selectivity

High selectivity = good index.

Example:

Index on gender (male/female)
→ Bad selectivity.

Index on email
→ Excellent selectivity.

Interviewers test this concept.

---

# 1️⃣4️⃣ Over-Indexing Problem

Too many indexes cause:

* Slower writes
* More memory usage
* Bigger disk size
* Slower insert/update

Every insert updates all indexes.

Tradeoff awareness = senior mindset.

---

# 🎯 Step 4 Mastery Checklist

You are strong if you understand:

✔ Single vs compound index
✔ Left prefix rule
✔ Multikey behavior
✔ Partial vs sparse
✔ TTL index
✔ Text limitations
✔ Hashed index purpose
✔ Covered queries
✔ Explain plan analysis
✔ Selectivity concept
✔ Write overhead of indexes
