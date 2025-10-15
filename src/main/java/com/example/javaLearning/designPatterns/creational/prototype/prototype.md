# Module 5 — Prototype Pattern

---

## 1) Definition & Intent

The **Prototype Pattern** lets you **create new objects by copying existing ones (prototypes)**, instead of creating them from scratch with `new`.

👉 Idea: Instead of instantiating and configuring a new object every time, you **clone** an existing “prototype” object.

---

## 2) Problem it solves

* When **object creation is expensive** (e.g., lots of setup, DB calls, network lookups).
* When you need **many similar objects** with only slight differences.
* When you want to **decouple object creation from specific classes** (client can clone a prototype without knowing its concrete type).

---

## 3) Example without Prototype (problem)

```java
class Document {
    private String title;
    private String content;
    private String author;

    public Document(String title, String content, String author) {
        // Imagine this does heavy DB/network setup...
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // getters/setters omitted
}
```

Usage:

```java
Document doc1 = new Document("Report", "Content...", "Alice");
Document doc2 = new Document("Report", "Content...", "Alice"); // repeated setup
```

❌ Every new document runs heavy setup logic.

---

## 4) ✅ Prototype Pattern applied

```java
// Prototype interface
interface Prototype<T> {
    T clone();
}

// Concrete class
class Document implements Prototype<Document> {
    private String title;
    private String content;
    private String author;

    public Document(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // Copy constructor (used by clone)
    private Document(Document other) {
        this.title = other.title;
        this.content = other.content;
        this.author = other.author;
    }

    @Override
    public Document clone() {
        return new Document(this);
    }

    @Override
    public String toString() {
        return "Document [title=" + title + ", content=" + content + ", author=" + author + "]";
    }
}
```

### Client

```java
public class Demo {
    public static void main(String[] args) {
        Document prototype = new Document("Report", "Content...", "Alice");

        // Clone instead of creating from scratch
        Document doc1 = prototype.clone();
        Document doc2 = prototype.clone();

        System.out.println(doc1);
        System.out.println(doc2);
    }
}
```

---

## 5) Shallow vs Deep Copy

* **Shallow copy** → copies references; nested objects still point to the same reference.
* **Deep copy** → copies everything, including nested objects (safe but more expensive).

Java ways:

* Implement `Cloneable` + override `clone()` (messy, not recommended in modern code).
* Prefer **copy constructor** or **static factory** (`from(other)`) for clarity.

---

## 6) Real-world analogy

Think of a **stamp** or **photocopy machine**:

* You design one **master copy** (prototype).
* Then clone it to produce identical (or slightly modified) copies.
* Saves time compared to designing from scratch each time.

---

## 7) When to use

* Object creation cost is **high**, but cloning is cheap.
* You need to create many objects that share the same base abstractState.
* You want to **hide the complexity of creating objects** from the client.

---

## 8) Interview questions

1. What is the Prototype pattern and when would you use it?
2. Difference between shallow copy and deep copy?
3. How does Prototype compare to Builder or Factory patterns?
4. Why is Java’s `Cloneable` considered broken? What alternatives would you use?
5. Give a real-world use case where cloning is better than `new`.

---

## 9) Exercises

1. Implement a `Shape` hierarchy (`Circle`, `Rectangle`) with a `clone()` method to duplicate shapes.
2. Implement a `DatabaseConnection` prototype where cloning avoids re-parsing connection configs.
3. Show difference between shallow copy and deep copy by cloning an object with a mutable `List`.


## 10) Pros and Cons

1. Pros:

- You can clone objects without coupling to their concrete classes.
- You can get rid of repeated initialization code in favor of cloning pre-built prototypes.
- You can produce complex objects more conveniently.
- You get an alternative to inheritance when dealing with configuration presets for complex objects.


2. Cons:

- Cloning complex objects that have circular references might be very tricky