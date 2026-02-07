

# 🔥 STEP 14: SUBQUERIES, EXISTS, IN

## Optimizer Behavior, Pitfalls & Interview Truths

> This step is about **how MySQL EXECUTES**, not how SQL looks.

---

## 14.1 Types of Subqueries (ONLY WHAT MATTERS)

### 1️⃣ Scalar subquery

Returns **one value**

```sql
SELECT *
FROM orders
WHERE customer_id = (
  SELECT id FROM customers WHERE email='a@x.com'
);
```

✔ Usually safe
✔ Often optimized into a join

---

### 2️⃣ IN subquery

Returns **multiple values**

```sql
SELECT *
FROM orders
WHERE customer_id IN (
  SELECT id FROM customers WHERE country='IN'
);
```

⚠️ Dangerous on large result sets

---

### 3️⃣ EXISTS subquery

Checks **existence only**

```sql
SELECT *
FROM customers c
WHERE EXISTS (
  SELECT 1 FROM orders o WHERE o.customer_id = c.id
);
```

✔ Short-circuits
✔ Usually faster

---

## 14.2 IN vs EXISTS (THE CORE DIFFERENCE)

| Aspect         | IN                     | EXISTS               |
| -------------- | ---------------------- | -------------------- |
| Evaluation     | Builds full result set | Stops at first match |
| Memory         | High                   | Low                  |
| Large subquery | ❌ bad                  | ✅ good               |
| NULL handling  | Tricky                 | Safe                 |

💬 Interview line:

> “EXISTS short-circuits; IN materializes.”

---

## 14.3 Why `IN` Can Be SLOW (VERY IMPORTANT)

```sql
SELECT *
FROM orders
WHERE customer_id IN (
  SELECT id FROM customers WHERE country='IN'
);
```

What MySQL may do:

1. Execute subquery fully
2. Store results in temp structure
3. Compare for each row

🚨 Large subquery = heavy memory + CPU

---

## 14.4 EXISTS Short-Circuit (KEY ADVANTAGE)

```sql
SELECT *
FROM customers c
WHERE EXISTS (
  SELECT 1 FROM orders o WHERE o.customer_id = c.id
);
```

Execution:

* For each customer
* Stop at **first matching order**

💬 Interview killer:

> “EXISTS stops scanning once a match is found.”

---

## 14.5 NULL TRAP (VERY COMMON INTERVIEW QUESTION)

```sql
SELECT * FROM orders
WHERE customer_id IN (SELECT customer_id FROM blacklist);
```

If subquery returns:

```
(1, 2, NULL)
```

➡️ Result = **empty**

Why?

* `x IN (1,2,NULL)` → UNKNOWN

---

### EXISTS does NOT have this issue

```sql
WHERE EXISTS (
  SELECT 1 FROM blacklist b WHERE b.customer_id = o.customer_id
);
```

✔ NULL-safe

💬 Interview line:

> “IN behaves badly with NULLs; EXISTS does not.”

---

## 14.6 Correlated vs Non-Correlated Subqueries

### Correlated (executed per row)

```sql
SELECT *
FROM customers c
WHERE EXISTS (
  SELECT 1 FROM orders o WHERE o.customer_id = c.id
);
```

### Non-correlated (executed once)

```sql
SELECT *
FROM orders
WHERE customer_id IN (1,2,3);
```

💡 Correlated subqueries **can be optimized** if indexed.

---

## 14.7 MySQL Optimizer Tricks (IMPORTANT)

Modern MySQL (8.0):
✔ Converts subqueries into joins
✔ Uses semi-joins
✔ Pushes predicates

But:
❌ Not always
❌ Data distribution matters

💬 Interview-safe line:

> “The optimizer may rewrite subqueries, but you shouldn’t rely on it blindly.”

---

## 14.8 EXISTS vs JOIN (TRICK QUESTION)

```sql
SELECT DISTINCT c.*
FROM customers c
JOIN orders o ON o.customer_id = c.id;
```

vs

```sql
SELECT *
FROM customers c
WHERE EXISTS (
  SELECT 1 FROM orders o WHERE o.customer_id = c.id
);
```

Key difference:

* JOIN may produce duplicates
* EXISTS never does

💬 Interview line:

> “EXISTS is semantically a semi-join.”

---

## 14.9 WHEN TO USE WHAT (MEMORIZE)

| Scenario                      | Best Choice |
| ----------------------------- | ----------- |
| Small static list             | IN          |
| Large dynamic result          | EXISTS      |
| NULLs possible                | EXISTS      |
| Need columns from both tables | JOIN        |
| Existence check only          | EXISTS      |

---

## 14.10 REAL INTERVIEW SCENARIO

**Q:** Why is this slow?

```sql
SELECT *
FROM orders
WHERE customer_id IN (
  SELECT customer_id FROM blacklist
);
```

**Answer:**

> “Because IN materializes the entire subquery and NULLs may break logic; EXISTS with proper indexing is better.”

---

## 14.11 EXPLAIN SIGNALS TO WATCH

| Signal             | Meaning               |
| ------------------ | --------------------- |
| DEPENDENT SUBQUERY | Executed per row      |
| Using temporary    | Subquery materialized |
| SEMI-JOIN          | Optimized EXISTS      |

💬 Interview line:

> “I check EXPLAIN to see if subqueries are materialized.”

---

## 🎯 INTERVIEW ONE-LINERS (USE THESE)

* “EXISTS short-circuits; IN materializes.”
* “IN behaves badly with NULLs.”
* “EXISTS is a semi-join.”
* “Optimizer may rewrite subqueries, but indexes still matter.”

---

## ✅ STEP 14 CHECKPOINT

You should now confidently explain:

✔ IN vs EXISTS
✔ Why EXISTS is safer
✔ NULL pitfalls
✔ When JOIN is better
✔ How optimizer treats subqueries

If yes → **you’re very strong in SQL interviews**

---

## 🚀 NEXT STEP

Next we go into **final performance & ops topics**:

👉 **STEP 15: LOGS — REDO, UNDO, BINLOG (CRASH RECOVERY & CDC)**

Say **“Start Step 15”** when ready 💪
