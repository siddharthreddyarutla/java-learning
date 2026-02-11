# 📘 STEP 5 — Write Path, Refresh & Near Real-Time

---

## 1️⃣ What Happens When You Index a Document

When a document is indexed:

1. Request goes to **primary shard**
2. Document is written to:

    * In-memory buffer
    * Transaction log (translog)
3. Document is **not searchable yet**
4. Change is replicated to replica shards

📌 Important:

> Indexing ≠ searchable

---

## 2️⃣ Refresh (Key Concept)

* Refresh makes documents **searchable**
* Happens by:

    * Creating a new search segment
    * Opening it for search

Default:

* `refresh_interval ≈ 1s`

📌 Interview line:

> Refresh exposes data to search, it does not persist it.

---

## 3️⃣ Why Elasticsearch Is Near Real-Time

* Writes are fast because:

    * No immediate refresh
* Search sees data **after refresh**
* Small delay improves throughput

📌 Core idea:

> ES sacrifices immediate visibility for performance.

---

## 4️⃣ Refresh vs Flush (VERY IMPORTANT)

### Refresh

* Makes data searchable
* Happens frequently
* Lightweight
* Does NOT write to disk permanently

### Flush

* Writes in-memory segments to disk
* Clears translog
* Heavy operation
* Happens less frequently

📌 Interview trap:

> Refresh ≠ Flush

---

## 5️⃣ Translog (Crash Safety)

* Every write is recorded in translog
* Used for:

    * Crash recovery
* Even if data not flushed, it’s safe

📌 Interview line:

> Translog protects against data loss.

---

## 6️⃣ Controlling Refresh (Performance Tuning)

* `refresh_interval` can be:

    * Increased → better write performance
    * Decreased → faster search visibility

Example use cases:

* Heavy bulk indexing → disable refresh
* Search-heavy system → default or lower

---

## 7️⃣ Real Production Pattern

During bulk indexing:

* Set `refresh_interval = -1`
* Index data
* Manually refresh once
* Re-enable refresh

📌 Senior-level signal.

---

## 🧠 Step 5 Memory Hook

> Indexing writes data fast, refresh makes it searchable, flush makes it durable.

---

## 🎯 Interview Gold Summary

> Elasticsearch is near real-time because indexed documents become searchable only after refresh, which is asynchronous and periodic.