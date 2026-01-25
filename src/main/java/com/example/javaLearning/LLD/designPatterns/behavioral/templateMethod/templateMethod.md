
## 🧩 **Template Method Pattern — Detailed & Practical Overview**

### 🔹 **Definition**

The **Template Method Pattern** defines the **skeleton (outline)** of an algorithm in a base (abstract) class and lets **subclasses override specific steps** without changing the algorithm’s overall structure.

👉 It enforces a *fixed process flow* while allowing *customization* of certain parts.

---

### 🔹 **Why Use It**

* To **reuse** common logic across multiple subclasses.
* To **enforce consistency** — all subclasses follow the same workflow.
* To allow **partial customization** — subclasses modify specific steps, not the overall structure.

---

### 🔹 **Real-world analogy**

Think of a **coffee machine**:

* Steps: boil water → brew drink → pour into cup → add condiments
* The **sequence** never changes, but the **specifics** (e.g., tea leaves vs coffee powder) do.

---

### 🔹 **Structure**

* **Abstract Class (Template Class):** defines the *template method* (final) + abstract methods for customizable steps.
* **Concrete Subclasses:** implement the abstract steps.
* **Template Method:** calls the steps in a specific order (cannot be overridden).

---

### 🔹 **Example (Java)**

```java
abstract class DataProcessor {

    // Template method - defines fixed sequence
    public final void processData() {
        readData();
        processDataInternal();
        saveData();
    }

    protected abstract void readData();
    protected abstract void processDataInternal();

    // Default step (hook method) — can be overridden if needed
    protected void saveData() {
        System.out.println("Saving processed data to database...");
    }
}

// Concrete subclass 1
class CSVDataProcessor extends DataProcessor {
    protected void readData() {
        System.out.println("Reading data from CSV file...");
    }
    protected void processDataInternal() {
        System.out.println("Processing CSV data...");
    }
}

// Concrete subclass 2
class JSONDataProcessor extends DataProcessor {
    protected void readData() {
        System.out.println("Reading data from JSON file...");
    }
    protected void processDataInternal() {
        System.out.println("Processing JSON data...");
    }
}

// Client
public class TemplateDemo {
    public static void main(String[] args) {
        DataProcessor csv = new CSVDataProcessor();
        csv.processData();

        DataProcessor json = new JSONDataProcessor();
        json.processData();
    }
}
```

---

### 🔹 **Output**

```
Reading data from CSV file...
Processing CSV data...
Saving processed data to database...

Reading data from JSON file...
Processing JSON data...
Saving processed data to database...
```

---

### 🔹 **Key Concepts**

* `final` template method ensures **workflow order** can’t be changed.
* Abstract methods ensure **flexibility** for subclass customization.
* Common steps can be implemented as **default or hook methods**.
* Promotes **code reuse** and **consistency**.

---

### 🔹 **When to Use**

✅ Common sequence of steps but subclass-specific details (e.g., parsing files, payment processing, report generation, or task pipelines).
✅ Need to **enforce** a standard procedure for all subclasses.

---

### 🔹 **Advantages**

✅ Promotes **DRY** (Don’t Repeat Yourself) principle.
✅ Improves **code consistency**.
✅ Allows controlled **customization**.

---

### 🔹 **Disadvantages**

❌ Inflexible if algorithm steps differ drastically between subclasses.
❌ Tight coupling through inheritance (Strategy pattern is better when you need composition).

---

### 🔹 **Template vs Strategy**

| Feature       | Template Method             | Strategy                             |
| ------------- | --------------------------- | ------------------------------------ |
| Approach      | **Inheritance**             | **Composition**                      |
| Extensibility | Subclasses override methods | Different strategy objects           |
| Control       | Base class controls flow    | Context chooses behavior dynamically |

---

### 🔹 **Common Examples in Java**

* `java.util.AbstractList` → defines methods like `add()`, `remove()` but leaves `get()` to subclasses.
* `HttpServlet` → `service()` defines the flow, calls subclass methods like `doGet()` or `doPost()`.
* `Spring Framework` → `JdbcTemplate` (handles connection & resource management; subclasses define SQL execution logic).

---

### 🔹 **Interview Q&A**

1. **What is the Template Method pattern?**
   → A behavioral pattern that defines a fixed algorithm skeleton, letting subclasses override specific steps.
2. **Why is the template method usually `final`?**
   → To prevent subclasses from changing the workflow sequence.
3. **Template vs Strategy?**
   → Template uses **inheritance** (fixed flow), Strategy uses **composition** (pluggable behavior).
4. **Give an example from Java.**
   → `HttpServlet`’s `service()` method in Java EE / Spring.
5. **What are hook methods?**
   → Optional steps in the template method that subclasses can override if needed.

---

### 🧠 **In short:**

> **Template Method = “Define the steps once, let subclasses fill in the blanks — without changing the flow.”**

---



## Pros and cons:

1. Pros:

-  You can let clients override only certain parts of a large algorithm, making them less affected by changes that happen to other parts of the algorithm.
- You can pull the duplicate code into a superclass.


2. Cons:

- Some clients may be limited by the provided skeleton of an algorithm.
- You might violate the Liskov Substitution Principle by suppressing a default step implementation via a subclass.
- Template methods tend to be harder to maintain the more steps they have.