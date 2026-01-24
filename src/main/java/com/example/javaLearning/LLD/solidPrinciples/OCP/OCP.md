# Module 2 — Open/Closed Principle (OCP)

## 📖 Definition

> **Classes, modules, and functions should be open for extension but closed for modification.**

This means:

* You should be able to **add new behavior** to a system **without changing existing, working code**.
* Instead of editing old classes every time requirements change, you design them in a way that allows extension (via inheritance, interfaces, or composition).

---

## ❌ BAD Example (Violating OCP)

```java
class AreaCalculator {
    public double calculateArea(Object shape) {
        if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            return Math.PI * c.radius * c.radius;
        } else if (shape instanceof Rectangle) {
            Rectangle r = (Rectangle) shape;
            return r.length * r.breadth;
        } else if (shape instanceof Triangle) {
            Triangle t = (Triangle) shape;
            return 0.5 * t.base * t.height;
        }
        return 0;
    }
}

class Circle {
    double radius;
    Circle(double r) { this.radius = r; }
}
class Rectangle {
    double length, breadth;
    Rectangle(double l, double b) { this.length = l; this.breadth = b; }
}
class Triangle {
    double base, height;
    Triangle(double b, double h) { this.base = b; this.height = h; }
}
```

### Problems:

* Every time we add a new shape (like Square, Polygon), we must **modify** `AreaCalculator`.
* This breaks OCP because the class is **not closed for modification**.
* High risk: a change for one new shape could accidentally break old calculations.

---

## ✅ GOOD Example (Applying OCP using Polymorphism)

```java
// Common abstraction
interface Shape {
    double area();
}

// Circle implementation
class Circle implements Shape {
    double radius;
    Circle(double r) { this.radius = r; }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

// Rectangle implementation
class Rectangle implements Shape {
    double length, breadth;
    Rectangle(double l, double b) { this.length = l; this.breadth = b; }

    @Override
    public double area() {
        return length * breadth;
    }
}

// AreaCalculator is closed for modification
class AreaCalculator {
    public double calculateArea(Shape shape) {
        return shape.area();
    }
}
```

### Benefits:

* `AreaCalculator` doesn’t need changes when new shapes are added.
* To add a new shape → just create a new class implementing `Shape`.
* Code is **open for extension** but **closed for modification** ✅.

---

## 💡 Real-World Analogy

Think of a **smartphone OS**:

* When new apps are added (banking app, music app), the OS doesn’t change.
* The OS just **defines an abstraction (API)** for apps.
* Developers extend functionality by building new apps → OS remains untouched.

If OS had to be modified for every new app → it would be chaos!

---

## 🧪 Unit-testing note

* With OCP, you test each `Shape` class in isolation.
* No need to keep re-testing `AreaCalculator` when adding a new shape.
* Reduces regression testing effort drastically.

---

## 🔧 Refactor Practice Exercise

Take this **bad design**:

```java
class PaymentProcessor {
    public void processPayment(String type) {
        if ("CREDIT_CARD".equals(type)) {
            // process credit card
        } else if ("PAYPAL".equals(type)) {
            // process PayPal
        } else if ("BITCOIN".equals(type)) {
            // process Bitcoin
        }
    }
}
```

👉 Refactor it using OCP so new payment methods can be added without modifying `PaymentProcessor`.
(Hint: Create a `PaymentMethod` interface and concrete classes.)

---

## 🛑 Common Violations of OCP

* Long `switch` or `if-else` chains based on type.
* Huge classes where new requirements always force modifications.
* Mixing multiple business rules in a single class.

---

## 🎯 Interview Questions (OCP)

1. Explain the Open/Closed Principle in simple words.
2. How can polymorphism help in achieving OCP?
3. Give an example from your project where you applied OCP.
4. What’s the drawback if you don’t follow OCP?
5. Can OCP ever be overused? (Hint: Too many abstractions / unnecessary interfaces.)