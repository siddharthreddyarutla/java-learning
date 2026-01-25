# Module 9 — Composite Pattern (Structural)

---

## 1) Definition & Intent

**Composite** lets you treat **individual objects** and **compositions (groups)** of objects uniformly.
You build a tree structure of objects where leaves are simple objects and nodes are composites that hold children. The client interacts with the component interface and doesn’t care whether it’s a leaf or a composite.

**Intent:** represent whole-part hierarchies and allow clients to work with single objects and groups through the same interface.

---

## 2) When to use (problem it solves)

* You have hierarchical data (e.g., file system, UI components, organization chart).
* You want the same operations to apply to both single objects and groups (e.g., `render()`, `print()`, `getSalary()`).
* Avoid special-case code (no `if (isComposite) ... else ...` at client side).

---

## 3) Bad design (no composite — client has to treat leaves and groups differently)

```java
// Pseudo
class File { void print(); }
class Directory { List<File> children; void printAll(); }

// Client:
if (node instanceof File) node.print();
else if (node instanceof Directory) for (child : dir.children) child.print();
```

Problems:

* Client must know about concrete types and handle traversal logic.
* Adding new operations requires updating many places.

---

## 4) ✅ Good design (Composite applied)

### Java example — file system-like structure

```java
import java.util.ArrayList;
import java.util.List;

// Component
interface FileSystemNode {
    void print(String indent);
}

// Leaf
class FileLeaf implements FileSystemNode {
    private final String name;
    public FileLeaf(String name) { this.name = name; }
    @Override
    public void print(String indent) {
        System.out.println(indent + "- " + name);
    }
}

// Composite
class DirectoryComposite implements FileSystemNode {
    private final String name;
    private final List<FileSystemNode> children = new ArrayList<>();
    public DirectoryComposite(String name) { this.name = name; }

    public void add(FileSystemNode node) { children.add(node); }
    public void remove(FileSystemNode node) { children.remove(node); }

    @Override
    public void print(String indent) {
        System.out.println(indent + "+ " + name);
        for (FileSystemNode child : children) {
            child.print(indent + "  ");
        }
    }
}

// Client demo
public class CompositeDemo {
    public static void main(String[] args) {
        DirectoryComposite root = new DirectoryComposite("root");
        DirectoryComposite home = new DirectoryComposite("home");
        DirectoryComposite alice = new DirectoryComposite("alice");

        FileLeaf file1 = new FileLeaf("notes.txt");
        FileLeaf file2 = new FileLeaf("todo.txt");
        FileLeaf file3 = new FileLeaf("photo.jpg");

        alice.add(file1);
        alice.add(file3);
        home.add(alice);
        root.add(home);
        root.add(file2);

        root.print(""); // client treats everything via FileSystemNode
    }
}
```

Output (example):

```
+ root
  + home
    + alice
      - notes.txt
      - photo.jpg
  - todo.txt
```

---

## 5) Real-world analogy

Think of a **folder tree** on your computer:

* A folder can contain files **and** other folders.
* Whether you open a file or a folder, you use similar operations (open, delete, move) — the Composite models that.

---

## 6) Variations & considerations

* **Transparent composite:** Component interface includes child-management methods (`add`, `remove`). Easy but leaf must implement them (may throw `UnsupportedOperationException`).
* **Safe composite:** Only composite exposes `add`/`remove`, component interface only has common ops. Safer but client must know if it’s a composite when modifying children.

---

## 7) When NOT to use / pitfalls

* Overuse when structure is simple — composite adds complexity.
* If leaf and composite do very different things, forcing a single interface can be awkward.
* Beware cycles; trees are assumed acyclic — ensure you don’t create loops.

---

## 8) Testability & benefits

* Test leaf behaviour and composite traversal separately.
* Clients can be tested against the `Component` interface using both leaf and composite instances.
* Promotes uniformity and reduces branching code.

---

## 9) Exercises (try these)

1. **Organization chart:** Implement `Employee` (leaf) with `getSalary()` and `Manager` (composite) that aggregates salary of subordinates. Write a method to compute total payroll for a subtree.
2. **GUI:** Implement `UIComponent` interface with `render()`; create `Button` (leaf) and `Panel` (composite) that can contain other components. Demonstrate nested rendering.
3. **Permissions:** Build a resource-permission tree where permissions propagate from folders to files; show how to query effective permissions.

---

## 10) Interview questions

1. What is the Composite pattern? Give an example.
2. What's the difference between transparent and safe composite? Pros/cons?
3. How does Composite relate to Iterator (traversing children)?
4. How do you prevent cycles in a Composite tree?
5. When would you avoid using Composite?


## 11)  Pros and Cons

1. Pros:


- You can work with complex tree structures more conveniently: use polymorphism and recursion to your advantage.
- Open/Closed Principle. You can introduce new element types into the app without breaking the existing code, which now works with the object tree.


2. Cons:

- It might be difficult to provide a common interface for classes whose functionality differs too much. In certain scenarios, you’d need to overgeneralize the component interface, making it harder to comprehend.