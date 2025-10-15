# 🧱 Module 12 — Proxy Pattern

---

## 1️⃣ Definition & Intent

> The **Proxy Pattern** provides a **surrogate** or **placeholder** for another object to control access to it.

In simple terms:
Instead of directly calling a real object, you call a **proxy** that either:

* adds extra logic before/after delegating to the real object, or
* delays the creation of the real object until it’s actually needed.

---

## 2️⃣ Why we need it

Sometimes you don’t want direct access to a real object because:

* The real object is **expensive to create** (e.g., database connection, image file).
* You want to **add extra functionality** like logging, access control, caching, or lazy initialization.
* The real object might be on a **remote server** (like a remote API call).

In those cases, a **proxy** acts as a middleman between the client and the real object.

---

## 3️⃣ Types of Proxies

| Type                        | Purpose                                                                |
| --------------------------- | ---------------------------------------------------------------------- |
| **Virtual Proxy**           | Delays expensive object creation until it’s needed (lazy loading).     |
| **Protection Proxy**        | Controls access based on permissions (e.g., admin vs. user).           |
| **Remote Proxy**            | Represents an object located remotely (like RMI or web service).       |
| **Logging / Caching Proxy** | Adds extra behaviors like logging, caching, retry, etc.                |
| **Smart Reference Proxy**   | Performs housekeeping before/after delegating (ref counting, locking). |

---

## 4️⃣ Example — Virtual Proxy (Lazy loading an image)

### 🧾 Problem

Loading a large image from disk is expensive, and you might not need it until the user opens the viewer.

### ✅ Solution — Proxy loads it lazily

```java
// Subject interface
public interface Image {
    void display();
}

// Real subject (expensive to load)
public class RealImage implements Image {
    private final String fileName;

    public RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk();
    }

    private void loadFromDisk() {
        System.out.println("Loading image from disk: " + fileName);
    }

    @Override
    public void display() {
        System.out.println("Displaying image: " + fileName);
    }
}

// Proxy (lazy initialization)
public class ProxyImage implements Image {
    private RealImage realImage;
    private final String fileName;

    public ProxyImage(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(fileName); // load only when needed
        }
        realImage.display();
    }
}

// Client
public class Demo {
    public static void main(String[] args) {
        Image img = new ProxyImage("design.png");

        System.out.println("Image created, but not loaded yet!");
        img.display(); // loads + displays
        img.display(); // directly displays (already loaded)
    }
}
```

### ✅ Output:

```
Image created, but not loaded yet!
Loading image from disk: design.png
Displaying image: design.png
Displaying image: design.png
```

---

## 5️⃣ Real-world examples of Proxy pattern

| Context                 | Example                                                                 |
| ----------------------- | ----------------------------------------------------------------------- |
| 🖼 **Virtual Proxy**    | Lazy loading images or database connections.                            |
| 🔒 **Protection Proxy** | Controlling access to admin functions.                                  |
| 🌍 **Remote Proxy**     | RMI, Web Services, microservice stubs.                                  |
| 🧾 **Logging Proxy**    | Wrapping service calls to add logging / auditing.                       |
| ⚙️ **Spring AOP**       | Dynamic proxies add cross-cutting concerns like transactions, security. |

---

## 6️⃣ Real-world analogy

Imagine a **personal assistant**:

* You ask your assistant (proxy) to schedule a meeting or fetch a file.
* The assistant decides when and how to contact the real person or resource.
* You don’t talk to the actual person directly — the assistant acts as a **proxy**.

---

## 7️⃣ Difference between Proxy vs Decorator vs Facade

| Aspect           | **Proxy**                          | **Decorator**                                 | **Facade**                      |
| ---------------- | ---------------------------------- | --------------------------------------------- | ------------------------------- |
| Purpose          | Controls access to real object     | Adds new behavior dynamically                 | Simplifies a complex subsystem  |
| Relationship     | Proxy *represents* the real object | Decorator *wraps* and extends the real object | Facade *aggregates* subsystems  |
| Client awareness | Client sees same interface         | Client sees same interface                    | Client uses a simpler interface |
| Common usage     | Security, caching, lazy loading    | Runtime feature extension                     | Simplified APIs                 |

---

## 8️⃣ Practical Example — Protection Proxy

```java
// Subject
public interface ReportGenerator {
    void generateReport();
}

// Real subject
public class RealReportGenerator implements ReportGenerator {
    @Override
    public void generateReport() {
        System.out.println("Generating sensitive report...");
    }
}

// Proxy with access control
public class SecureReportProxy implements ReportGenerator {
    private final String userRole;
    private RealReportGenerator realReport;

    public SecureReportProxy(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public void generateReport() {
        if ("ADMIN".equalsIgnoreCase(userRole)) {
            if (realReport == null) realReport = new RealReportGenerator();
            realReport.generateReport();
        } else {
            System.out.println("Access denied. Only ADMIN can generate reports.");
        }
    }
}

// Client
public class ProxyDemo {
    public static void main(String[] args) {
        ReportGenerator admin = new SecureReportProxy("ADMIN");
        admin.generateReport(); // Allowed

        ReportGenerator user = new SecureReportProxy("USER");
        user.generateReport(); // Denied
    }
}
```

✅ Output:

```
Generating sensitive report...
Access denied. Only ADMIN can generate reports.
```

---

## 9️⃣ Benefits

✅ Controls access to sensitive or heavy resources.
✅ Adds caching, logging, or lazy loading without modifying original class.
✅ Improves performance (e.g., virtual proxy).
✅ Supports security and remote access seamlessly.

---

## 🔟 Drawbacks

⚠️ Introduces another layer of indirection — slight overhead.
⚠️ Can make debugging harder if overused.
⚠️ Requires maintenance if real object interface changes.

---

## 💪 Exercises

1. Implement a **DatabaseConnectionProxy** that opens the connection only when needed and closes it automatically after use.
2. Create a **LoggingProxy** around your `ReportGenerator` that logs method calls and execution time.
3. Build a **CachingProxy** for an external API client that stores results in memory for repeated calls.

---

## 💬 Interview Questions

1. What is the Proxy pattern and its main purpose?
2. What are different types of proxies and use cases for each?
3. How is Proxy different from Decorator or Facade?
4. How does Spring AOP use Proxy under the hood?
5. Can Proxy pattern help with lazy initialization? How?

---

✅ **In short:**

> **Proxy Pattern** = "Stand-in" object that controls access to another object — adding lazy loading, caching, logging, or security checks without modifying the real class.

---

Would you like me to show a **realistic Proxy pattern** using your existing `Database` example (e.g., lazy connection or access control for SQL queries)?
