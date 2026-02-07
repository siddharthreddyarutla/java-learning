# 🔥 STEP 16: MYSQL REPLICATION

## Flow, Lag, Consistency & Failover (INTERVIEW + PROD)

> Replication = **scale reads + availability**, not free consistency.

---

## 🧠 Big Picture (MEMORIZE THIS FLOW)

```
Primary
  └─ writes → binlog
Replica
  └─ reads binlog → replays
```

Replication is **binlog-driven**, not redo-driven.

💬 Interview line:

> “Replication is logical and asynchronous by default.”

---

## 16.1 REPLICATION COMPONENTS (IMPORTANT)

### On Primary

* **Binlog writer**

### On Replica (old names still asked)

* **IO Thread** → pulls binlog
* **SQL Thread** → applies events

(MySQL 8 also has multi-threaded appliers.)

---

## 16.2 TYPES OF REPLICATION

| Type            | Behavior            |
| --------------- | ------------------- |
| Async (default) | Fast, may lose data |
| Semi-sync       | Safer, slower       |
| Sync            | Rare, expensive     |

### Async

* Primary doesn’t wait
* Replica can lag

### Semi-sync

* Primary waits for **one replica ACK**
* Reduces data loss window

💬 Interview line:

> “Async replication favors performance over consistency.”

---

## 16.3 REPLICATION LAG (VERY COMMON QUESTION)

### What is lag?

Replica is **behind primary** in applying binlog events.

---

### Causes of lag

✔ Heavy writes on primary
✔ Long transactions
✔ Large batch updates
✔ Slow replica disk
✔ Single SQL thread

💬 Interview killer:

> “Replication lag usually comes from slow apply, not slow transfer.”

---

## 16.4 HOW TO DETECT LAG

```sql
SHOW SLAVE STATUS\G
```

Key fields:

* `Seconds_Behind_Master`
* `Relay_Log_Space`

📌 Value is **approximate**, not exact.

---

## 16.5 READS FROM REPLICAS (TRAP)

Problem:

```text
Write → Read immediately from replica
```

May return **stale data**.

---

### Solutions

✔ Read-after-write from primary
✔ Use semi-sync
✔ Application-level routing
✔ GTID wait

💬 Interview line:

> “Replicas are eventually consistent.”

---

## 16.6 BINLOG FORMAT (IMPORTANT)

| Format    | Issue             |
| --------- | ----------------- |
| STATEMENT | Non-deterministic |
| ROW       | Safe              |
| MIXED     | Hybrid            |

📌 Modern MySQL → **ROW-based**

💬 Interview line:

> “Row-based binlog is safest for replication.”

---

## 16.7 FAILOVER (HIGH-SIGNAL TOPIC)

### What is failover?

Promoting a replica when primary dies.

---

### Basic steps (conceptual)

1. Stop writes
2. Pick most up-to-date replica
3. Promote it
4. Redirect traffic

🚨 Risks:

* Data loss (async)
* Split brain

---

## 16.8 HOW GTID HELPS (BONUS POINTS)

GTID = Global Transaction ID

Benefits:
✔ Easier failover
✔ No guessing binlog position
✔ Safer topology changes

💬 Interview brownie:

> “GTID simplifies failover and replica management.”

---

## 16.9 MULTI-THREADED REPLICATION

Old:

* Single SQL thread → bottleneck

Now:

* Parallel apply by schema / transaction

Reduces:
✔ Lag
✔ Catch-up time

---

## 16.10 COMMON PROD MISTAKES

🚨 Writing long transactions
🚨 Reading critical data from replicas
🚨 Ignoring lag monitoring
🚨 No failover plan

---

## 16.11 REAL INTERVIEW SCENARIO (MEMORIZE)

**Q:** Why did my replica fall behind during deployment?

**Answer:**

> “A long transaction or batch update blocked the SQL thread, causing replication lag.”

---

## 🎯 INTERVIEW ONE-LINERS

* “Replication uses binlog, not redo.”
* “Async replication can lose data.”
* “Replication lag is expected.”
* “Replicas are eventually consistent.”
* “GTID simplifies failover.”

---

## ✅ STEP 16 CHECKPOINT

You should now explain:

✔ How replication works
✔ Why lag happens
✔ How to detect lag
✔ Async vs semi-sync
✔ Failover basics

If yes → **you are production-ready**