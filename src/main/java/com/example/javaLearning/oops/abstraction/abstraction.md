# Abstraction

## 1. Concept Overview

* **Abstraction** = *hiding implementation details and showing only essential features*.
* Focuses on **what an object does**, not **how it does it**.
* Achieved in Java by:

    1. **Abstract classes** (`abstract` keyword)
    2. **Interfaces**

---


## 2. Key Points / Checklist

* **Abstract Class**

    * Declared with `abstract` keyword.
    * Can have **abstract methods** (without body).
    * Can also have **concrete methods** (with body).
    * Cannot be instantiated directly.
    * Child class must implement abstract methods.
* **Interface**

    * 100% abstract before Java 8.
    * Since Java 8 → can have `default` and `static` methods.
    * Since Java 9 → can have `private` methods.
    * A class can implement **multiple interfaces** (supports multiple inheritance).

---

## 3. Code Examples

### Example 1: Abstract Class

```java
abstract class Animal {
    abstract void sound();  // abstract method (no body)

    public void breathe() { // concrete method
        System.out.println("All animals breathe");
    }
}

class Dog extends Animal {
    @Override
    void sound() {
        System.out.println("Dog barks");
    }
}

public class Main {
    public static void main(String[] args) {
        Animal a = new Dog();  // upcasting
        a.sound();             // Dog barks
        a.breathe();           // All animals breathe
    }
}
```

---

### Example 2: Interface

```java
interface Vehicle {
    void start();
    void stop();
}

class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car starts with key");
    }
    @Override
    public void stop() {
        System.out.println("Car stops with brake");
    }
}

class Bike implements Vehicle {
    @Override
    public void start() {
        System.out.println("Bike starts with button");
    }
    @Override
    public void stop() {
        System.out.println("Bike stops with hand brake");
    }
}

public class Main {
    public static void main(String[] args) {
        Vehicle v = new Car();
        v.start(); // Car starts with key
        v.stop();  // Car stops with brake
    }
}
```

---

### Example 3: Interface with Default Method (Java 8+)

```java
interface Payment {
    void pay(double amount);

    default void printReceipt(double amount) {
        System.out.println("Paid " + amount + " successfully!");
    }
}

class CreditCardPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card.");
    }
}

public class Main {
    public static void main(String[] args) {
        Payment p = new CreditCardPayment();
        p.pay(2000);
        p.printReceipt(2000);
    }
}
```

---

## 4. Mini Exercises

1. Create an abstract class `Shape` with method `area()`. Implement in `Circle` and `Rectangle`.
2. Create an interface `Playable` with methods `play()`, `pause()`, `stop()`. Implement in `MusicPlayer` and `VideoPlayer`.
3. Create an abstract class `Employee` with abstract method `calculateSalary()`. Subclasses: `FullTimeEmployee`, `PartTimeEmployee`.
4. Create an interface `Notification` with `send()`. Implement `EmailNotification` and `SMSNotification`.
5. (Challenge) Create an interface `Database` with methods `connect()`, `disconnect()`. Implement `MySQLDatabase` and `MongoDBDatabase`. Use polymorphism to switch DBs at runtime.

---

## 5. Common Interview Questions (with hints)

1. **What is abstraction?**

    * Hiding implementation details, showing only essential features.

2. **How is abstraction achieved in Java?**

    * Using abstract classes and interfaces.

3. **Difference between abstract class and interface?**

    * Abstract class → can have abstract + concrete methods, fields, constructors.
    * Interface → pure contract (though since Java 8/9, can have default, static, private methods).
    * Class can extend 1 abstract class, but implement multiple interfaces.

4. **Can we create an object of abstract class?**

    * ❌ No, but you can have a reference of abstract class pointing to child object.

5. **When to use abstract class vs interface?**

    * Abstract class → when classes share **state + behavior**.
    * Interface → when you want a pure contract across unrelated classes.

6. **What is marker interface?**

    * Interface with no methods (e.g., `Serializable`). Used for tagging.

---

## 6. Real-World Usage

* **Abstract classes**:

    * Java’s `HttpServlet` is abstract → you extend and override `doGet()`, `doPost()`.
* **Interfaces**:

    * JDBC → `Connection`, `Statement`, `ResultSet`.
    * Collections → `List`, `Set`, `Map`.
* **Spring Framework**: heavy use of interfaces for flexibility.

---

## 7. Suggested Mini-Project Task

Extend the **Library System**:

* Create an abstract class `LibraryItem` with abstract method `borrowItem()`.
* Subclasses `Book` and `Magazine` implement differently.
* Create an interface `Searchable` with method `search(String keyword)`.
* Implement in both `Book` and `Magazine`.
