# 📘 Elasticsearch Deep-Dive Notes

These are detailed notes covering **Index, Shards, Replicas, Nodes, Clusters**, and shard allocation with real QA/Dev examples.

---

## 🔹 Index
- An **index** in Elasticsearch is like a **database** in traditional systems.
- Stores JSON **documents** (like rows in RDBMS).
- Each document is automatically assigned to a **shard**.
- Example: `user_leave_attendance` is an index.

---

## 🔹 Shards
- An **index is split into multiple shards** for scalability.
- A **shard** is a **Lucene index** that stores a portion of the data.
- **Number of primary shards is fixed at index creation**.
- Shards are distributed across nodes.

### Types of shards:
1. **Primary shard (p)**
    - Original data lives here.
    - Every document belongs to exactly **one primary shard**.

2. **Replica shard (r)**
    - A full copy of a primary shard.
    - Provides:
        - **High availability** (if a primary fails, replica becomes primary).
        - **Load balancing** (queries can hit replicas too).

---

## 🔹 Nodes
- A **node** = a **single Elasticsearch server instance** (Java process).
- Nodes store shards.
- Multiple nodes = a **cluster**.

### Types of nodes:
- **Master-eligible node** → manages cluster state (which nodes exist, shard allocation).
- **Data node** → stores and serves shards (most common).
- **Ingest node** → preprocesses docs before indexing.
- **Coordinating-only node** → routes queries but doesn’t store data.

---

## 🔹 Cluster
- A **cluster** is a **group of nodes** working together.
- Provides:
    - **Scalability** → more nodes = more capacity.
    - **High availability** → replicas ensure no single point of failure.
- Cluster is identified by a **cluster name**.
- Clients connect to any node (called a **coordinating node**) and Elasticsearch routes the query internally.

---

## 🔹 How Elasticsearch Decides Where to Store Docs
Formula for shard assignment:

```text
shard = hash(_routing) % number_of_primary_shards
```


- `_routing` defaults to the document `_id`.
- Ensures:
    - Same doc ID → always goes to the same shard.
    - Even distribution across shards.

---

## 🔹 Shard Allocation Rules
1. **No primary & replica on the same node**.
    - Ensures redundancy.

2. **Balance across nodes**.
    - ES spreads shards to avoid hot-spots.

3. **Awareness (rack/zone/datacenter)**.
    - Shards can be forced to different racks/zones.

4. **Disk watermarks**.
    - If a node is low on disk, ES won’t allocate new shards there.

---

## 🔹 Practical Examples

### 🟢 Dev Environment (No replicas)

```text
index shard prirep state docs store ip node
user_leave_attendance 1 p STARTED 4109241 884.2mb 10.2.6.241 new-es-node
user_leave_attendance 0 p STARTED 4112884 773.1mb 10.2.6.241 new-es-node
```


- **2 primary shards (0 and 1)**.
- Both shards are on the same node (`10.2.6.241`).
- No replicas (`prirep` = `p` only).
- Total documents = 4,112,884 + 4,109,241 = ~8.22M.
- Total storage ~1.65 GB.
- ❌ Risk: if this node goes down, the entire index is lost (no replicas).

---

### 🟢 QA Environment (With replicas)

```text
index shard prirep state docs store ip node
user_leave_attendance 1 p STARTED 12057064 2.2gb 10.1.3.90 new-es-node1
user_leave_attendance 1 r STARTED 12057064 2.1gb 10.1.41.198 new-es-node2
user_leave_attendance 0 p STARTED 12043098 2.2gb 10.1.3.90 new-es-node1
user_leave_attendance 0 r STARTED 12043098 2gb 10.1.68.107 new-es-node3
```


- **2 primaries** (Shard 0 and Shard 1).
- Each primary has **1 replica**:
    - Shard 1 primary → Node1, replica → Node2.
    - Shard 0 primary → Node1, replica → Node3.
- Docs per shard ~12M, total ~24.1M docs.
- Storage ~8.5 GB (primaries + replicas).
- ✅ Safer: if Node1 fails, replicas on Node2 and Node3 can be promoted to primaries.

---

## 🔹 Summary of Key Concepts
- **Index**: logical namespace for documents.
- **Shard**: physical data container (Lucene index).
- **Primary shard**: holds original data.
- **Replica shard**: copy of primary for redundancy & load balancing.
- **Node**: one ES server (process).
- **Cluster**: group of nodes working together.
- **Shard allocation**: decides where primaries & replicas live, ensuring balance & fault tolerance.

---

## 🔹 Analogy
- **Cluster** = entire library.
- **Node** = bookshelf in the library.
- **Shard** = a section of books on a shelf.
- **Replica** = duplicate copy of that section on another shelf (for safety).

---
