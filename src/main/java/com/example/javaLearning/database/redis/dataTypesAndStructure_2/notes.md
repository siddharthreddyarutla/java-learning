Perfect — thanks for clarifying 👍
Now we’ll **slow down and go deep**, exactly like you want:

* **Internal storage**
* **Encodings**
* **ALL important commands**
* **Time complexity**
* **Why Redis chose that structure**
* **Interview traps**

This is the level where you can **whiteboard Redis** in interviews.

I’ll go **one data type at a time**, starting with **String → List → Set → Sorted Set → Hash**.

---

# 🔥 Redis Data Types — DEEP INTERNAL GUIDE

> Redis does **not** store “just values”.
> Every value is stored as a **Redis Object**:

```
redisObject {
  type
  encoding
  ptr (actual data)
}
```

---

## 1️⃣ STRING (Most fundamental)

### Logical view

```text
key → "value"
```

---

## 1.1 Internal Representation

Redis strings are stored as **SDS (Simple Dynamic String)**

### SDS structure

```c
struct sdshdr {
  int len;      // used length
  int alloc;    // allocated size
  char buf[];   // actual data
}
```

### Why SDS is better than C strings

| Problem in C string | SDS solution      |
| ------------------- | ----------------- |
| strlen is O(n)      | len stored → O(1) |
| Buffer overflow     | alloc tracked     |
| Frequent realloc    | pre-allocation    |
| Binary unsafe       | Binary safe       |

---

## 1.2 String Encodings

Redis chooses encoding automatically:

| Encoding | When used                 |
| -------- | ------------------------- |
| int      | Small integers            |
| embstr   | Short strings (≤44 bytes) |
| raw      | Long strings              |

👉 Interview gold:

> Redis optimizes strings by using different encodings based on size and type.

---

## 1.3 Commands & Complexity

### Basic

| Command | Description | Complexity |
| ------- | ----------- | ---------- |
| SET     | Set value   | O(1)       |
| GET     | Get value   | O(1)       |
| DEL     | Delete      | O(1)       |

### Numeric

| Command     | Complexity |
| ----------- | ---------- |
| INCR / DECR | O(1)       |
| INCRBY      | O(1)       |

### String ops

| Command  | Complexity     |
| -------- | -------------- |
| APPEND   | Amortized O(1) |
| STRLEN   | O(1)           |
| GETRANGE | O(n)           |
| SETRANGE | O(n)           |

---

## 1.4 Interview traps

❓ Is string always O(1)?
✔ Mostly yes, except substring operations.

---

# 2️⃣ LIST (Ordered collection)

### Logical view

```text
[a, b, c]
```

---

## 2.1 Internal Representation

Modern Redis uses **QuickList**

### QuickList =

> Linked List of Listpacks (arrays)

```
QuickList
 ├── listpack [a b c]
 ├── listpack [d e f]
```

### Why not plain linked list?

* Poor CPU cache locality
* High memory overhead

### Why not plain array?

* Insert at head is expensive

👉 QuickList balances **memory + speed**.

---

## 2.2 List Encodings

| Encoding  | When        |
| --------- | ----------- |
| listpack  | Small lists |
| quicklist | Large lists |

---

## 2.3 Commands & Complexity

### Insert / Remove

| Command       | Complexity |
| ------------- | ---------- |
| LPUSH / RPUSH | O(1)       |
| LPOP / RPOP   | O(1)       |

### Read

| Command | Complexity |
| ------- | ---------- |
| LINDEX  | O(n)       |
| LRANGE  | O(n)       |

### Modify

| Command | Complexity |
| ------- | ---------- |
| LSET    | O(n)       |
| LREM    | O(n)       |

---

## 2.4 Interview traps

❓ Why LRANGE is slow?
✔ Needs to traverse list.

❓ Can list be used as queue?
✔ Yes (LPUSH + RPOP)

---

# 3️⃣ SET (Unordered unique collection)

### Logical view

```text
{1, 2, 3}
```

---

## 3.1 Internal Representation

Redis uses **two encodings**:

### 1️⃣ IntSet (small integers only)

* Sorted array of integers
* Binary search

### 2️⃣ Hash Table

* When:

    * Non-integer
    * Large set

Redis auto-converts between them.

---

## 3.2 Why two encodings?

| Encoding   | Advantage        |
| ---------- | ---------------- |
| IntSet     | Memory efficient |
| Hash table | Fast lookups     |

---

## 3.3 Commands & Complexity

### Core

| Command   | Complexity |
| --------- | ---------- |
| SADD      | O(1)       |
| SREM      | O(1)       |
| SISMEMBER | O(1)       |
| SCARD     | O(1)       |

### Set ops

| Command | Complexity |
| ------- | ---------- |
| SUNION  | O(n)       |
| SINTER  | O(n*m)     |
| SDIFF   | O(n)       |

---

## 3.4 Interview traps

❓ How Redis ensures uniqueness?
✔ Hash table / IntSet.

❓ Are set operations expensive?
✔ Yes for large sets.

---

# 4️⃣ SORTED SET (🔥 MOST IMPORTANT)

### Logical view

```text
(score, member)
```

---

## 4.1 Internal Representation

Redis uses **TWO data structures**:

### 1️⃣ Hash Table

```text
member → score
```

✔ O(1) lookup

### 2️⃣ Skip List

```text
sorted by score
```

✔ O(log n) range queries

---

## 4.2 Why Skip List (not tree)?

| Skip List      | Balanced Tree    |
| -------------- | ---------------- |
| Simple         | Complex          |
| Fast enough    | Slightly faster  |
| Redis-friendly | Hard to maintain |

---

## 4.3 Commands & Complexity

### Insert / Remove

| Command | Complexity |
| ------- | ---------- |
| ZADD    | O(log n)   |
| ZREM    | O(log n)   |

### Query

| Command   | Complexity   |
| --------- | ------------ |
| ZSCORE    | O(1)         |
| ZRANK     | O(log n)     |
| ZRANGE    | O(log n + m) |
| ZREVRANGE | O(log n + m) |

---

## 4.4 Interview traps

❓ Why sorted set is perfect for leaderboard?
✔ Order + fast lookup.

❓ Why not list?
✔ Sorting list is expensive.

---

# 5️⃣ HASH (Key–Field–Value)

### Logical view

```text
user:1 → {name: A, age: 25}
```

---

## 5.1 Internal Representation

Two encodings:

| Encoding   | When       |
| ---------- | ---------- |
| listpack   | Small hash |
| hash table | Large hash |

Redis auto-upgrades.

---

## 5.2 Why Hash is memory efficient

Instead of:

```text
user:1:name → A
user:1:age → 25
```

You store:

```text
user:1 → {name, age}
```

Less keys → less memory.

---

## 5.3 Commands & Complexity

| Command | Complexity |
| ------- | ---------- |
| HSET    | O(1)       |
| HGET    | O(1)       |
| HMSET   | O(n)       |
| HGETALL | O(n)       |
| HEXISTS | O(1)       |
| HDEL    | O(1)       |

---

## 5.4 Interview traps

❓ Hash vs String?
✔ Hash is better for objects.

❓ HGETALL cost?
✔ O(n), can be dangerous.

---

# 🧠 Final Mental Model (IMPORTANT)

| Type       | Structure       | Fast Because    |
| ---------- | --------------- | --------------- |
| String     | SDS             | O(1) ops        |
| List       | QuickList       | Head/tail ops   |
| Set        | Hash/IntSet     | O(1) lookup     |
| Sorted Set | Hash + SkipList | Order + speed   |
| Hash       | Listpack/Hash   | Compact storage |
