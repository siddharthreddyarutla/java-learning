# Module 14 — Strategy Pattern (Behavioral)

---

## 1) Definition & Intent

**Strategy** defines a family of algorithms, encapsulates each one, and makes them interchangeable.
It lets the algorithm vary independently from clients that use it.

**Intent:** separate *what* is done from *how* it’s done — pick the behavior at runtime.

---

## 2) Problem it solves (when to use)

* When a class uses different algorithms or behaviors that change at runtime (sorting, validation, pricing rules).
* When you want to avoid large `if/else` or `switch` blocks selecting behavior.
* When you want to **swap** algorithms for testing or configuration without changing client code.

---

## 3) Anti-pattern (bad code)

```java
public class PaymentService {
    public void pay(String method, double amount) {
        if ("CREDIT".equals(method)) {
            // credit card logic
        } else if ("DEBIT".equals(method)) {
            // debit logic
        } else if ("UPI".equals(method)) {
            // upi logic
        } // grows as you add methods
    }
}
```

Problems: lots of branching, violates OCP — every new algorithm requires editing `PaymentService`.

---

## 4) ✅ Good example — Strategy applied

### Classic Java (interfaces + concrete strategies)

```java
// Strategy interface
public interface PaymentStrategy {
    void pay(double amount);
}

// Concrete strategies
public class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card");
    }
}

public class UpiPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using UPI");
    }
}

// Context (client)
public class PaymentProcessor {
    private PaymentStrategy strategy;

    public PaymentProcessor(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void pay(double amount) {
        strategy.pay(amount); // delegates to strategy
    }
}

// Usage
public class Demo {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor(new CreditCardPayment());
        processor.pay(100.0); // credit card

        processor.setStrategy(new UpiPayment());
        processor.pay(50.0); // UPI
    }
}
```

### Java 8+ concise variant with lambdas (functional strategy)

```java
import java.util.function.Consumer;

public class DemoLambda {
    public static void main(String[] args) {
        Consumer<Double> credit = amt -> System.out.println("Paid " + amt + " via Credit");
        Consumer<Double> upi = amt -> System.out.println("Paid " + amt + " via UPI");

        Consumer<Double> current = credit;
        current.accept(120.0);

        current = upi;
        current.accept(60.0);
    }
}
```

---

## 5) Real-world analogy

Think of a **payment terminal** that supports multiple payment cards/apps:

* Terminal (context) delegates actual payment processing to whichever method the customer selects (strategy).
* Adding a new payment method (e.g., mobile wallet) means adding a new pluggable handler, not changing the terminal logic.

---

## 6) When NOT to use / pitfalls

* Overkill if you only have two simple branches that will never change.
* Don’t create too many tiny strategy classes — consider using lambdas or registration maps for compactness.
* If strategies need to share state, manage lifecycle carefully (stateless strategies are easier).

---

## 7) Testability & benefits

* Strategies are **easy to unit test** independently.
* Context can be tested with mock/dummy strategies.
* Improves adherence to **Open/Closed Principle** — add new strategies without changing the context.

---

## 8) Variations & practical patterns

* **Strategy Registry**: map keys → strategy implementations (useful for runtime lookup/config).
* **Factory + Strategy**: combine with a factory to create strategies based on config.
* **State Pattern**: when strategies also represent states and transitions, consider State pattern (behavior depends on current state and transitions).

---

## 9) Exercises (try in your IDE)

1. **Discount strategies**: Implement `DiscountStrategy` with `NoDiscount`, `PercentageDiscount`, `FixedAmountDiscount`. Create a `Checkout` context that applies selected discount.
2. **Sorting strategy**: Implement context that sorts `List<Product>` using different `Comparator` strategies (price, rating, name) and swap at runtime.
3. **Validation strategy with registration**: Implement `Map<String, Validator>` and a `FormProcessor` that picks a validator based on form type.

---

## 10) Interview questions (Strategy)

1. What is the Strategy Pattern? Give a real example.
2. How does Strategy help OCP and DIP?
3. How would you implement Strategy in Java using lambdas?
4. When would you choose Strategy vs State?
5. How do you test code that uses Strategy?

---

## 11) Bonus — Registration-based Strategy (practical)

If you want runtime selection by key (common in apps), use a registry:

```java
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class StrategyRegistry {
    private final Map<String, Consumer<Double>> registry = new ConcurrentHashMap<>();

    public void register(String key, Consumer<Double> strategy) {
        registry.put(key, strategy);
    }

    public Consumer<Double> get(String key) {
        return registry.get(key);
    }

    public static void main(String[] args) {
        StrategyRegistry reg = new StrategyRegistry();
        reg.register("CREDIT", amt -> System.out.println("Credit pay " + amt));
        reg.register("UPI", amt -> System.out.println("UPI pay " + amt));

        Consumer<Double> s = reg.get("UPI");
        s.accept(75.0);
    }
}
```

This mirrors how Spring often injects a `Map<String, Strategy>` of beans.

---

## 12) Quick checklist for implementing Strategy well

* Define a **clear strategy interface** (single responsibility).
* Keep strategies **stateless** if possible (simplifies reuse).
* Use **DI or registry** for wiring strategies in apps.
* Prefer lambdas for small, stateless strategies to reduce boilerplate.