# 🧱 Module 11 — Facade Pattern

---

## 1️⃣ Definition & Intent

> The **Facade Pattern** provides a **unified, simplified interface** to a set of complex subsystems.
> It hides the complexity of multiple classes behind one easy-to-use API.

👉 **Think:** *“I don’t need to know how all the subsystems work — just give me one simple entry point.”*

---

## 2️⃣ Problem it solves

In real-world systems, multiple components (classes, services, APIs) often interact in a complex way.

If every client has to directly handle all those subsystems, you get:

* Tight coupling between client and subsystems.
* High learning curve for using the system.
* Difficult maintenance when subsystems change.

**Facade** wraps that complexity into one high-level class that exposes a simple API for the most common use cases.

---

## 3️⃣ Real-world analogy

🎬 Imagine you go to a **movie theater**:

* You could manually:
  buy a ticket,
  book a seat,
  turn on the projector,
  dim the lights,
  start the movie.

But you don’t do all that!
You simply call the **“Movie Theater System”** and say → *“Play the movie.”*
The **Facade** (theater staff/system) coordinates all those complex actions for you.

---

## 4️⃣ Example — without Facade (ugly client code)

```java
public class CPU {
    public void freeze() { System.out.println("CPU freeze"); }
    public void jump(long position) { System.out.println("CPU jump to " + position); }
    public void execute() { System.out.println("CPU execute"); }
}

public class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Memory load data at position " + position);
    }
}

public class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("Reading " + size + " bytes from sector " + lba);
        return new byte[size];
    }
}

// Client code (too complex)
public class Client {
    public static void main(String[] args) {
        CPU cpu = new CPU();
        Memory memory = new Memory();
        HardDrive hardDrive = new HardDrive();

        cpu.freeze();
        byte[] data = hardDrive.read(123, 1024);
        memory.load(456, data);
        cpu.jump(789);
        cpu.execute();
    }
}
```

The client knows too much about the system internals (CPU, Memory, HardDrive).

---

## 5️⃣ ✅ Applying Facade Pattern

We create a **Facade class** that wraps this complexity behind simple methods.

```java
// Facade
public class ComputerFacade {
    private final CPU cpu;
    private final Memory memory;
    private final HardDrive hardDrive;

    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }

    public void startComputer() {
        System.out.println("Starting computer...");
        cpu.freeze();
        byte[] data = hardDrive.read(123, 1024);
        memory.load(456, data);
        cpu.jump(789);
        cpu.execute();
        System.out.println("Computer started successfully!");
    }
}

// Client
public class Demo {
    public static void main(String[] args) {
        ComputerFacade computer = new ComputerFacade();
        computer.startComputer();  // client calls only one method!
    }
}
```

### Output:

```
Starting computer...
CPU freeze
Reading 1024 bytes from sector 123
Memory load data at position 456
CPU jump to 789
CPU execute
Computer started successfully!
```

---

## 6️⃣ Real-world Java examples

* **Spring Boot’s `RestTemplate` / `WebClient`** — wraps complex HTTP handling behind simple API calls.
* **`java.sql.Connection`** — hides the JDBC driver, networking, and transaction management details.
* **`javax.faces.context.FacesContext`** — simplifies access to many JSF subsystems.

---

## 7️⃣ When to use Facade

✅ Use when:

* You have a **complex subsystem** that needs a simple entry point.
* You want to **decouple client** from subsystem details.
* You want to **organize layers** in a large system (e.g., controller → service → DAO).

❌ Avoid when:

* You only have 1–2 classes (no need for a facade).
* You need the client to access the full flexibility of the subsystem (Facade simplifies, but sometimes oversimplifies).

---

## 8️⃣ Benefits

✅ **Simplifies** client code — no need to manage subsystem complexity.
✅ **Reduces coupling** — changes inside subsystem don’t affect client.
✅ **Encapsulates** common use cases — gives clean, unified API.
✅ Works great as the **entry point of a layer** (common in enterprise apps).

---

## 9️⃣ Drawbacks

⚠️ May hide useful low-level operations.
⚠️ Can become a **god object** if it grows too large — keep Facades focused on specific workflows.
⚠️ Overuse can reduce flexibility — use only for common, high-level operations.

---

## 🔟 Exercises

1. Implement a **ReportExportFacade** that hides the complexity of creating a report → generating data → exporting → sending email.
2. Create a **HomeAutomationFacade** that controls multiple devices (Light, Fan, AC, DoorLock) with simple methods like `turnOnEverything()`.
3. Create a **DatabaseFacade** that internally manages `Connection`, `Statement`, and `ResultSet`, exposing one simple `executeQuery(String sql)` method.

---

## 🧠 Interview questions

1. What is the Facade pattern and what problem does it solve?
2. How is Facade different from Adapter?
3. Can you have multiple Facades for one subsystem?
4. How does Facade improve coupling and layering?
5. Give real examples of Facade in Java or Spring.

---

✅ **In short:**

> **Facade** = simplify complex systems by exposing a single unified interface to clients.
> **Decorator** = add features dynamically to a single object.
> **Adapter** = make incompatible interfaces work together.

---


## 11) Pros and Cons

1. Pros:
You can isolate your code from the complexity of a subsystem.

2. Cons:
A facade can become a god object coupled to all classes of an app.