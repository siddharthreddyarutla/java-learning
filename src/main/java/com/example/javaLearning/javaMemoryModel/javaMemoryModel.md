# Java memory model


Alright, let’s do this like we’re in a **Java architecture roundtable** — concise, structured, but deep enough so you can explain it back to someone else.

---

# 🔹 Java Memory Model (JMM)

### 1. **JVM Runtime Memory Areas**

When a Java program runs, the JVM divides memory like this:

* **Heap (shared)**

    * Stores **all objects** and **their instance variables**.
    * Divided into:

        * **Young Generation**

            * Eden space (new objects start here).
            * Survivor spaces (S0, S1 → surviving objects get promoted).
        * **Old Generation (Tenured)** → long-lived objects.
    * GC happens here.

* **Stack (per thread)**

    * Each thread has its own stack.
    * Stores **frames** for each method call.
    * Frame holds local variables, references, partial results.
    * Destroyed automatically when the method returns.

* **Method Area / Metaspace (shared)**

    * Stores class metadata (names, fields, methods, bytecode).
    * Stores **static variables** and **runtime constant pool**.
    * In Java 8+, **Metaspace** replaced **PermGen**.

* **PC Register**

    * Each thread has one, points to current executing instruction.

* **Native Method Stack**

    * For JNI/native calls.

---

### 2. **Concurrency Aspect of JMM**

JSR-133 defines **how threads interact with memory**:

* **happens-before rule** → defines visibility/order guarantees.
* **volatile** → guarantees visibility of updates.
* **synchronized/locks** → guarantees atomicity + visibility.
* Each thread may cache variables; JMM rules ensure consistency.

---

# 🔹 Garbage Collection (GC)

### 1. **What GC Does**

* Finds objects in the **heap** that are no longer reachable (unreferenced).
* Reclaims memory so new objects can be created.

---

### 2. **GC Process**

1. **Mark** – trace from GC Roots (local vars, static vars, threads, JNI refs) to mark reachable objects.
2. **Sweep** – collect the unmarked (unreachable) ones.
3. **Compact** – (optional) rearrange memory to reduce fragmentation.

---

### 3. **Types of GC Events**

* **Minor GC** → Cleans Young Gen (fast, frequent).
* **Major GC (Full GC)** → Cleans Old Gen + sometimes Young Gen (slower, bigger pause).

---

### 4. **Collectors**

* **Serial GC** → Single-threaded (small apps).
* **Parallel GC** → Multi-threaded, throughput oriented.
* **CMS (Concurrent Mark Sweep)** → Low-pause, deprecated.
* **G1 (Garbage First)** → Default since Java 9, balances latency/throughput.
* **ZGC / Shenandoah** → Modern, low-latency, for very large heaps.

---

# 🔹 Example Code Walkthrough

```java
class Demo {
    static int count = 0;        // Method Area / Metaspace
    public static void main(String[] args) {
        int x = 10;              // Stack (local var in main frame)
        Person p = new Person(); // p → stack reference, object → heap
        count++;                 // static var → Metaspace
    }
}
```

* When `main` ends:

    * `x` and `p` disappear from the stack.
    * If no other references to the `Person` object exist → eligible for GC.

---

✅ **In summary:**

* **Heap** → Objects, GC territory.
* **Stack** → Local variables, per-thread.
* **Metaspace** → Class info + static vars.
* **GC** → Reclaims unreachable heap memory (Young/Old gen) using collectors like G1.
* **JMM rules** → Make sure multi-threaded code sees consistent values.

---
