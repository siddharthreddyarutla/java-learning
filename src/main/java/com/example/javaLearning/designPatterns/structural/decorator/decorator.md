# Module 10 — Decorator Pattern (Structural)

---

## 1) Definition & Intent

**Decorator** attaches additional responsibilities to an object **dynamically**. It provides a flexible alternative to subclassing for extending behavior.
Instead of creating many subclasses for combinations of features, you wrap objects with decorators that add behavior before/after delegating to the wrapped object.

**Intent:** add functionality to individual objects at runtime without changing other objects of the same class.

---

## 2) Problem it solves

* You need to add features (logging, encryption, formatting, toppings) to objects without exploding the number of subclasses (the combinatorial subclass problem).
* You want to add/remove responsibilities at runtime to specific objects, not to all instances of a class.

---

## 3) Bad design (lots of subclasses)

Imagine a `Coffee` with size + many possible toppings. Subclassing every combination leads to `EspressoWithMilkAndSugar`, `LatteWithSoyAndCaramel`, etc. That’s unmaintainable.

---

## 4) ✅ Good design — Decorator applied

### Simple Coffee example (readable beginner-friendly)

```java
// Component
public interface Coffee {
    String getDescription();
    double cost();
}

// Concrete component
public class SimpleCoffee implements Coffee {
    @Override public String getDescription() { return "Simple coffee"; }
    @Override public double cost() { return 2.0; }
}

// Decorator base (also implements Coffee)
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee coffee;
    protected CoffeeDecorator(Coffee coffee) { this.coffee = coffee; }
    @Override public String getDescription() { return coffee.getDescription(); }
    @Override public double cost() { return coffee.cost(); }
}

// Concrete decorators
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    @Override public String getDescription() { return coffee.getDescription() + ", milk"; }
    @Override public double cost() { return coffee.cost() + 0.5; }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) { super(coffee); }
    @Override public String getDescription() { return coffee.getDescription() + ", sugar"; }
    @Override public double cost() { return coffee.cost() + 0.2; }
}

// Usage
public class Demo {
    public static void main(String[] args) {
        Coffee c = new SimpleCoffee();
        c = new MilkDecorator(c);   // add milk
        c = new SugarDecorator(c);  // add sugar
        System.out.println(c.getDescription() + " = $" + c.cost());
        // Output: Simple coffee, milk, sugar = $2.7
    }
}
```

### Real Java standard-library example

`java.io` uses Decorator heavily: `InputStream` → `BufferedInputStream` wraps another `InputStream`; `FilterInputStream` is the decorator base class.

---

## 5) Real-world analogy

Think of **wrapping a present**:

* The gift (core object) is wrapped with wrapping paper (decorator 1), then a ribbon (decorator 2), then a gift tag (decorator 3).
* Each wrapper adds something without changing the original gift.

---

## 6) When to use / advantages

* When you want to add responsibilities **to some objects** and not to others of the same class.
* Avoid subclass explosion and keep classes small & focused.
* Compose decorators at runtime for flexible behavior.

---

## 7) When **not** to use (pitfalls)

* Overuse leads to many small decorator classes — manage complexity.
* Deep decorator chains can be hard to debug and reason about (hard to know final behavior).
* If all instances always need the same set of features, prefer a simpler design (single class or parameters) rather than many decorators.

---

## 8) Testability & patterns to combine

* Test each decorator independently by passing a simple stub `Coffee` (or using mocks).
* Combine with **Strategy** when the added behavior is algorithmic (decorator adds cross-cutting behavior, strategy provides algorithm).
* Use **Factory/Registry** to assemble decorated objects if many combinations are required.

---

## 9) Exercises (try in your IDE)

1. Implement `Pizza` component and decorators `Cheese`, `Olives`, `ExtraSauce`. Chain different decorators and compute total price & description.
2. Wrap a `MessageSender` object with `EncryptionDecorator`, `CompressionDecorator`, and `LoggingDecorator` — ensure order matters and test it.
3. Re-implement your `ReportGenerator` so `ReportGenerator` is the component and you have decorators for `CsvFormatting`, `Compression`, `AuditLogging`. Show composing decorators at runtime.

---

## 10) Interview questions (Decorator)

1. What is the Decorator pattern and how does it differ from inheritance?
2. Give a Java standard library example of Decorator.
3. When would you choose Decorator vs Subclassing?
4. How does Decorator compare to Proxy and Adapter?
5. How would you unit test a decorator chain?

---