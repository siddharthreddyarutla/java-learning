# Basics


## 🧠 Key Concepts

### 🔹 CPU (Central Processing Unit)

* Hardware component, also called the **processor**.
* Executes instructions of programs.
* A CPU can have **multiple cores**.
* Each CPU/core executes instructions sequentially, but with multithreading it can *appear* parallel.

---

### 🔹 Core

* A **core** is an independent execution unit inside a CPU.
* Each core can execute its own instructions.
* More cores → more **true parallelism**.
* Example:

    * Single-core CPU → only one task at a time.
    * Quad-core CPU → can run 4 tasks *truly in parallel*.

---

### 🔹 Program

* A **program** is a passive collection of instructions stored on disk (e.g., `.java`, `.exe`).
* It’s not running yet, just a file with code.

---

### 🔹 Process

* A **process** is a running instance of a program.
* When you open a program → OS creates a process.
* Each process has its own **memory space** (isolation).
* Example: Opening Chrome twice → two processes.

---

### 🔹 Thread

* A **thread** is the smallest execution unit inside a process.
* A process always has **at least one thread** (the main thread).
* Multiple threads inside a process share:

    * **Heap (shared memory)**
    * **File handles**
* But each thread has its own:

    * **Stack**
    * **Program counter**

---

## 🏗️ Relationship (Hierarchy)

```
Program (code on disk)
   ↓ (when executed)
Process (running program, with memory & resources)
   ↓
Threads (multiple execution paths inside the process)
   ↓
CPU Cores (hardware executing threads)
```

---

## ⚡ Example (Real-world analogy)

* **Program**: A recipe book (instructions).
* **Process**: A cook following a recipe.
* **Thread**: Two hands of the cook working on tasks (cutting + stirring).
* **Core**: Number of cooks in the kitchen.
* **CPU**: The entire kitchen itself.

---

## 📝 Quick Summary Notes

* **CPU** = physical processor chip.
* **Core** = independent unit inside CPU.
* **Program** = instructions/code (not running yet).
* **Process** = running program with its own memory.
* **Thread** = lightweight execution unit within a process (shares memory).

---
---

## 🔹 Multitasking

* **Definition**: Ability of the OS to run **multiple processes** at the same time.
* Each process is independent and has its own memory space.
* Example: You can run Chrome, Spotify, and Word at the same time → OS switches CPU time between them.
* **Types of multitasking**:

  1. **Process-based multitasking** (multiprocessing): running multiple processes.
  2. **Thread-based multitasking** (multithreading): running multiple threads inside the same process.

---

## 🔹 Multithreading

* **Definition**: Ability of a process to be divided into multiple **threads** that run concurrently.
* Threads **share the same memory** of the process → faster communication, less overhead.
* Example: A web browser (single process) →

  * One thread renders page
  * Another downloads images
  * Another plays a video

---

## ⚡ Key Differences

| Aspect                | Multitasking (Processes)                      | Multithreading (Threads)                     |
| --------------------- | --------------------------------------------- | -------------------------------------------- |
| **Unit of execution** | Process                                       | Thread (inside a process)                    |
| **Memory**            | Each process has its own memory (heavyweight) | Threads share memory (lightweight)           |
| **Isolation**         | Processes are independent                     | Threads are dependent (share data)           |
| **Context switch**    | Expensive (save/restore entire process state) | Cheaper (just registers + stack)             |
| **Communication**     | Inter-process communication (IPC) is complex  | Easier (shared memory)                       |
| **Example**           | Running Chrome + Spotify + Word               | Inside Chrome → tabs, rendering, downloading |

---

## 📝 Quick Summary

* **Multitasking** = Running multiple processes.
* **Multithreading** = Multiple threads inside one process.
* OS provides **multitasking**, while programs use **multithreading** for efficiency.

---

