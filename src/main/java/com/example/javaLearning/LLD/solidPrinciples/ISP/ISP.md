# Module 4 — Interface Segregation Principle (ISP)

## 📖 Definition

> **Clients should not be forced to depend on interfaces they do not use.**
> 👉 In other words: **create small, specific interfaces** instead of one giant “god” interface.

---

## ❌ BAD Example (violates ISP)

```java
interface Machine {
    void print();
    void scan();
    void fax();
}

class SimplePrinter implements Machine {
    @Override
    public void print() {
        System.out.println("Printing...");
    }

    @Override
    public void scan() {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public void fax() {
        throw new UnsupportedOperationException("Not supported!");
    }
}
```

### Problems:

* `SimplePrinter` just prints, but it’s **forced** to implement `scan()` and `fax()`.
* If another class needs only `print()`, it still sees extra methods it doesn’t care about.
* Clients are **depending on methods they don’t use** → violates ISP.

---

## ✅ GOOD Example (ISP applied)

```java
// Split into smaller, role-specific interfaces
interface Printer {
    void print();
}

interface Scanner {
    void scan();
}

interface Fax {
    void fax();
}

// SimplePrinter only needs printing
class SimplePrinter implements Printer {
    @Override
    public void print() {
        System.out.println("Printing...");
    }
}

// Multi-function printer needs all
class MultiFunctionPrinter implements Printer, Scanner, Fax {
    @Override
    public void print() {
        System.out.println("Printing...");
    }

    @Override
    public void scan() {
        System.out.println("Scanning...");
    }

    @Override
    public void fax() {
        System.out.println("Faxing...");
    }
}
```

👉 Now:

* `SimplePrinter` depends only on `Printer`.
* `MultiFunctionPrinter` implements all needed capabilities.
* Clients depend only on the **interfaces they use**, not the whole bundle. ✅

---

## 💡 Real-world Analogy

Think of a **restaurant menu** 🍴:

* Instead of **one giant menu** with veg + non-veg + drinks + desserts, you get **separate menus**.
* If you’re vegetarian, you take the **veg menu** only.
* You’re not forced to look at things you’ll never order.

👉 That’s Interface Segregation in real life.

---

## 🧪 Unit-testing notes

* ISP makes mocking and testing easier.

    * If your service needs only `Printer`, you mock only that interface.
    * You don’t need to stub irrelevant methods like `scan()` or `fax()`.
* Tests are focused and cleaner.

---

## 🔧 Refactor Exercise (your turn)

You’re given this bad interface:

```java
interface Worker {
    void work();
    void eat();
}

class Robot implements Worker {
    @Override
    public void work() {
        System.out.println("Robot working...");
    }

    @Override
    public void eat() {
        throw new UnsupportedOperationException("Robots don’t eat!");
    }
}
```

👉 Refactor using ISP so that:

* Humans both **work** and **eat**.
* Robots only **work**.
* Clients depending on `Worker` don’t get `eat()` if they don’t need it.

---

## ⚠️ Common Violations of ISP

* “Fat” interfaces with many unrelated methods.
* Classes implementing methods with `throw new UnsupportedOperationException`.
* Client code receiving interfaces with methods it never calls.

---

## ✅ Rules of Thumb

* Prefer **many small interfaces** over one bloated one.
* Group methods logically (Printer, Scanner, Fax rather than Machine).
* Clients should only see **what they actually need**.

---

## 🎯 Interview Questions (ISP)

1. What is the Interface Segregation Principle? Why is it important?
2. Give an example of a fat interface and how you would refactor it.
3. How does ISP improve testability?
4. Is it possible to overuse ISP? (hint: too many small interfaces → fragmentation).
5. How is ISP different from SRP?
