# 📘 Low-Level Design (LLD) — Interview Revision Notes (with Java)

---

## 1️⃣ What is Low-Level Design (LLD)?

**Definition (Interview-ready):**

> Low-Level Design focuses on designing **classes, objects, their relationships, and interactions**, and acts as a bridge between **High-Level Design (architecture)** and **actual code**.

### Placement

```
HLD (System / Architecture)
        ↓
LLD (Classes, Interfaces, Relationships)
        ↓
Code (Java Implementation)
```

### Why LLD is important

* Code becomes **clean**
* Code is **flexible & maintainable**
* Code is **easy to test**
* Writing code is easy → **designing it well is hard**

💡 **Interview line you can use:**

> “Good LLD makes coding straightforward and reduces future changes.”

---

## 2️⃣ Categories of LLD (Design Patterns)

LLD patterns are divided into **3 categories**:

```
Creational  → Object creation
Structural  → Object arrangement (skeleton)
Behavioral  → Object interaction (behavior)
```

---

## 3️⃣ Creational Patterns (Object Creation)

**Purpose:**
Control **how and when objects are created**

### Common Patterns

* Singleton
* Builder
* Factory
* Abstract Factory
* Prototype
* Object Pool

### Example: Singleton

**When to use**

* Only **one instance** should exist (config, cache, logger)

```java
public class Singleton {
    private static Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

📌 **Interview tip:**

> “Creational patterns help avoid uncontrolled object creation.”

---

### Example: Builder

**When to use**

* Object creation is complex
* Many optional fields

```java
class User {
    private String name;
    private int age;

    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
    }

    static class Builder {
        private String name;
        private int age;

        Builder setName(String name) {
            this.name = name;
            return this;
        }

        Builder setAge(int age) {
            this.age = age;
            return this;
        }

        User build() {
            return new User(this);
        }
    }
}
```

---

## 4️⃣ Structural Patterns (Object Arrangement)

**Purpose:**
Define **how objects are arranged together** to solve a larger problem.

💡 Think of this as the **skeleton of the system**.

### Common Patterns

* Adapter
* Decorator
* Proxy
* Composite
* Facade
* Bridge
* Flyweight

### Example: Facade

**Problem:**
Complex subsystems confuse clients

**Solution:**
Provide a simple interface

```java
class Engine {
    void start() {}
}

class Lights {
    void on() {}
}

class CarFacade {
    private Engine engine = new Engine();
    private Lights lights = new Lights();

    void startCar() {
        engine.start();
        lights.on();
    }
}
```

📌 **Interview line:**

> “Structural patterns focus on object composition rather than inheritance.”

---

## 5️⃣ Behavioral Patterns (Object Interaction)

**Purpose:**
Control **how objects communicate and coordinate**

* Who talks to whom?
* Who controls the flow?
* Direct call vs mediator/orchestrator?

### Examples

* Strategy
* Observer
* Command
* Mediator
* Chain of Responsibility

### Example: Strategy

```java
interface PaymentStrategy {
    void pay(int amount);
}

class CardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid by card: " + amount);
    }
}

class PaymentService {
    private PaymentStrategy strategy;

    PaymentService(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    void pay(int amount) {
        strategy.pay(amount);
    }
}
```

📌 **Interview line:**

> “Behavioral patterns define how the system behaves, not how it’s structured.”

---

## 6️⃣ IS-A Relationship (Inheritance)

**Definition:**

* Represents **parent–child** relationship
* Uses `extends`

### Example

```java
class Vehicle {}

class Car extends Vehicle {}
class Bike extends Vehicle {}
```

📌 Rule:

> If you can say **“X is a Y”**, use inheritance.

---

## 7️⃣ HAS-A Relationship (Association)

**Definition:**

* One object **contains or uses** another

```java
class Car {
    Engine engine; // Car has an Engine
}
```

---

## 8️⃣ Aggregation vs Composition (VERY IMPORTANT)

### 🔹 Aggregation (Weak HAS-A)

* Objects can exist independently
* Parent does NOT manage lifecycle

**Example: Library & Books**

```java
class Book {}

class Library {
    private List<Book> books; // Library knows books
}
```

📌 Book can exist without Library

---

### 🔹 Composition (Strong HAS-A)

* Child depends on parent
* Parent manages lifecycle

**Example: House & Rooms**

```java
class Room {}

class House {
    private List<Room> rooms = new ArrayList<>();

    House() {
        rooms.add(new Room());
    }
}
```

📌 If House is destroyed → Rooms are destroyed

---

## 9️⃣ UML in Interviews (Practical Advice)

* UML is **for clarity**, not art
* In **40–45 min LLD round**:

    * Spend **10–15 min max** on UML
    * Rest time → coding
* Interviewers mostly care about:

    * Correct classes
    * Correct relationships
    * Clean code

💡 Shortcut:

* Use arrows
* Clearly label **IS-A / HAS-A**
* Don’t waste time on perfect symbols

---

## 🔟 How Interviewers Evaluate LLD

They check:

* Do you identify correct classes?
* Are relationships correct?
* Is code extensible?
* Are responsibilities clear?

❌ Not checking:

* Perfect UML symbols
* Remembering pattern names

---

## 🧠 Final Interview Takeaways

✔ LLD bridges architecture and code
✔ Design patterns are **tools, not rules**
✔ IS-A = inheritance
✔ HAS-A = association
✔ Aggregation vs Composition depends on **lifecycle**
✔ UML should help you code faster

---

## ⭐ One-line Summary (Memorize)

> “Low-Level Design focuses on clean class design, correct relationships, and object interactions to make code maintainable and testable.”

---

If you want next, I can:

* Convert this into **Parking Lot / BookMyShow LLD**
* Give **LLD interview questions + answers**
* Show **mistakes interviewers reject**
* Create a **1-page LLD cheat sheet**

Just tell me 🚀
