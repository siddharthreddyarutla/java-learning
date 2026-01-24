# Module 3 — Liskov Substitution Principle (LSP)

## 📖 Definition

> **If `S` is a subtype of `T`, then objects of type `T` should be replaceable with objects of type `S` without altering desirable program properties (correctness, task performed, etc.).**

In plain words: **subclasses must behave like their base classes**. A subclass should not surprise callers by breaking expected behavior.

---

## ❌ BAD Example (Classic `Bird` / `Ostrich` problem)

```java
class Bird {
    public void fly() {
        System.out.println("Flying...");
    }
}

class Sparrow extends Bird {
    @Override
    public void fly() {
        System.out.println("Sparrow flying");
    }
}

class Ostrich extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Ostrich can't fly");
    }
}

// Client code
public class BirdWatcher {
    public void letBirdFly(Bird b) {
        b.fly(); // expects bird to fly
    }
}
```

### Why this violates LSP:

* `Ostrich` is a `Bird` but cannot perform the expected `fly()` operation.
* Replacing a `Bird` with an `Ostrich` breaks the client (`BirdWatcher`) which assumes `fly()` is valid.
* Subtypes should preserve the contract (behavioral expectations) of supertypes.

---

## ✅ Correct Approaches (Design fixes)

### Approach A — Re-think the inheritance: split capabilities into smaller abstractions

```java
interface Bird {
    void eat();
}

interface Flyable {
    void fly();
}

class Sparrow implements Bird, Flyable {
    @Override public void eat() { System.out.println("Sparrow eating"); }
    @Override public void fly() { System.out.println("Sparrow flying"); }
}

class Ostrich implements Bird {
    @Override public void eat() { System.out.println("Ostrich eating"); }
}

// Client code now depends on the specific capability it needs:
class BirdWatcher {
    public void letItFly(Flyable f) {
        f.fly(); // safe: Flyable guarantees fly()
    }
}
```

**Result:** `Ostrich` is no longer forced to implement a method it can't support. Clients depend on the capability interface they actually need.

---

### Approach B — Use composition instead of inheritance when behavior differs

```java
class FlyingBehavior {
    public void fly() {
        System.out.println("Flying...");
    }
}

class Bird {
    private final FlyingBehavior flyingBehavior; // nullable if non-flyer

    public Bird(FlyingBehavior flyingBehavior) {
        this.flyingBehavior = flyingBehavior;
    }

    public void performFly() {
        if (flyingBehavior == null) {
            throw new UnsupportedOperationException("This bird cannot fly");
        }
        flyingBehavior.fly();
    }

    public void eat() { System.out.println("Eating..."); }
}

class Sparrow extends Bird {
    public Sparrow() { super(new FlyingBehavior()); }
}

class Ostrich extends Bird {
    public Ostrich() { super(null); } // no flying behavior
}
```

**Result:** Clients can query or use `performFly()` only when meaningful; you avoid incorrect substitution by modeling behavior explicitly.

---

### Approach C — Strengthen the contract (document and enforce preconditions/postconditions)

If a base class documents `fly()` as always available, subclasses must honor that. If some subtypes can’t satisfy it, the base type was a bad abstraction.

---

## 💡 Real-World Analogy

Think of a **power socket** that promises to deliver electricity. If a new “socket” subclass is installed that only accepts USB devices (no mains power), anything expecting mains power will fail. The new socket must not be presented as the same kind of socket unless it truly fulfills the same promises.

---

## 🧪 Unit-testing notes (how LSP helps testing)

* When LSP holds, tests written for the base type should pass for all subtypes.
* Create a **test suite** for the base type’s contract (behavioral tests), and run it against each implementation. If a subtype fails the suite, it violates LSP.
* Example: tests that assert `fly()` changes position or returns success — all `Flyable` implementations should satisfy these tests.

---

## 🔧 Refactor exercise (do this on your own)

You have this class:

```java
class Rectangle {
    public void setWidth(int w) { ... }
    public void setHeight(int h) { ... }
    public int getArea() { ... }
}

class Square extends Rectangle {
    @Override public void setWidth(int w) { ... } // sets both w and h
    @Override public void setHeight(int h) { ... } // sets both
}
```

* Why might `Square` break LSP?
* Refactor so that rectangles and squares are modeled without violating LSP (hint: prefer separate interfaces or no inheritance between them).

---

## ⚠️ Common LSP Violations and Code Smells

* Subclass methods throw `UnsupportedOperationException` for operations declared in the parent.
* Subclasses silently change semantics (e.g., different units, different exception behavior).
* Overriding methods that weaken postconditions or strengthen preconditions (changing required inputs).
* Client code that uses `instanceof` or type checks to guard behavior — usually a sign of bad substitution design.

---

## ✅ Rules of thumb to follow LSP

* Design base classes by *behavioral contract*, not by convenience of code reuse.
* If not all subtypes can provide a behavior, move that behavior to a more specific interface.
* Keep method semantics consistent: same inputs -> same kinds of outputs/exceptions across subtypes.
* Use composition if subclassing would force incorrect behavior.

---

## Interview questions (LSP)

1. What is the Liskov Substitution Principle? Give a real example of a violation.
2. How do you detect LSP violations in a codebase?
3. Explain why `UnsupportedOperationException` in a subclass can indicate an LSP problem.
4. How would you refactor an inheritance hierarchy where some subclasses can’t support a base class operation?
5. How do preconditions and postconditions relate to LSP? Give a short example.

---

## Quick summary

* LSP is about **behavioral compatibility** between base types and subtypes.
* If a subclass can’t fully honor the base class contract, change the design: narrow interfaces, separate capabilities, or use composition.
* A good practice: write base-type behavioral tests and run them against every subtype — if they pass, LSP holds.
