
# 🏗️ Module 6: Composition vs Inheritance in Java

---

## 1. Concept Overview

* **Inheritance** = *Is-a relationship*

    * A subclass extends a superclass and inherits its properties/behaviors.
    * Example: `Car` is-a `Vehicle`.

* **Composition** = *Has-a relationship*

    * A class contains references to other classes as fields, instead of extending them.
    * Example: `Car` has-a `Engine`.

---

## 2. Key Points / Checklist

* **Inheritance**

    * Good for “is-a” relationships.
    * Promotes **code reuse**.
    * Can lead to **tight coupling** and fragile hierarchy if misused.

* **Composition**

    * Good for “has-a” relationships.
    * Promotes **flexibility** (you can change parts without breaking hierarchy).
    * Encourages **looser coupling**.

* Rule of thumb:

    * Use **inheritance** when the relationship is naturally “is-a”.
    * Use **composition** when the relationship is “has-a” or when you want more flexibility.

---

## 3. Code Examples

### Example 1: Inheritance (Is-a)

```java
class Vehicle {
    void start() {
        System.out.println("Vehicle starting...");
    }
}

class Car extends Vehicle {
    void honk() {
        System.out.println("Car honks!");
    }
}

public class Main {
    public static void main(String[] args) {
        Car car = new Car();
        car.start();  // Inherited method
        car.honk();   // Own method
    }
}
```

👉 Here: `Car` **is-a** `Vehicle`.

---

### Example 2: Composition (Has-a)

```java
class Engine {
    void start() {
        System.out.println("Engine starts...");
    }
}

class Car {
    private Engine engine;  // Composition

    public Car() {
        this.engine = new Engine();
    }

    void startCar() {
        engine.start();  // Delegation
        System.out.println("Car is ready to go!");
    }
}

public class Main {
    public static void main(String[] args) {
        Car car = new Car();
        car.startCar();
    }
}
```

👉 Here: `Car` **has-a** `Engine`.

---

### Example 3: Why Favor Composition

```java
interface Engine {
    void start();
}

class PetrolEngine implements Engine {
    @Override
    public void start() {
        System.out.println("Petrol Engine starts...");
    }
}

class ElectricEngine implements Engine {
    @Override
    public void start() {
        System.out.println("Electric Engine starts silently...");
    }
}

class Car {
    private Engine engine;

    // Inject engine via constructor (flexibility!)
    public Car(Engine engine) {
        this.engine = engine;
    }

    void startCar() {
        engine.start();
    }
}

public class Main {
    public static void main(String[] args) {
        Car petrolCar = new Car(new PetrolEngine());
        petrolCar.startCar();   // Petrol Engine starts...

        Car electricCar = new Car(new ElectricEngine());
        electricCar.startCar(); // Electric Engine starts silently...
    }
}
```

👉 This way, **Car doesn’t care what type of Engine it has**.
We can swap engines without changing `Car` class → that’s **composition flexibility**.

---

## 4. Mini Exercises

1. Create a `Library` class that **has-a** `Book` object.

    * Add method `printBookDetails()`.

2. Create a `Company` class with a list of `Employee` objects (composition).

3. Create an inheritance example: `Bird` superclass, `Sparrow` subclass.

    * Add a method `fly()`.

4. (Challenge) Redesign the `Bird` example using **composition** with a `Flyable` interface.

    * Inject different flying behaviors (`CanFly`, `CannotFly`).

---

## 5. Common Interview Questions (with hints)

1. **What is the difference between inheritance and composition?**

    * Inheritance = is-a, tight coupling, reuse by extension.
    * Composition = has-a, loose coupling, reuse by delegation.

2. **When should you prefer composition over inheritance?**

    * When the relationship is has-a.
    * When you want flexibility to swap parts without changing hierarchy.

3. **What are the drawbacks of inheritance?**

    * Fragile base class problem.
    * Tight coupling, harder to maintain.

4. **Give real-world examples of composition.**

    * `Car` has-an `Engine`.
    * `Library` has `Books`.

5. **Which design principle relates to this?**

    * “Favor composition over inheritance” (part of **Gang of Four (GoF) design principles**).

---

## 6. Real-World Usage

* **Inheritance**:

    * `ArrayList` extends `AbstractList`.
    * `IOException` extends `Exception`.

* **Composition**:

    * `HashMap` has-a `HashTable` internally.
    * `Car` has-an `Engine`.
    * **Spring Framework**: Beans injected into other beans = composition.

---

## 7. Suggested Mini-Project Task

Extend the **Library System**:

* `Library` class **has-a** list of `LibraryItem` objects (composition).
* `Book` and `Magazine` extend `LibraryItem` (inheritance).
* Show both approaches together in a clean design.

---

✅ **End of Module 7: Composition vs Inheritance**

---

🎉 Now you’ve covered **all core OOP concepts in Java**:

* Encapsulation
* Inheritance
* Polymorphism (Overloading/Overriding)
* Abstraction
* Composition (Has-a vs Is-a)

---

👉 Do you want me to now create a **single cheat sheet summarizing all 5 pillars of OOP in Java** for quick revision before moving into advanced topics like **SOLID principles & design patterns**?
