# 🧱 Module 13 — Flyweight Pattern

---

## 1️⃣ Definition & Intent

> The **Flyweight Pattern** is used to **minimize memory usage** by sharing common (intrinsic) state between multiple objects instead of creating duplicates.

It helps when you have **a large number of similar objects**, and storing all of them individually would consume too much memory.

---

## 2️⃣ Problem it solves

When you have thousands or millions of objects that share some data,
e.g.:

* Characters in a text editor.
* Trees in a game map.
* Particles in a simulation.
* Emojis or icons rendered repeatedly.

👉 Instead of creating separate copies of repeating data, store shared data (intrinsic state) in a **shared object** and external data (extrinsic state) outside.

---

## 3️⃣ Real-world analogy

🪶 Think of a **forest of trees** 🌲:

* All trees share the same species info (color, texture, shape).
* But each tree differs in position, height, or age.

Rather than creating 1 million `Tree` objects with duplicate species info,
you create:

* **Flyweight object** for each unique type (e.g. Oak, Pine, Mango).
* Reuse them for each individual tree position.

---

## 4️⃣ Key Concepts

| Type                  | Meaning                                                                              |
| --------------------- | ------------------------------------------------------------------------------------ |
| **Intrinsic State**   | Shared, constant data (e.g., color, shape, texture). Stored in the Flyweight object. |
| **Extrinsic State**   | Context-specific data (e.g., position, coordinates). Passed externally when used.    |
| **Flyweight Factory** | Ensures shared objects are reused (caches and returns existing ones).                |

---

## 5️⃣ Example — Forest Simulation 🌳

```java
import java.util.HashMap;
import java.util.Map;

// Flyweight class (shared data)
class TreeType {
    private final String name;
    private final String color;
    private final String texture;

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }

    public void draw(int x, int y) {
        System.out.println("Drawing " + name + " tree at (" + x + "," + y + ") with color " + color);
    }
}

// Factory that manages Flyweights
class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String texture) {
        String key = name + "-" + color + "-" + texture;
        return treeTypes.computeIfAbsent(key, k -> {
            System.out.println("Creating new TreeType: " + name);
            return new TreeType(name, color, texture);
        });
    }
}

// Context class (extrinsic data)
class Tree {
    private final int x;
    private final int y;
    private final TreeType type;

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        type.draw(x, y);
    }
}

// Client (forest)
public class FlyweightDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            TreeType oak = TreeFactory.getTreeType("Oak", "Green", "Rough");
            Tree tree = new Tree(i * 10, i * 20, oak);
            tree.draw();
        }

        for (int i = 0; i < 3; i++) {
            TreeType pine = TreeFactory.getTreeType("Pine", "Dark Green", "Smooth");
            Tree tree = new Tree(i * 15, i * 25, pine);
            tree.draw();
        }

        System.out.println("\nTotal unique TreeTypes created: " + TreeFactory.getTreeTypeCount());
    }
}
```

Add this to your `TreeFactory`:

```java
public static int getTreeTypeCount() {
    return treeTypes.size();
}
```

### ✅ Output:

```
Creating new TreeType: Oak
Drawing Oak tree at (0,0) with color Green
Drawing Oak tree at (10,20) with color Green
...
Creating new TreeType: Pine
Drawing Pine tree at (0,0) with color Dark Green
Drawing Pine tree at (15,25) with color Dark Green

Total unique TreeTypes created: 2
```

👉 8 trees drawn, but only **2 unique `TreeType` objects** created!
Memory saved — thanks to **Flyweight Pattern** 🎯

---

## 6️⃣ Benefits

✅ Reduces memory usage dramatically when many objects share common data.
✅ Improves performance in systems with repetitive elements (e.g., UI elements, game objects).
✅ Centralizes immutable data for consistency.

---

## 7️⃣ Drawbacks

⚠️ Code complexity — separating intrinsic/extrinsic data adds boilerplate.
⚠️ Less flexibility — shared objects must be immutable to be thread-safe.
⚠️ If extrinsic data grows large, memory savings diminish.

---

## 8️⃣ Real-world Java examples

* **`java.lang.Integer.valueOf(int)`** — caches integers from -128 to 127.
* **`String.intern()`** — stores shared copies of string literals in the string pool.
* **Font, Icon, and Shape rendering in JavaFX/Swing** — shared shape or texture data reused across instances.

---

## 9️⃣ When to use

* Large number of small objects that repeat intrinsic data.
* You can identify a **shared core (intrinsic)** that can be factored out.
* Object creation/memory cost is high.

---

## 🔟 Exercises

1. Create a **CharacterFlyweight** for a text editor that reuses the same character objects (A–Z).
2. Build a **VehicleFactory** that reuses car models (`SUV`, `Hatchback`, etc.) while varying color and plate number.
3. Optimize your earlier `ReportGenerator` example by caching reusable report templates via Flyweight.

---

## 🧠 Interview Questions

1. What is the Flyweight pattern?
2. What are intrinsic and extrinsic states?
3. How does the Flyweight pattern reduce memory usage?
4. Give examples from Java’s core libraries that use Flyweight.
5. What are the limitations or pitfalls of using Flyweight?

---

✅ **In short:**

> **Flyweight** = “Don’t duplicate what can be shared.”
> Store intrinsic (shared) data once and externalize extrinsic (unique) data to reduce memory.
