## The Core Problem

> **Two managers try to approve the same leave row at the same time**

Example:

```text
leave_request
id = 101
status = PENDING
```

Manager A → clicks **Approve**
Manager B → clicks **Approve** at the same time

### What can go wrong?

* Both read `PENDING`
* Both update to `APPROVED`
* System thinks **two approvals happened**
* Or worse: one overwrites another silently

This is called a **lost update problem**.

---

## High-Level Answer (Senior-Level)

Databases handle this using a **combination of**:

1. **Transactions**
2. **Row-level locking**
3. **Isolation levels**
4. **MVCC (for reads)**

👉 For *updates to the same row*, **locking is the key mechanism**.

Let’s break this down slowly.

---

## 1️⃣ Transactions Create a Boundary

Each manager’s action runs inside a **transaction**:

```sql
START TRANSACTION;
UPDATE leave_request
SET status = 'APPROVED'
WHERE id = 101 AND status = 'PENDING';
COMMIT;
```

This already introduces **control**:

* Either the whole update succeeds
* Or it fails

But transactions alone are not enough.

---

## 2️⃣ Row-Level Locks (The Real Hero Here)

In **InnoDB**, when you UPDATE a row:

> 🔒 MySQL places an **exclusive row lock (X-lock)** on that row.

### What happens step-by-step:

### Timeline

```
Time → →
Manager A starts transaction
Manager A updates row id=101
→ Row is LOCKED

Manager B starts transaction
Manager B tries to update row id=101
→ BLOCKED (waits)
```

Manager B **cannot update** until:

* Manager A commits or rolls back

This guarantees:
✅ Only ONE writer at a time for a row

---

## 3️⃣ What Manager B Sees (Important)

After Manager A commits:

* The row is now `APPROVED`
* Manager B’s UPDATE runs
* Condition `status = 'PENDING'` fails
* **0 rows updated**

This is **intentional and correct behavior**.

Your application can then:

* Detect `rowsAffected = 0`
* Show: “Already approved by someone else”

📌 **This pattern is extremely common in real systems**

---

## 4️⃣ Why Reads Don’t Block Writes (MVCC preview)

Now, what if:

* Manager B is just *viewing* the leave, not approving?

MySQL uses **MVCC**:

* Reads don’t block writes
* Writers don’t block reads

Manager B sees a **snapshot** of the row.

We’ll deep-dive MVCC later — for now:

> Writes lock rows, reads use snapshots.

---

## 5️⃣ What If We DIDN’T Have Locks? (Why DBs Are Hard)

Without row-level locks:

* Both updates run simultaneously
* Last commit wins
* Data corruption without error

This is why:

> **Concurrency control is the hardest part of databases**

---

## 6️⃣ Why This Is NOT Done in Application Code

You might think:

> “I’ll fetch row, check status, then update”

That fails because:

* Two threads can read `PENDING` simultaneously
* Race condition at application level

👉 **Concurrency must be enforced inside the database**

---

## 7️⃣ Senior Engineer Interview Answer (Perfect Form)

If interviewer asks:

> “How does MySQL handle concurrent updates to the same row?”

Answer:

> “InnoDB uses row-level exclusive locks for updates. When one transaction updates a row, other transactions trying to update the same row are blocked until the lock is released, preventing lost updates.”

Bonus:

> “Combined with conditional updates and checking affected rows, this ensures correctness.”

---

## 8️⃣ Key Takeaway (Lock This In)

* **Writes = locks**
* **Only one writer per row**
* **Others wait**
* **MVCC handles reads**
* **Application checks affected rows**

This exact mechanism will reappear when we study:

* Deadlocks
* Isolation levels
* Gap locks
* Phantom reads

---