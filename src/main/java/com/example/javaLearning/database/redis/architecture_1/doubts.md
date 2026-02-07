
# 1️⃣ Does Redis create a TCP connection for every fetch? Isn’t that slow?

### Short answer

❌ **NO**, Redis does **NOT** create a TCP connection per request.

### What actually happens

* Client creates a **TCP connection ONCE**
* Connection stays **open (persistent)**
* All commands reuse the **same socket**

Example:

```text
Java App ────── TCP connection ────── Redis
          GET
          SET
          GET
          INCR
          ...
```

Creating TCP repeatedly would:

* Kill performance
* Add handshake + kernel overhead

👉 Redis clients (Jedis, Lettuce) use:

* **Connection pooling**
* **Persistent connections**

💡 Interview-ready line:

> Redis uses long-lived TCP connections; commands are multiplexed over existing sockets, avoiding connection setup overhead.

---

# 2️⃣ TCP socket, OS, non-blocking I/O, I/O multiplexing & event loop

(this is the BIG one — let’s untangle it)

---

## 2.1 What creates the TCP socket?

* Redis calls OS system calls like:

    * `socket()`
    * `bind()`
    * `listen()`
    * `accept()`

👉 **OS owns the socket**, not Redis.

Redis just gets a **file descriptor (FD)**:

```text
socket FD = 12
```

Think of FD as:

> “A handle to talk to the OS networking system”

---

## 2.2 What does *non-blocking I/O* mean?

### Blocking I/O (bad for Redis)

```text
read(socket)  ← waits if no data
```

Redis would get **stuck** waiting for one client 😬

---

### Non-blocking I/O (what Redis uses)

```text
read(socket)
→ if no data: returns immediately
```

Redis says:

> “If there’s data, give it. If not, I’ll do something else.”

---

## 2.3 Where does I/O multiplexing come in?

![Image](https://substackcdn.com/image/fetch/%24s_%21LbM4%21%2Cf_auto%2Cq_auto%3Agood%2Cfl_progressive%3Asteep/https%3A%2F%2Fsubstack-post-media.s3.amazonaws.com%2Fpublic%2Fimages%2F7ca7545a-2a0d-4fee-96cb-62a4a04470d3_1752x838.png)

![Image](https://www.rapid7.com/cdn/assets/blt9b5aa0cb49233f63/683dddadbc38b12ba5477c09/eventloop.png)

![Image](https://miro.medium.com/1%2Axm_WajiPlaOeJWcqgJb1xQ.png)

Problem:

* Redis has **10,000 clients**
* It cannot check each socket one-by-one

Solution:

### I/O Multiplexing (epoll / kqueue)

Redis asks the OS:

> “Here are 10,000 sockets — tell me ONLY which ones are ready”

The OS:

* Monitors sockets internally
* Wakes Redis **only when something happens**

No polling. No busy waiting.

---

## 2.4 Are requests stored in OS registers?

Not registers — **kernel buffers**.

Flow:

1. Client sends data
2. Data goes to **kernel receive buffer**
3. Socket becomes “readable”
4. OS notifies Redis via epoll
5. Redis reads data

So:

* OS buffers data
* Redis **pulls it when notified**

---

## 2.5 Event Loop — where is it & what does it hold?

### Where?

* Inside **Redis process memory**
* Runs as a continuous loop

### What it holds:

* List of active sockets
* Ready-to-read sockets
* Ready-to-write sockets
* Timers (TTL, eviction, persistence)

### Simplified loop:

```text
while (true) {
  readySockets = epoll_wait()
  for each socket:
    read command
    execute command
    write response
}
```

👉 Important:

* **One command at a time**
* That’s why Redis operations are atomic

---

## 2.6 So yes — your understanding was almost right ✅

Corrected version of what you said:

> Requests arrive at OS buffers → OS notifies Redis → Redis processes them one by one inside the event loop.

---

# 3️⃣ Redis stores data in heap memory — what about crash, persistence & size?

Excellent question. This is **Step 3 material**, but let’s preview it clearly.

---

## 3.1 What happens if Redis crashes?

By default:
❌ **DATA IS LOST**

Redis is:

> “In-memory first, persistence optional”

---

## 3.2 How does Redis persist data?

Two mechanisms (we’ll deep dive later):

### 🔹 RDB (Snapshot)

* Periodic memory dump to disk
* Fast
* Some data loss possible

### 🔹 AOF (Append Only File)

* Logs every write command
* Slower
* Safer

👉 Production systems often use **both**.

---

## 3.3 How much data can Redis store?

Limits:

1. **Available RAM**
2. Redis key/value limits:

    * String value: **512 MB**
3. `maxmemory` config

Redis can:

* Evict old keys
* Reject writes
* Use LRU / LFU

---

## 3.4 Why Redis doesn’t support custom queries?

Redis is **NOT relational**.

* No tables
* No joins
* No WHERE clauses

Why?

* Queries = CPU heavy
* Indexing = memory heavy
* Redis is optimized for **direct access**

You access data like:

```text
key → value
```

Which is:

* Hash table lookup
* **O(1)** average time

👉 Interview line:

> Redis trades query flexibility for speed by using direct key-based access instead of relational querying.

---

## 3.5 Can Redis be used as a primary DB?

Answer like a pro:

> Redis can be a primary database for specific workloads like caching, sessions, counters, and leaderboards, but it’s usually combined with a durable database like MySQL for long-term storage.

---

## ✅ Summary (lock this in)

✔ TCP connections are persistent
✔ OS owns sockets, Redis uses FDs
✔ Non-blocking I/O prevents waiting
✔ I/O multiplexing lets one thread handle many clients
✔ Event loop executes commands sequentially
✔ Data is in heap memory
✔ Persistence is optional but configurable
✔ Fast access due to O(1) key lookups

--