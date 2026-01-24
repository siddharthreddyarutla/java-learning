# 🟢 What is LSP in plain English?

👉 **If you have a base class, then any subclass should be usable in place of that base class, without breaking the program.**

That’s it.

* If `Parent` has some methods, `Child` must behave in such a way that you can replace `Parent` with `Child` safely.
* **Subclass should “fit in” wherever the base class is expected.**

---

# ⚡ Example: Without LSP (Problem case)

```java
class Bird {
    void fly() {
        System.out.println("Flying...");
    }
}

class Sparrow extends Bird {
    @Override
    void fly() {
        System.out.println("Sparrow flying...");
    }
}

class Ostrich extends Bird {
    @Override
    void fly() {
        throw new UnsupportedOperationException("Ostrich can't fly!");
    }
}

public class Zoo {
    public static void letBirdFly(Bird bird) {
        bird.fly();
    }

    public static void main(String[] args) {
        letBirdFly(new Sparrow());  // Works ✅
        letBirdFly(new Ostrich());  // ❌ BOOM! Throws exception
    }
}
```

👉 Problem: `Ostrich` **is-a Bird**, but it **can’t** do what Bird promises (`fly()`).
So **substituting** `Ostrich` for `Bird` **breaks the program** → LSP violation.

---

# 🟢 Fixing with LSP

We split behavior into correct abstractions:

```java
interface Bird {
    void eat();
}

interface Flyable {
    void fly();
}

class Sparrow implements Bird, Flyable {
    @Override
    public void eat() { System.out.println("Sparrow eating..."); }
    @Override
    public void fly() { System.out.println("Sparrow flying..."); }
}

class Ostrich implements Bird {
    @Override
    public void eat() { System.out.println("Ostrich eating..."); }
}

public class Zoo {
    public static void letBirdFly(Flyable bird) {
        bird.fly(); // ✅ Safe: only called for birds that can fly
    }

    public static void main(String[] args) {
        letBirdFly(new Sparrow());   // Works
        // letBirdFly(new Ostrich()); ❌ Compiler error, can't pass Ostrich
    }
}
```

👉 Now:

* `Sparrow` can substitute `Bird` or `Flyable`.
* `Ostrich` can substitute `Bird` (but not `Flyable`).
* No runtime errors, no surprises. ✅

---

# 🏠 Real-world analogy

Imagine:

* A **House key** works for opening doors.
* If I give you a **Car key** but say “it’s just another house key,” you’ll try to open your door and fail. That’s an **LSP violation**.
* A **substitute key** must behave like the original → otherwise, it’s misleading.

---

# 🧪 Quick Rule of Thumb

Ask yourself:

* “Can I replace the base class object with this subclass object **without changing how the program works**?”
* If **yes → LSP is satisfied**.
* If **no → redesign needed** (split interfaces, use composition, etc.).

---

# 🔧 Classic Example: Rectangle vs Square

```java
class Rectangle {
    protected int width, height;
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int w) {
        this.width = w;
        this.height = w; // force equal sides
    }

    @Override
    public void setHeight(int h) {
        this.width = h;
        this.height = h;
    }
}
```

👉 Looks logical (Square **is-a** Rectangle)…
But:

```java
Rectangle r = new Square();
r.setWidth(5);
r.setHeight(10);

System.out.println(r.getArea()); // Expected 5*10=50, but gets 100 ❌
```

* `Square` doesn’t behave like a true `Rectangle`.
* Replacing Rectangle with Square breaks expectations.
* That’s an **LSP violation**.

---

# ✅ How to Fix

Model them separately (no inheritance) or use **composition**.

```java
interface Shape {
    int getArea();
}

class Rectangle implements Shape {
    private int width, height;
    Rectangle(int w, int h) { this.width = w; this.height = h; }
    public int getArea() { return width * height; }
}

class Square implements Shape {
    private int side;
    Square(int side) { this.side = side; }
    public int getArea() { return side * side; }
}
```

👉 Now, each shape works correctly. No false promises. ✅

---

# 🎯 Key takeaway

**LSP = subclasses should honor the contract of the base class.**

* If a base class says “I can do X,” then all subclasses must be able to do X properly.
* If not, your design is wrong — maybe you need separate interfaces or composition.

---

# Interview-style summary answer

> *“LSP means that any subclass should be usable in place of its base class without breaking the behavior of the program. For example, if a `Bird` class has `fly()`, then all subclasses must be able to fly. If some birds like Ostrich can’t fly, then forcing them into the same inheritance hierarchy violates LSP. The fix is to separate capabilities (like `Flyable` interface) so that only true flying birds implement it. This ensures substitutability.”*

---