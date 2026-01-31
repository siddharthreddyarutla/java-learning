# 🧱 STEP 9: Redis Replication & High Availability (IN DEPTH)

Core interview question:

> ❓ *How does Redis handle failures and scale reliably in production?*

Redis solves this in **layers**, not one feature.

---

## 9.1 Redis Replication (FOUNDATION)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2AYEn12adU0WKkDKxsXgEjJA.png)

![Image](https://deepsource.com/blog/redis-diskless-replication/diskless.png)

### What replication is

* One **Master**
* One or more **Replicas**
* Replicas are **read-only**
* Replication is **asynchronous**

👉 Key idea:

> Replication improves **availability and read scaling**, not durability.

---

## 9.1.1 Why Redis replication is async (IMPORTANT)

Why NOT synchronous?

* Would slow down writes
* Network latency would hurt throughput

Trade-off:

* Small chance of data loss
* Huge performance gain

👉 Interview line:

> Redis chooses asynchronous replication to prioritize performance over strict consistency.

---

## 9.1.2 Full Sync (Initial Replication)

When a replica connects for the first time:

### Step-by-step flow

1. Replica sends `PSYNC ? -1`
2. Master creates **RDB snapshot**
3. Snapshot sent to replica
4. Replica loads snapshot into RAM
5. Master sends buffered writes

This is called **FULL RESYNC**.

❗ Expensive:

* Fork
* Disk I/O
* Network transfer

---

## 9.1.3 Partial Sync (Normal Case)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1200/1%2AcYg8UteFzapsySfj6T-Tsg.png)

![Image](https://systemdesignschool.io/concepts/replication-code-lab/replication-with-redis.png)

Redis avoids full sync using:

### Replication Backlog

* Fixed-size in-memory buffer
* Stores recent write commands

If replica disconnects briefly:

1. Reconnects with offset
2. Master checks backlog
3. Sends only missing commands

👉 Huge performance win.

---

## 9.1.4 What if backlog is insufficient?

* Replica too slow
* Backlog overwritten

Result:
❌ Partial sync fails
✅ Full resync triggered again

---

## 9.1.5 Replication failure scenarios

| Scenario          | Result            |
| ----------------- | ----------------- |
| Replica crash     | Master unaffected |
| Master crash      | Writes stop       |
| Network partition | Replicas stale    |

👉 Replication alone **does NOT handle failover**.

---

# 9.2 Redis Sentinel (AUTOMATIC FAILOVER)

![Image](https://cdn.sanity.io/images/sy1jschh/production/5bbd013a1ea309d98c894c2ee86705faf44e70fa-842x613.jpg)

![Image](https://severalnines.com/sites/default/files/blog/node_6290/image2.png)

Replication gives copies — **Sentinel gives intelligence**.

---

## 9.2.1 What Sentinel actually does

Sentinel is a **separate Redis process** that:

* Monitors Redis nodes
* Detects failures
* Performs **automatic failover**
* Notifies clients

Minimum recommended:

> **3 Sentinel nodes** (quorum)

---

## 9.2.2 How Sentinel detects failure

Two stages:

### 1️⃣ Subjective Down (SDOWN)

* One Sentinel can’t reach master
* Marks it *possibly down*

### 2️⃣ Objective Down (ODOWN)

* Majority of Sentinels agree
* Master is officially down

👉 Prevents false positives.

---

## 9.2.3 Sentinel Failover Flow (VERY IMPORTANT)

![Image](https://miro.medium.com/1%2A6VzRtAcbUE1KOwqgUFea7Q.png)

![Image](https://cdn.sanity.io/images/sy1jschh/production/5bbd013a1ea309d98c894c2ee86705faf44e70fa-842x613.jpg)

Step-by-step:

1. Master marked ODOWN
2. One Sentinel elected leader
3. Best replica selected
4. Replica promoted to master
5. Other replicas reconfigured
6. Clients updated

Failover time:

* Usually **seconds**

---

## 9.2.4 How Sentinel chooses new master

Selection criteria:

* Replica priority
* Replication offset
* Connectivity

Goal:

> Choose the **most up-to-date replica**

---

## 9.2.5 How clients know new master

Clients:

* Query Sentinel
* Get current master address
* Reconnect automatically

👉 Sentinel-aware clients are mandatory.

---

## 9.2.6 Sentinel limitations (INTERVIEW TRAP)

❌ No sharding
❌ Single write master
❌ Not designed for massive scale

👉 Sentinel = **HA**, not **scaling**

---

# 9.3 Redis Cluster (SCALING + HA)

![Image](https://substackcdn.com/image/fetch/%24s_%21lZd6%21%2Cf_auto%2Cq_auto%3Agood%2Cfl_progressive%3Asteep/https%3A%2F%2Fsubstack-post-media.s3.amazonaws.com%2Fpublic%2Fimages%2F903484b2-8c0c-4ce9-b4ab-e967538aeb78_1972x1197.jpeg)

![Image](https://severalnines.com/sites/default/files/blog/node_6303/image1.png)

Cluster solves:

* Data size limits
* Write scalability
* Node-level failures

---

## 9.3.1 Hash Slots (CORE CONCEPT)

Redis Cluster uses:

> **16384 hash slots**

Flow:

```
hash(key) → slot → master node
```

Each master:

* Owns a subset of slots
* Has replicas for HA

---

## 9.3.2 Why 16384 slots?

* Power of 2
* Easy modulo
* Fine-grained rebalancing

Slots move, **keys don’t move individually**.

---

## 9.3.3 Write & Read Flow in Cluster

1. Client hashes key
2. Finds correct slot
3. Sends command to correct node
4. Node replies

If wrong node:

* Redis replies with `MOVED` redirect

---

## 9.3.4 Cluster Failover

![Image](https://yqintl.alicdn.com/a0b317aa8dfb629a97d39e74fbc7c9e75dbe69ac.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1200/1%2Aejl4ZXCUXd57rncaGRufpg.png)

If master fails:

1. Replica promoted
2. Takes ownership of slots
3. Cluster state updated
4. Clients redirected

Fully automatic.

---

## 9.3.5 Cluster vs Sentinel (COMMON INTERVIEW)

| Aspect     | Sentinel      | Cluster          |
| ---------- | ------------- | ---------------- |
| Sharding   | ❌             | ✅                |
| HA         | ✅             | ✅                |
| Writes     | Single master | Multiple masters |
| Complexity | Lower         | Higher           |

👉 Interview line:

> Use Sentinel for HA, Cluster for HA + scale.

---

## 9.4 Consistency & Data Loss (IMPORTANT)

Redis guarantees:

* **Eventual consistency**

Possible data loss:

* During master failure
* Async replication lag

Mitigation:

* Minimize replication delay
* Use AOF everysec
* Accept trade-off

---

## 9.5 Common Interview Scenarios (VERY IMPORTANT)

❓ What happens if master crashes before replica syncs?
✔ Data loss possible.

❓ Can Redis guarantee zero data loss?
✔ No (not by default).

❓ How Redis avoids split-brain?
✔ Quorum voting (Sentinel / Cluster).

❓ Can Cluster use Sentinel?
✔ No — Cluster has built-in failover.

---

## 🧠 Mental Model (lock this in)

```
Replication → copies
Sentinel    → failover
Cluster     → scale + failover
```

---

## ✅ STEP 9 COMPLETE — You should now deeply understand

✔ Replication internals (full vs partial sync)
✔ Replication backlog
✔ Sentinel quorum & failover
✔ Cluster hash slots
✔ Failover mechanics
✔ Trade-offs & data loss
