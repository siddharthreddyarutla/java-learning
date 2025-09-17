
# 🕵️ Understanding Abstraction (Simplified)

---

## 🔹 Definition (in plain words)

**Abstraction** = *hiding the "how" and showing only the "what"*.

* You **don’t care about implementation**, you just **use the features provided**.
* It lets us **design systems by focusing on behavior (what to do)** instead of worrying about low-level details (how it’s done).

---

## 🔹 Real-World Analogy

* Think about a **TV remote**:

    * You press **volume up** or **channel change** (features).
    * You don’t care **how signals travel inside circuits** (implementation hidden).

---

## 🔹 When to Use

* Use **abstract class** when:

    * You want to share **common code** among subclasses.
    * You expect subclasses to have some **default behavior** but also enforce some methods to be implemented.
    * Example: `HttpServlet` → provides default implementations (`doGet()`, `doPost()`) but you override only what you need.

* Use **interface** when:

    * You want a **contract** that multiple unrelated classes can implement.
    * You want to support **multiple inheritance of type** (since class can implement many interfaces).
    * Example: `Comparable`, `Runnable`, `Serializable`.

---

## 🔹 Example: Vehicle Abstraction

### Interface Example

```java
interface Vehicle {
    void start();
    void stop();
}

class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car starts with a key.");
    }
    @Override
    public void stop() {
        System.out.println("Car stops with hydraulic brakes.");
    }
}

class Bike implements Vehicle {
    @Override
    public void start() {
        System.out.println("Bike starts with a button.");
    }
    @Override
    public void stop() {
        System.out.println("Bike stops with disc brakes.");
    }
}

public class Main {
    public static void main(String[] args) {
        Vehicle v1 = new Car();
        Vehicle v2 = new Bike();

        v1.start(); // Car starts with a key.
        v2.start(); // Bike starts with a button.
    }
}
```

👉 Here, **`Vehicle` interface hides the details**.
We only know `start()` and `stop()` exist.
How Car and Bike start/stop = **implementation hidden**.

---

### Abstract Class Example

```java
abstract class Shape {
    // Abstract method (no body) - must be implemented
    abstract double area();

    // Concrete method (shared by all)
    public void display() {
        System.out.println("This is a shape.");
    }
}

class Circle extends Shape {
    double radius;
    Circle(double radius) { this.radius = radius; }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {
    double length, width;
    Rectangle(double l, double w) { this.length = l; this.width = w; }

    @Override
    double area() {
        return length * width;
    }
}

public class Main {
    public static void main(String[] args) {
        Shape s1 = new Circle(5);
        Shape s2 = new Rectangle(4, 6);

        s1.display(); // This is a shape.
        System.out.println("Circle area: " + s1.area());

        s2.display(); // This is a shape.
        System.out.println("Rectangle area: " + s2.area());
    }
}
```

👉 Here:

* `Shape` defines **what all shapes must do** → `area()`.
* Each subclass provides **how** it’s done.
* The **implementation details are hidden** from the user.

---

## 🔹 In Short

* **Abstraction**: Focus on *what* an object does, not *how*.
* **Interface**: Perfect for unrelated objects (Car, Bike, Plane can all be `Vehicle`).
* **Abstract class**: Perfect for related objects with shared code (all Shapes have area, but calculated differently).

---

✅ Now, abstraction should feel **practical + interview clear**.

---

👉 Do you want me to now continue with **Module 6: Method Overloading vs Method Overriding** in full detail,
or prepare a **short cheat sheet for Abstraction** before moving ahead?
