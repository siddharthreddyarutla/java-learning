# 🗂 1️⃣ Create Database

In MongoDB, database is created lazily.

```js
use ecommerceDB
```

This does NOT immediately create database.

Database is created only when you insert first document.

Example:

```js
db.users.insertOne({ name: "John" })
```

Now `ecommerceDB` exists.

---

# 📁 2️⃣ Create Collection

## Method 1 — Auto Creation

```js
db.users.insertOne({ name: "John" })
```

Collection is created automatically.

---

## Method 2 — Explicit Creation (Recommended in Production)

```js
db.createCollection("users")
```

You can also define options:

```js
db.createCollection("logs", {
   capped: true,
   size: 5242880,
   max: 5000
})
```

### Capped Collection

* Fixed size
* FIFO behavior
* Used for logs

---

# 📄 3️⃣ Insert Sample Data

```js
db.users.insertMany([
  { name: "Alice", age: 25 },
  { name: "Bob", age: 30 }
])
```

---

# 📌 4️⃣ Create Indexes

Indexes are critical for performance.

---

## 🔹 Single Field Index

```js
db.users.createIndex({ age: 1 })
```

`1` → ascending
`-1` → descending

---

## 🔹 Compound Index

```js
db.users.createIndex({ age: 1, name: 1 })
```

Important:
Order matters (left prefix rule).

---

## 🔹 Unique Index

```js
db.users.createIndex({ email: 1 }, { unique: true })
```

Prevents duplicate values.

---

## 🔹 TTL Index (Auto Expiry)

```js
db.sessions.createIndex(
   { createdAt: 1 },
   { expireAfterSeconds: 3600 }
)
```

Documents auto-delete after 1 hour.

---

## 🔹 Text Index

```js
db.products.createIndex({ description: "text" })
```

Used for full-text search.

---

# 🔎 5️⃣ View Indexes

```js
db.users.getIndexes()
```

Default index always exists:

```
{ _id: 1 }
```

Mongo automatically creates `_id` index.

---

# 🧠 6️⃣ What Happens Internally When Creating Index?

When you run:

```js
db.users.createIndex({ age: 1 })
```

Mongo:

1. Scans entire collection
2. Builds B-tree structure
3. Stores index separately
4. Future inserts maintain index

If collection is large:
→ Index build can be heavy operation.

---

# ⚠️ Production Note

On large collections:

* Use background index build (older versions)
* Or build during low traffic
* Index creation locks metadata

---

# 🎯 Minimal Commands You Must Remember for Interview

```js
use dbName
db.createCollection("name")
db.collection.insertOne({})
db.collection.createIndex({})
db.collection.getIndexes()
db.collection.dropIndex("indexName")
```