# 🧠 Redis Cluster — DEEP, STEP-BY-STEP EXPLANATION

---

## 1️⃣ Why Redis Cluster even exists (problem first)

### Problem with single Redis

* Limited by **one machine’s RAM**
* Single write master
* Vertical scaling only

### What companies need

* More memory than one machine
* More write throughput
* High availability

👉 **Redis Cluster = Horizontal scaling + HA**

---

## 2️⃣ What Redis Cluster actually is (1-line truth)

> Redis Cluster is a distributed Redis setup where **data is automatically partitioned (sharded)** across multiple Redis nodes, with **built-in failover**.

Key words:

* **Distributed**
* **Sharded**
* **Automatic**
* **Failover**

---

## 3️⃣ Core idea: HASH SLOTS (THIS IS THE KEY 🔑)

### Redis Cluster does NOT shard by key directly

It shards by **hash slots**

### Total hash slots

```
16384 slots (fixed)
```

Every key belongs to **exactly one slot**.

---

## 4️⃣ How a key maps to a node (VERY IMPORTANT)

### Step-by-step flow

1. Client sends a key:

   ```text
   user:123
   ```

2. Redis calculates:

   ```text
   CRC16(user:123) % 16384 = slot 5791
   ```

3. Slot 5791 is owned by **some master node**

4. That node stores the key

---

### Visual mental model

![Image](https://severalnines.com/sites/default/files/blog/node_6303/image1.png)

![Image](https://i.sstatic.net/kTq5N.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1200/1%2AQ1uy_RrVYMhGjEy7Y69tGg.jpeg)

```
Key → Hash → Slot → Master Node
```

👉 **Keys don’t move often — slots move**

---

## 5️⃣ What nodes exist in Redis Cluster?

A cluster has **two types of nodes**:

### 1️⃣ Master nodes

* Own hash slots
* Accept writes

### 2️⃣ Replica nodes

* Copy data from master
* Used for failover

Minimum recommended cluster:

```
3 masters + 3 replicas = 6 nodes
```

---

## 6️⃣ Example cluster (realistic)

Assume:

```
Node A → slots 0–5460
Node B → slots 5461–10922
Node C → slots 10923–16383
```

Each master has one replica:

```
A → A1
B → B1
C → C1
```

---

## 7️⃣ How READ & WRITE work in Redis Cluster

### WRITE flow

1. Client hashes key
2. Finds slot
3. Sends command to correct master
4. Master writes data
5. Replicas sync asynchronously

---

### What if client sends to WRONG node?

Redis replies:

```text
MOVED 5791 10.0.0.2:6379
```

Client:

* Updates its slot cache
* Retries automatically

👉 Clients **must be cluster-aware**

---

## 8️⃣ Why Redis Cluster does NOT support multi-key ops easily

### Problem

Keys:

```text
user:1
order:99
```

They likely map to **different slots**.

### Result

Commands like:

```bash
MGET user:1 order:99
```

❌ FAIL

---

### Solution: Hash Tags `{}`

```text
user:{123}
order:{123}
```

Only content inside `{}` is hashed.

👉 Both go to same slot → multi-key ops allowed

---

## 9️⃣ How FAILOVER works in Redis Cluster (CRITICAL)

![Image](https://yqintl.alicdn.com/a0b317aa8dfb629a97d39e74fbc7c9e75dbe69ac.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1200/1%2Aejl4ZXCUXd57rncaGRufpg.png)

### Scenario: Master B crashes

#### Step-by-step

1. Other masters detect B is unreachable
2. Replica B1 is elected
3. B1 promoted to master
4. Slots owned by B move to B1
5. Cluster continues

✔ Automatic
✔ No Sentinel needed

---

## 10️⃣ How Redis Cluster avoids split-brain

Redis Cluster uses **majority voting**.

Rule:

> If **majority of masters are reachable**, cluster is healthy.

If NOT:

* Cluster becomes **read-only / unavailable**
* Prevents data inconsistency

---

## 11️⃣ Cluster Bus (Internal communication)

Redis nodes talk using a **separate TCP port**:

```
Client port: 6379
Cluster bus: 16379
```

Used for:

* Heartbeats
* Failure detection
* Slot updates

---

## 12️⃣ Redis Cluster vs Sentinel (CLEAR DIFFERENCE)

| Feature         | Sentinel | Cluster |
| --------------- | -------- | ------- |
| Sharding        | ❌        | ✅       |
| Write scaling   | ❌        | ✅       |
| HA              | ✅        | ✅       |
| Slot management | ❌        | ✅       |
| Complexity      | Medium   | High    |

👉 **Interview gold**:

> Sentinel is for availability, Cluster is for availability + scalability.

---

## 13️⃣ Data consistency guarantees (important truth)

Redis Cluster:

* **Eventually consistent**
* Async replication
* Small data loss possible

Why acceptable?

* Performance > strict consistency
* Redis is often a cache / fast store

---

## 14️⃣ When SHOULD you use Redis Cluster?

### Use Cluster when:

* Dataset > single machine RAM
* High write throughput
* Horizontal scaling needed

### DON’T use Cluster when:

* Heavy multi-key transactions
* Strong consistency required
* Simple caching needs

---

## 15️⃣ ONE PERFECT INTERVIEW ANSWER (memorize)

> Redis Cluster horizontally partitions data using 16384 hash slots distributed across multiple master nodes, each with replicas for failover. Clients route requests based on slot ownership, and the cluster provides automatic failover and scalability with eventual consistency.

---

## 🧠 Final mental picture (lock this in)

```
Key
 ↓
Hash
 ↓
Slot (0–16383)
 ↓
Master Node
 ↓
Replica (failover)
```