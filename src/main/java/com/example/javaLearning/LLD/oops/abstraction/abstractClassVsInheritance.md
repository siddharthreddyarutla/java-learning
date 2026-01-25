
# 🤔 Abstract Class vs Interface — Why Do We Need Abstract Classes?

---

## 1. Similarities (Why the doubt arises)

* Both **cannot be instantiated** directly.
* Both can declare **abstract methods**.
* Both allow concrete methods:

    * **Abstract class** → concrete methods (since always).
    * **Interface** → default/static/private methods (since Java 8/9).
* Both allow constants:

    * **Abstract class** → `public static final` fields.
    * **Interface** → fields are implicitly `public static final`.

So yes ✅ — on the surface, they look interchangeable.

---

## 2. Key Differences (Justification for Abstract Classes)

### 🔹 1. State (Fields)

* **Abstract class**: can have **instance variables** (fields with values that vary per object).
* **Interface**: can only have `public static final` constants (no instance abstractState).

**Example:**

```java
abstract class Animal {
    protected String name;  // instance variable
    public Animal(String name) { this.name = name; }
}

interface AnimalInterface {
    String TYPE = "Animal"; // constant only
}
```

👉 If you want **abstractState (fields)** in the base class → use **abstract class**.

---

### 🔹 2. Constructors

* **Abstract class**: can have **constructors** → useful for initializing abstractState for subclasses.
* **Interface**: ❌ no constructors.

**Example:**

```java
abstract class Vehicle {
    protected String brand;
    public Vehicle(String brand) {
        this.brand = brand; // initialized
    }
}

class Car extends Vehicle {
    public Car(String brand) {
        super(brand);
    }
}
```

👉 If you need **shared initialization logic** → use **abstract class**.

---

### 🔹 3. Multiple Inheritance

* **Abstract class**: Java allows extending **only one abstract class**.
* **Interface**: A class can implement **multiple interfaces** → more flexible.

👉 If you want **multiple contracts** → use **interfaces**.

---

### 🔹 4. Access Modifiers

* **Abstract class**: methods can have **any access modifier** (`public`, `protected`, `private`).
* **Interface**: methods are **implicitly public** (except `private` helper methods since Java 9).

---

### 🔹 5. Use-case Semantics

* **Abstract class** → “is-a base type with shared code + abstractState”.
* **Interface** → “is-a capability/contract”.

**Example:**

* Abstract class: `Animal` (shared abstractState like `name`, shared behavior like `breathe()`).
* Interface: `Flyable`, `Swimmable` (capabilities that can be added to many classes).

---

## 3. Real-World Justification

### Example: **Abstract class (shared code + abstractState)**

```java
abstract class Shape {
    protected String color;

    public Shape(String color) {
        this.color = color; // constructor for common abstractState
    }

    abstract double area();

    public void displayColor() {
        System.out.println("Color: " + color);
    }
}

class Circle extends Shape {
    double radius;
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}
```

👉 Here `Shape` provides **shared abstractState (color)** and behavior.
This cannot be done with only interfaces.

---

### Example: **Interface (capability)**

```java
interface Flyable {
    void fly();
}

interface Swimmable {
    void swim();
}

class Duck implements Flyable, Swimmable {
    public void fly() { System.out.println("Duck flies"); }
    public void swim() { System.out.println("Duck swims"); }
}
```

👉 `Duck` is not forced into an inheritance hierarchy.
It just **adds capabilities** → **multiple interfaces supported**.

---

## 4. The Rule of Thumb

* **Use abstract class when**:

    * Classes are closely related and share **abstractState + implementation**.
    * You want to provide **constructors** and **protected members**.
    * Example: `HttpServlet` in Java EE.

* **Use interface when**:

    * Classes can be **unrelated** but must follow the same contract.
    * You want **multiple inheritance of behavior**.
    * Example: `Comparable`, `Runnable`.

---

## 5. Interview-Ready Answer

👉 If asked *“Why use abstract class when interfaces can do almost everything?”*
You can say:

* Abstract classes are for **base classes with shared abstractState + implementation**.
* Interfaces are for **contracts or capabilities across unrelated classes**.
* Example:

    * Abstract class: `Shape` defines common fields (like `color`) and a method `area()`.
    * Interface: `Flyable` defines a behavior that can be applied to unrelated classes (`Bird`, `Plane`).

---

✅ That’s why **both exist in Java** — they solve different design problems.

---

👉 Do you want me to now make a **concise comparison table (Abstract class vs Interface)** as a quick cheat sheet for interviews?
