> *If gap lock already locks between 10 and 20, what extra does next-key lock give? Why is it useful? Where does it avoid inserts?*

---

# 🔑 First: One Important Correction (Key to Understanding)

👉 **Gap lock does NOT lock existing rows**
👉 **Row lock does NOT lock gaps**

So **neither alone is enough**.

**Next-key lock exists because MySQL must lock BOTH.**

---

# 🧠 Setup (Same Table, Same Data)

```text
id (PK)
---------
5
10
20
30
```

Gaps are:

```
(5,10)  (10,20)  (20,30)
```

---

# 🔹 CASE 1: GAP LOCK ONLY (NO ROW LOCK)

Imagine **only gap lock exists**.

```sql
-- T1
SELECT * FROM orders
WHERE id BETWEEN 10 AND 20
LOCK IN SHARE MODE;
```

What is locked:

* 🔒 Gap (10,20)

What is **NOT** locked:

* ❌ Row 10
* ❌ Row 20

---

## What can go wrong?

### T2 does:

```sql
UPDATE orders SET status='PAID' WHERE id=10;
```

✔ Allowed (row 10 not locked)

### T1 reads again:

```sql
SELECT * FROM orders WHERE id BETWEEN 10 AND 20;
```

Row 10 has changed → **non-repeatable read**

❌ Isolation broken

👉 **Gap lock alone is NOT enough**

---

# 🔹 CASE 2: ROW LOCK ONLY (NO GAP LOCK)

Imagine **only row locks exist**.

```sql
-- T1
SELECT * FROM orders
WHERE id BETWEEN 10 AND 20
FOR UPDATE;
```

What is locked:

* 🔒 Row 10
* 🔒 Row 20

What is **NOT** locked:

* ❌ Gap (10,20)

---

## What can go wrong?

### T2 does:

```sql
INSERT INTO orders VALUES (15);
```

✔ Allowed (gap not locked)

### T1 reads again:

```sql
SELECT * FROM orders WHERE id BETWEEN 10 AND 20;
```

Now row 15 appears → **phantom row**

❌ Isolation broken

👉 **Row lock alone is NOT enough**

---

# 🔥 CASE 3: NEXT-KEY LOCK (ROW + GAP) ✅

This is why **next-key lock exists**.

```sql
-- T1
SELECT * FROM orders
WHERE id BETWEEN 10 AND 20
FOR UPDATE;
```

What is locked:

* 🔒 Row 10
* 🔒 Row 20
* 🔒 Gap (10,20)

---

## Now test again

### T2 tries update:

```sql
UPDATE orders SET status='PAID' WHERE id=10;
```

❌ Blocked (row lock)

### T2 tries insert:

```sql
INSERT INTO orders VALUES (15);
```

❌ Blocked (gap lock)

✔ No updates
✔ No inserts
✔ No phantoms
✔ Repeatable reads guaranteed

---

# 🎯 THIS IS THE CORE ANSWER

### Why next-key lock is useful?

Because:

* **Gap lock alone** → doesn’t protect existing rows
* **Row lock alone** → doesn’t protect new rows
* **Next-key lock** → protects BOTH

---

# 📌 Where does next-key avoid inserts?

👉 **In the gap immediately after each locked row**

For row `10`, next-key lock covers:

```
[10, next_record)
```

So inserting **15 is blocked**.

---

# 🧠 One Diagram (Read Slowly)

```
5   [10 —— GAP —— 20]   30
     ↑            ↑
   row lock     row lock
   + gap lock between
```

---

# 🧠 FINAL ONE-LINER (INTERVIEW PERFECT)

> “Gap locks prevent inserts, row locks prevent updates, and next-key locks combine both to guarantee repeatable reads without phantoms.”