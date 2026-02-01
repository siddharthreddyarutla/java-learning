# Module 5 — Dependency Inversion Principle (DIP)

## 📖 Definition

> **High-level modules should not depend on low-level modules. Both should depend on abstractions.**
> **Abstractions should not depend on details. Details should depend on abstractions.**

👉 In simple words: **depend on interfaces, not concrete classes.**

---

## ❌ BAD Example (violates DIP)

```java
class MySQLDatabase {
    public void connect() {
        System.out.println("Connected to MySQL Database");
    }
}

class Application {
    private MySQLDatabase database = new MySQLDatabase(); // tightly coupled ❌

    public void start() {
        database.connect();
    }
}
```

### Problems:

* `Application` (high-level module) directly depends on `MySQLDatabase` (low-level).
* If tomorrow you switch to `PostgreSQL`, you must **modify `Application`**.
* Hard to test → you can’t easily mock the database.

---

## ✅ GOOD Example (DIP applied)

We introduce an **abstraction** (`Database`) and depend on it:

```java
// Abstraction
interface Database {
    void connect();
}

// Low-Level implementations
class MySQLDatabase implements Database {
    @Override
    public void connect() {
        System.out.println("Connected to MySQL Database");
    }
}

class PostgreSQLDatabase implements Database {
    @Override
    public void connect() {
        System.out.println("Connected to PostgreSQL Database");
    }
}

// High-Level module depends on abstraction, not details
class Application {
    private final Database database;

    // Constructor injection
    public Application(Database database) {
        this.database = database;
    }

    public void start() {
        database.connect();
    }
}

// Client
public class Main {
    public static void main(String[] args) {
        Database db = new MySQLDatabase();  // could also be new PostgreSQLDatabase()
        Application app = new Application(db);
        app.start(); // Works with any Database
    }
}
```

### Benefits:

* `Application` doesn’t care if it’s MySQL or PostgreSQL.
* Easy to **extend** → just add a new `Database` implementation.
* Easy to **test** → pass a mock `Database` in unit tests.
* High-level & low-level modules both depend on the **abstraction** (`Database`).

---

## 💡 Real-world Analogy

Think of a **wall socket and appliances**:

* Wall socket gives **electricity via an interface (standard plug)**.
* Appliances (fan, TV, charger) just need a compatible plug.
* The socket doesn’t care which device is plugged in.
* If the socket required “only a fan” → you couldn’t use it for TV → tight coupling.

👉 DIP ensures flexibility just like universal sockets.

---

## 🧪 Unit-testing note

* With DIP, you can inject a **mock database** in tests:

```java
class MockDatabase implements Database {
    @Override
    public void connect() {
        System.out.println("Mock DB connected (test only)");
    }
}
```

```java
Application app = new Application(new MockDatabase());
app.start(); // ✅ Test without real DB
```

👉 Makes testing fast and independent of external systems.

---

## 🔧 Refactor Exercise (your turn)

Bad design:

```java
class EmailService {
    public void sendEmail(String to, String message) {
        System.out.println("Sending email to " + to);
    }
}

class Notification {
    private EmailService emailService = new EmailService();

    public void notifyUser(String to, String message) {
        emailService.sendEmail(to, message);
    }
}
```

👉 Refactor using DIP so that `Notification` depends on an **abstraction**, and you could easily switch to `SMSService` or `PushNotificationService`.

---

## ⚠️ Common Violations of DIP

* Directly instantiating dependencies with `new` inside classes.
* High-level modules (`Application`, `Notification`) tied to low-level details (`MySQL`, `EmailService`).
* No interface/abstraction layer.

---

## ✅ Rules of Thumb

* Always **program to an interface, not an implementation**.
* Use **constructor injection** or DI frameworks (Spring, Guice) to supply dependencies.
* Keep business logic unaware of infrastructure details.

---

## 🎯 Interview Questions (DIP)

1. Explain the Dependency Inversion Principle in simple words with an example.
2. How does DIP differ from Dependency Injection (DI)?
3. Why does DIP improve testability?
4. In your project, where have you applied DIP?
5. Can DIP ever be overused? (hint: unnecessary abstractions/interfaces).
