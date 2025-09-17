## 🔹 1. The Compilation Step

* You write `.java` source code.
* `javac` (Java compiler) compiles it into `.class` files containing **bytecode**.
* This bytecode is **not machine code** → it’s an intermediate language for the **JVM**.

👉 So at this stage, Java is a **compiled language** (compiled to bytecode).

---

## 🔹 2. The Execution Step

* When you run `java MyClass`, the JVM **interprets** the bytecode:

    * Reads bytecode instruction by instruction.
    * Executes it on your machine.
* This makes Java **an interpreted language** at runtime.

---

## 🔹 3. The JIT Compiler (Just-In-Time)

* To speed things up, the JVM has a **JIT compiler**.
* It detects “hot” methods (used often).
* Compiles those bytecodes into **native machine code** (x86, ARM, etc.).
* Stores them so future calls run at near-C speed.

👉 This means Java is **JIT-compiled** too.

---

## 🔹 4. So what is Java really?

* **Not purely compiled** (like C/C++ → machine code ahead of time).
* **Not purely interpreted** (like Python, Ruby → interpreted every time).
* ✅ Java uses a **hybrid model**:

    * Compiled to bytecode (portable).
    * Interpreted by JVM.
    * JIT-compiled to native code for performance.

---

## 🔹 Summary in one line

**Java is a compiled language (to bytecode) that is executed by an interpreter (the JVM) with help from a JIT compiler for performance.**
