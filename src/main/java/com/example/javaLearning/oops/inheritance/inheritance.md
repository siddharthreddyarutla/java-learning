# Inheritance

## 1. Concept Overview

* **Inheritance** = mechanism where a class (**child / subclass**) acquires properties and behaviors of another class (**parent / superclass**).
* Enables **code reuse** and **polymorphism**.

> Ex: 
> 1. If I created a vehicle class and provide engine and tires
> 2. When I scale my vehicle or introduce new version/upgraded car with more features as sound system, touch screen, AC etc.. But the engine and tires remains same and i can reuse that class and extend more features
> 3. By this people how wanted basic model can buy and as well as the new version of it as well

* Expresses **is-a relationship**:

    * `Dog` **is-a** `Animal` ✅
    * `Car` **is-a** `Vehicle` ✅
    * `Car` **is-a** `Engine` ❌ (better modeled by **composition**).

---

## 2. Key Points / Checklist

* Use **`extends`** keyword for inheritance.
* `super` keyword → access parent’s methods/constructors.
* A class can inherit from **only one parent** (Java supports **single inheritance**).
* But a class can implement **multiple interfaces**.
* Access levels:

    * `private` → not inherited.
    * `protected` → accessible in child.
* Constructors are **not inherited**, but **can be called** using `super()`.

---

## 3. Code Examples

### Example 1: Basic Inheritance

```java
// Parent class
public class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public void eat() {
        System.out.println(name + " is eating...");
    }
}

// Child class
public class Dog extends Animal {
    public Dog(String name) {
        super(name); // calling parent constructor
    }

    public void bark() {
        System.out.println(name + " says Woof!");
    }
}

// Main
public class Main {
    public static void main(String[] args) {
        Dog dog = new Dog("Buddy");
        dog.eat();   // Inherited method
        dog.bark();  // Child-specific method
    }
}
```

---

### Example 2: Using `super`

```java
// Parent
class Vehicle {
    int speed = 50;

    public void display() {
        System.out.println("Vehicle is moving at " + speed + " km/h");
    }
}

// Child
class Car extends Vehicle {
    int speed = 100;

    @Override
    public void display() {
        super.display(); // call parent method
        System.out.println("Car is moving at " + speed + " km/h");
    }
}

// Main
public class Test {
    public static void main(String[] args) {
        Car car = new Car();
        car.display();
    }
}
```

**Output:**

```
Vehicle is moving at 50 km/h
Car is moving at 100 km/h
```

---

### Example 3: Multilevel Inheritance

```java
class Animal {
    void breathe() { System.out.println("Breathing..."); }
}

class Mammal extends Animal {
    void walk() { System.out.println("Walking..."); }
}

class Dog extends Mammal {
    void bark() { System.out.println("Barking..."); }
}

public class Main {
    public static void main(String[] args) {
        Dog d = new Dog();
        d.breathe(); // from Animal
        d.walk();    // from Mammal
        d.bark();    // from Dog
    }
}
```

---

## 4. Mini Exercises

1. Create a `Person` class with `name`, `age`.

    * Create a `Student` subclass with `grade`.
    * Create a `Teacher` subclass with `subject`.

2. Create a `Shape` class with a method `area()`.

    * Subclasses: `Circle`, `Rectangle`. Override `area()` in each.

3. Create a `BankAccount` class with methods `deposit()`, `withdraw()`.

    * Subclass: `SavingsAccount` with an extra field `interestRate`.

4. Create a `Vehicle` class with `speed`.

    * Subclass `Bike` and `Car`. Add `applyBrake()` method that decreases speed differently in each.

5. (Challenge) Create a `Company` class with method `getDetails()`.

    * Subclass `Employee` with fields `empId`, `salary`.
    * Subclass `Manager` extends `Employee`, add `teamSize`. Override `getDetails()` to print all.

---

## 5. Common Interview Questions (with hints)

1. **What is inheritance in Java?**

    * Mechanism to acquire properties/methods from another class.

2. **Why does Java not support multiple inheritance with classes?**

    * To avoid the *diamond problem*. Instead, multiple inheritance is supported via **interfaces**.

3. **What is the difference between `extends` and `implements`?**

    * `extends` → inherit from a class.
    * `implements` → implement an interface.

4. **What does the `super` keyword do?**

    * Refers to the parent class (fields, methods, constructor).

5. **Can constructors be inherited?**

    * ❌ No. But child can call parent constructor using `super()`.

6. **Is it better to always use inheritance?**

    * ❌ No. Use inheritance only for **is-a relationships**. Prefer **composition** when it’s a **has-a relationship**.

---

## 6. Real-World Usage

* **Spring Framework**: many base classes (`HttpServlet`, `JpaRepository`) extended for reuse.
* **Java Collections**: `ArrayList` → inherits from `AbstractList`.
* **GUI frameworks**: `JFrame` extends `Frame`.

---

## 7. Suggested Mini-Project Task

Extend the **Library System (from Module 1 & 2)**:

* Create a base class `LibraryItem` with fields `id`, `title`.
* Subclass `Book` (add `author`) and `Magazine` (add `issueNumber`).
* Override `printDetails()` in each.
* Store multiple `LibraryItem` objects in an array/list and print details polymorphically.
