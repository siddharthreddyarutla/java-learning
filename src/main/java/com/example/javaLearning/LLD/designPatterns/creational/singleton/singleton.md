# Module 1 — Singleton (Creational)

[Resource Link](https://refactoring.guru/design-patterns/singleton)

## 1) Definition & intent

**Singleton** ensures a class has **only one instance** and provides a **global point of access** to it.

**Intent:** control instance creation for resources that must be single (configuration manager, runtime registry, logging, JVM-wide caches, etc.).

---

## 2) Problem it solves

* When exactly one instance is required and you need easy global access.
* Example: logger, system config, master connection pool manager (note: prefer DI-managed singletons in many apps).

---

## 3) Bad (naïve) example — non-thread-safe lazy init

```java
public class NaiveSingleton {
    private static NaiveSingleton instance; // initially null

    private NaiveSingleton() { }

    public static NaiveSingleton getInstance() {
        if (instance == null) {
            instance = new NaiveSingleton(); // race condition in multithreaded env
        }
        return instance;
    }
}
```

**Problem:** Race condition — two threads can create two instances.

---

## 4) Common correct implementations

### A — Eager initialization (thread-safe, simple)

```java
public class EagerSingleton {
    private static final EagerSingleton INSTANCE = new EagerSingleton();
    private EagerSingleton() {}
    public static EagerSingleton getInstance() { return INSTANCE; }
}
```

* Pros: simple, thread-safe.
* Cons: instance created even if never used (ok for cheap objects).

### B — Initialization-on-demand holder idiom (recommended)

```java
public class HolderSingleton {
    private HolderSingleton() {}

    private static class Holder {
        static final HolderSingleton INSTANCE = new HolderSingleton();
    }

    public static HolderSingleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

* Pros: lazy initialization, thread-safe without synchronized, idiomatic and efficient.

### C — Double-checked locking (DCL) — Java 5+ (with volatile)

```java
public class DCLSingleton {
    private static volatile DCLSingleton instance;
    private DCLSingleton() {}

    public static DCLSingleton getInstance() {
        if (instance == null) {
            synchronized (DCLSingleton.class) {
                if (instance == null) {
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}
```

* Pros: lazy + avoids sync overhead after init.
* Cons: more complex; requires `volatile`. Use only if you understand it.

### D — Enum Singleton (best for singletons that must be safe against serialization/reflection)

```java
public enum EnumSingleton {
    INSTANCE;

    public void doSomething() {
        // singleton behavior
    }
}
```

* Pros: simplest, provides serialization safety and guards against reflection creating new instances. Joshua Bloch recommends this in Effective Java.
* Cons: less flexible if your singleton must extend a class or implement certain patterns (but you can implement interfaces).

---

## 5) Serialization and reflection caveats

* For normal singletons, implement `private Object readResolve()` to preserve Singleton on deserialization:

```java
private Object readResolve() {
    return getInstance();
}
```

* Enum singletons automatically handle serialization.
* Reflection can break singletons by calling the private constructor. You can guard (throw exception if instance already created) — but enum provides stronger guarantee.

---

## 6) When **not** to use Singleton

* Overuse leads to **global abstractState** which increases coupling and makes testing harder.
* Prefer **dependency injection** (framework-managed singletons) for application services. Use patterns rather than raw singles for testability.
* Avoid Singletons for business-logic objects that should be ephemeral or multiple per context.

---

## 7) Testability notes

* Static singletons are hard to mock. Prefer constructor injection (DI) so you can inject a test double.
* If you must use a global singleton in tests, allow a way to reset or register a test instance (dangerous) or switch to DI during testing.

**Example**: In Spring, declare a bean with scope singleton — then `@Autowired` it in consumers. Tests can inject mocks instead.

---

## 8) Quick exercises (try in your IDE)

1. Implement `HolderSingleton` and prove only one instance is created by launching many threads that call `getInstance()` and printing `(System.identityHashCode(inst))`.
2. Implement `EnumSingleton` for a `Logger` with a `log(String)` method; serialize and deserialize it and show identity preserved.
3. Replace a static-global logger in a simple app by a DI-injected logger bean and write a unit test that injects a mock logger.

---

## 9) Pros/Cons & common interview points

**Pros**

* Controlled access to single resource.
* Good for objects that are inherently single (JVM-wide config).

**Cons**

* Global abstractState — can hide dependencies and make testing difficult.
* Overuse leads to tight coupling and concurrency pitfalls if not done carefully.

**Interview questions to expect**

1. What is a Singleton and when would you use it?
2. How do you implement a thread-safe singleton in Java? Explain pros/cons of DCL vs Holder vs Enum.
3. Why is `volatile` required for double-checked locking?
4. How does serialization affect singletons and how do you fix it?
5. Why might you prefer DI-managed singletons over the Singleton pattern?
6. How can singletons make testing harder — how do you mitigate it?

---

## 10) Minimal takeaways

* Use **Initialization-on-demand holder** or **Enum** for correctness and simplicity.
* Prefer **DI** (framework) singletons for application services to keep testable & decoupled.
* Avoid uncontrolled global abstractState — use singletons sparingly.

---

Would you like me to continue now with **Module 2 — Factory Method** (same detailed format), or would you prefer to try the Singleton exercises first and then I continue?
