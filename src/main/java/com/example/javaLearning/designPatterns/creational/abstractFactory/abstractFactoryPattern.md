# Module 3 — Abstract Factory (Creational)

## 1) Definition & Intent

**Abstract Factory** provides an interface for creating **families of related or dependent objects** without specifying their concrete classes.
It returns **a factory of factories** — each concrete factory creates a set of compatible products.

**Intent:** ensure that related products (a family) are created together and are compatible with each other, while decoupling client code from concrete implementations.

---

## 2) Problem it solves (when to use)

* You need to create **multiple related objects** that must work together (e.g., UI components for Windows vs Mac: `Button`, `Checkbox` that match visually/behaviorally).
* You want to **switch product families** at runtime (e.g., theme, DB vendor, protocol).
* You want to ensure clients only use objects from the same family (no mixing `MacButton` with `WindowsCheckbox`).

---

## 3) ❌ Bad example (no abstract factory — mixing families)

```java
// Two product hierarchies
interface Button { void paint(); }
class WindowsButton implements Button { public void paint(){ System.out.println("Windows button"); } }
class MacButton implements Button { public void paint(){ System.out.println("Mac button"); } }

interface Checkbox { void paint(); }
class WindowsCheckbox implements Checkbox { public void paint(){ System.out.println("Windows checkbox"); } }
class MacCheckbox implements Checkbox { public void paint(){ System.out.println("Mac checkbox"); } }

// Client manually picks concrete classes
class Application {
  private Button button;
  private Checkbox checkbox;

  public Application(String osType) {
    if ("WINDOWS".equalsIgnoreCase(osType)) {
      button = new WindowsButton();
      checkbox = new WindowsCheckbox();
    } else {
      button = new MacButton();
      checkbox = new MacCheckbox();
    }
  }
}
```

**Problems:**

* `Application` is coupled to concrete implementations.
* If you add a new OS family, you must change `Application`.
* Risk of mixing families accidentally if creation logic scattered.

---

## 4) ✅ Good example (Abstract Factory applied)

```java
// Products interfaces
public interface Button { void paint(); }
public interface Checkbox { void paint(); }

// Concrete product families
public class WindowsButton implements Button { @Override public void paint() { System.out.println("Windows Button"); } }
public class WindowsCheckbox implements Checkbox { @Override public void paint() { System.out.println("Windows Checkbox"); } }

public class MacButton implements Button { @Override public void paint() { System.out.println("Mac Button"); } }
public class MacCheckbox implements Checkbox { @Override public void paint() { System.out.println("Mac Checkbox"); } }

// Abstract Factory (family factory)
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Concrete factories for each family
public class WindowsFactory implements GUIFactory {
    @Override public Button createButton() { return new WindowsButton(); }
    @Override public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

public class MacFactory implements GUIFactory {
    @Override public Button createButton() { return new MacButton(); }
    @Override public Checkbox createCheckbox() { return new MacCheckbox(); }
}

// Client uses factory abstraction
public class Application {
    private final Button button;
    private final Checkbox checkbox;

    public Application(GUIFactory factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }

    public void paint() {
        button.paint();
        checkbox.paint();
    }
}

// Bootstrapping code
public class Demo {
    public static void main(String[] args) {
        GUIFactory factory = getFactoryForOS("WINDOWS"); // or "MAC" — decided at startup
        Application app = new Application(factory);
        app.paint();
    }

    private static GUIFactory getFactoryForOS(String os) {
        if ("WINDOWS".equalsIgnoreCase(os)) return new WindowsFactory();
        return new MacFactory();
    }
}
```

**Why this is better:**

* `Application` depends only on `GUIFactory` and product interfaces.
* Switching the family requires only changing the factory used at bootstrapping — satisfying OCP and DIP.
* Ensures compatibility: `WindowsFactory` returns only Windows products.

---

## 5) Real-world analogy

Think of an **electronics store** that sells product *sets* for regions.

* A “US kit” includes a US plug, a US adaptor, and US manuals.
* A “EU kit” includes EU plug + EU adaptor + EU manuals.
  You pick a kit (factory) and get a consistent set of items; you don’t mix US plug into EU kit.

---

## 6) When NOT to use / pitfalls

* Overkill for simple single-product creation — prefer Factory Method or registration approach if families are not needed.
* Creates many classes (one factory per family + concrete products) — may add complexity.
* If products vary independently, abstract factory can become rigid; consider builder or simple DI instead.
* Avoid if product variants change frequently — heavy-weight to maintain.

---

## 7) Testability notes

* Abstract Factory enables **mocking**: tests can inject a test `GUIFactory` that returns stubs or mocks of `Button`/`Checkbox`.
* You can unit test client logic independently from concrete products.
* In Spring/DI contexts, factories can be beans and swapped in tests.

---

## 8) Exercises (try in your IDE)

1. **UI theme exercise:** Implement `LightThemeFactory` and `DarkThemeFactory` returning `Button`, `TextBox`, and `Menu`. Create an `Application` that takes a factory and renders UI. Switch themes by changing the factory.
2. **DB vendor factory:** Create `SqlFactory` and `NoSqlFactory` producing `Connection`, `QueryBuilder`, `Session`. Demonstrate how switching factory changes whole DB stack.
3. **Test:** Write a unit test injecting a mock factory that returns mocks; verify `Application.paint()` calls the `paint()` methods on product mocks.

---

## 9) Interview questions (Abstract Factory)

1. What problem does Abstract Factory solve vs Factory Method?
2. Show the relationships (client, abstract factory, concrete factory, abstract products, concrete products).
3. When would you prefer Abstract Factory over Abstract Builder?
4. How does Abstract Factory support OCP and DIP?
5. Can you implement an Abstract Factory using dependency injection instead of concrete factory classes?

---

## 10) Practical notes & modern alternatives

* In modern applications with DI frameworks, often you don’t write explicit factory classes — you register families of beans and select which family to wire (profiles, qualifiers, configuration). The abstract factory pattern maps naturally to DI configuration.
* Another approach is **factory + registration**: a registry of factories keyed by family id (useful when adding families as plugins).


## 11) Pros and Cons

1. Pros:

- You can be sure that the products you’re getting from a factory are compatible with each other.
- You avoid tight coupling between concrete products and client code.
- Single Responsibility Principle. You can extract the product creation code into one place, making the code easier to support.
- Open/Closed Principle. You can introduce new variants of products without breaking existing client code.


2. Cons:

- The code may become more complicated than it should be, since a lot of new interfaces and classes are introduced along with the pattern.