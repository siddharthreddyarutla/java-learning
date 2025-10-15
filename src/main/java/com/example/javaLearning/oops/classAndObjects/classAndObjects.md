
# 🧩 Module 1: Classes & Objects in Java

## 1. Concept Overview

* **Class**: A **blueprint** (or template) that defines the structure (fields) and behavior (methods) of objects.
* **Object**: A **real-world entity** created from a class. Each object has its own abstractState (data) and can perform actions (methods).
* **OOP starts here**: without classes and objects, no other OOP principle makes sense.

---

## 2. Key Points / Checklist

* A class is declared using the `class` keyword.
* Objects are created using the `new` keyword.
* Fields → represent **abstractState** (data).
* Methods → represent **behavior** (functions).
* A class can have multiple objects, each with its own copy of instance variables.
* `this` keyword → refers to the current object.
* Memory: objects live on the **heap**, references live on the **stack**.

---

## 3. Code Examples

### Example 1: Simple Class and Object

```java
// Car.java
public class Car {
    // Fields (abstractState)
    String brand;
    int speed;

    // Constructor (initializes object)
    public Car(String brand, int speed) {
        this.brand = brand;
        this.speed = speed;
    }

    // Method (behavior)
    public void accelerate(int increment) {
        speed += increment;
        System.out.println(brand + " is now going at " + speed + " km/h.");
    }

    public void brake() {
        speed -= 10;
        System.out.println(brand + " slowed down to " + speed + " km/h.");
    }
}
```

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        Car car1 = new Car("Tesla", 50);  // object creation
        Car car2 = new Car("BMW", 30);

        car1.accelerate(20); // Tesla is now going at 70 km/h.
        car2.brake();        // BMW slowed down to 20 km/h.
    }
}
```

---

### Example 2: Multiple Objects

```java
public class Dog {
    String name;
    int age;

    public Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void bark() {
        System.out.println(name + " says: Woof! Woof!");
    }
}

class TestDogs {
    public static void main(String[] args) {
        Dog d1 = new Dog("Buddy", 3);
        Dog d2 = new Dog("Max", 5);

        d1.bark(); // Buddy says: Woof! Woof!
        d2.bark(); // Max says: Woof! Woof!
    }
}
```

---

## 4. Mini Exercises

Try these on your own (write full classes with `main()`):

1. Create a `Book` class with fields: `title`, `author`, `price`. Add a method `printDetails()` to print book info.
2. Create a `Student` class with fields: `name`, `marks`. Add a method `isPass()` that returns true if marks >= 40.
3. Modify the `Car` class to include a method `honk()` that prints “Beep Beep!”.
4. Create two `Employee` objects and print their details using a method `printEmployeeInfo()`.
5. (Challenge) Create a `BankAccount` class with fields: `accountNumber`, `balance`. Add methods `deposit(double amount)` and `withdraw(double amount)` (check if balance is enough before withdrawing).

---

## 5. Common Interview Questions (with hints)

1. **What is a class in Java?**

    * A blueprint/template from which objects are created.

2. **What is an object?**

    * An instance of a class with its own abstractState and behavior.

3. **How are objects stored in memory?**

    * Objects live in the **heap**, references in the **stack**.

4. **Difference between `class` and `object`?**

    * Class = definition; Object = instance of that definition.

5. **What is the `new` keyword used for?**

    * To create new objects in heap memory.

6. **Can a class exist without objects?**

    * Yes, but it’s useless until instantiated (except static context).

---

## 6. Real-World Usage

* **Class** = design (like "Car model").
* **Object** = actual instance (like "My Tesla car").
* In large applications:

    * Classes = models/entities (User, Product, Transaction).
    * Objects = runtime entities representing real-world data.

---

## 7. Suggested Mini-Project Task

* Create a **Library System (Phase 1)**:

    * Class: `Book` with fields (title, author, availableCopies).
    * Allow creating multiple `Book` objects.
    * Method: `borrowBook()` decreases available copies, `returnBook()` increases it.

