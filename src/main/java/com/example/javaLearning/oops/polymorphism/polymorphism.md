# Polymorphism

## 1. Concept Overview

* **Polymorphism** = "many forms" → the same object can behave differently depending on context.
* In Java, there are **two main types**:

    1. **Compile-time (Static) Polymorphism** → *Method Overloading*
    2. **Runtime (Dynamic) Polymorphism** → *Method Overriding*
* Enables **flexibility, extensibility, and dynamic behavior** in applications.

---

## 2. Key Points / Checklist

* **Method Overloading** (compile-time):

    * Same method name, different parameter list (number/type/order).
    * Return type *alone* is not enough.
* **Method Overriding** (runtime):

    * Subclass provides a new implementation of a parent’s method.
    * Must have **same signature** (name + parameters).
    * Return type must be same (or covariant).
    * Access modifier cannot be more restrictive.
    * Use **`@Override`** annotation (best practice).
* **Dynamic method dispatch**:

    * When a parent reference points to a child object, the **child’s method** executes at runtime.

---

## 3. Code Examples

### Example 1: Compile-time Polymorphism (Overloading)

```java
class MathUtil {
    // Overloaded methods
    public int add(int a, int b) {
        return a + b;
    }
    public double add(double a, double b) {
        return a + b;
    }
    public int add(int a, int b, int c) {
        return a + b + c;
    }
}

public class TestOverloading {
    public static void main(String[] args) {
        MathUtil util = new MathUtil();
        System.out.println(util.add(2, 3));       // 5
        System.out.println(util.add(2.5, 3.5));  // 6.0
        System.out.println(util.add(1, 2, 3));   // 6
    }
}
```

---

### Example 2: Runtime Polymorphism (Overriding)

```java
class Animal {
    void sound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends Animal {
    @Override
    void sound() {
        System.out.println("Dog barks");
    }
}

class Cat extends Animal {
    @Override
    void sound() {
        System.out.println("Cat meows");
    }
}

public class TestOverriding {
    public static void main(String[] args) {
        Animal a1 = new Dog();  // parent reference, child object
        Animal a2 = new Cat();

        a1.sound();  // Dog barks
        a2.sound();  // Cat meows
    }
}
```

---

### Example 3: Dynamic Method Dispatch in Action

```java
class Shape {
    void draw() { System.out.println("Drawing shape"); }
}

class Circle extends Shape {
    @Override
    void draw() { System.out.println("Drawing Circle"); }
}

class Rectangle extends Shape {
    @Override
    void draw() { System.out.println("Drawing Rectangle"); }
}

public class Main {
    public static void main(String[] args) {
        Shape s;               // reference of parent
        s = new Circle();
        s.draw();              // Drawing Circle

        s = new Rectangle();
        s.draw();              // Drawing Rectangle
    }
}
```

---

## 4. Mini Exercises

1. Create a class `Calculator` with overloaded `multiply()` methods:

    * Two ints, three ints, two doubles.

2. Create a `Vehicle` superclass with a method `fuelType()`.

    * Subclasses: `Car` (returns "Petrol"), `Truck` (returns "Diesel").
    * Show dynamic method dispatch.

3. Create a `Shape` hierarchy with `draw()` method (Circle, Square, Triangle).

    * Store them in an array of `Shape` references and call `draw()` on each.

4. (Challenge) Demonstrate **covariant return types**:

    * Parent method returns `Animal`.
    * Child method overrides it and returns `Dog`.

---

## 5. Common Interview Questions (with hints)

1. **What is polymorphism?**

    * Ability of an object to take many forms → method overloading (compile-time) and overriding (runtime).

2. **What is the difference between overloading and overriding?**

    * Overloading = same method name, different params (compile-time).
    * Overriding = same method signature, different implementation in subclass (runtime).

3. **What is dynamic method dispatch?**

    * Process of resolving method calls at runtime, based on object’s actual type, not reference type.

4. **What are covariant return types?**

    * When an overridden method can return a subtype of the parent’s return type.

5. **Why is overloading considered compile-time polymorphism?**

    * Because the method call is resolved at **compile time** (based on parameter types).

---

## 6. Real-World Usage

* **JDBC API**: `Connection`, `Statement`, `PreparedStatement` — polymorphism allows us to code against interfaces while using different implementations.
* **Collections API**: A `List` reference can point to `ArrayList` or `LinkedList`.
* **Spring Framework**: Beans often injected via parent/interface type, actual implementation decided at runtime.

---

## 7. Suggested Mini-Project Task

Extend the **Library System**:

* Create a superclass `LibraryItem` with method `borrowItem()`.
* Subclass `Book` and `Magazine`, override `borrowItem()` with different rules.
* Create an array of `LibraryItem` references and call `borrowItem()` polymorphically.
