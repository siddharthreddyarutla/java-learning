# 📝 OOP Cheat Sheet (Modules 1–3)

---

## 🔹 Module 1: Classes & Objects

* **Class** = blueprint; **Object** = instance.
* Fields → state; Methods → behavior.
* `new` keyword → creates objects in heap.
* `this` → refers to current object.

**Example:**

```java
class Car {
    String brand;
    int speed;

    Car(String brand, int speed) {
        this.brand = brand;
        this.speed = speed;
    }
}
Car c = new Car("Tesla", 100);
```

**Interview must-knows:**

* Difference between class & object?
* Where are objects stored in memory?
* Can we create objects without `new`? (Yes → reflection, cloning, deserialization)

---

## 🔹 Module 2: Encapsulation

* **Encapsulation** = hiding fields + controlled access through methods.
* Use `private` for fields, `public` getters/setters for access.
* Add **validation** in setters.
* For immutability → use `final` + only getters.

**Example:**

```java
class Student {
    private int age;
    public void setAge(int age) { if(age > 0) this.age = age; }
    public int getAge() { return age; }
}
```

**Interview must-knows:**

* Encapsulation vs Abstraction?
* Why use private fields?
* What is an immutable class?

---

## 🔹 Module 3: Inheritance

* **Inheritance** = subclass acquires properties/methods of superclass.
* `extends` keyword for classes.
* `super` keyword → access parent fields, methods, constructors.
* **Single inheritance only** (multiple via interfaces).
* Use **is-a relationship** (Car is-a Vehicle).

**Example:**

```java
class Animal {
    void eat() { System.out.println("Eating..."); }
}
class Dog extends Animal {
    void bark() { System.out.println("Woof!"); }
}
```

**Interview must-knows:**

* Why no multiple inheritance in Java? (Diamond problem)
* Difference between `extends` and `implements`?
* Can constructors be inherited? (No, but can call with `super()`)

---

## 🚀 Quick Mnemonic for Revision

* **C** → Class/Object = “What it is”
* **E** → Encapsulation = “Hide it”
* **I** → Inheritance = “Reuse it”

---

Perfect 👍 — before we jump into **Module 5 (Abstraction)**, here’s a **cheat sheet summary** of **Module 4: Polymorphism** so you can revise quickly.

---

## Polymorphism Cheat Sheet (Module 4)

## 🔹 Definition

* **Polymorphism** = "many forms" → the same object behaves differently based on context.
* Two types in Java:

    1. **Compile-time Polymorphism** → *Method Overloading*
    2. **Runtime Polymorphism** → *Method Overriding* (dynamic dispatch)

---

## 🔹 Method Overloading (Compile-time)

* Same method name, different parameter list.
* Resolved at **compile-time**.
* Return type alone doesn’t matter.

**Example:**

```java
class MathUtil {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
}
```

---

## 🔹 Method Overriding (Runtime)

* Subclass provides new implementation of parent’s method.
* Same name + parameters.
* Return type: same or covariant.
* Access modifier: cannot be more restrictive.
* Resolved at **runtime** using actual object type.

**Example:**

```java
class Animal { void sound() { System.out.println("Animal sound"); } }
class Dog extends Animal { @Override void sound() { System.out.println("Bark"); } }

Animal a = new Dog();
a.sound();  // Bark
```

---

## 🔹 Dynamic Method Dispatch

* Parent reference → Child object → Child method executes.
* Core of runtime polymorphism.

---

## 🔹 Covariant Return Types

* Overridden method can return a **subtype** of parent’s return type.

---

## 🔹 Real-World Examples

* `List list = new ArrayList();`
* `Connection con = DriverManager.getConnection();`
* Spring beans injected by **interface**, implemented by multiple classes.

---

## 🔹 Interview Must-Knows

* Difference between overloading & overriding?
* Why is overloading compile-time and overriding runtime?
* What is dynamic method dispatch?
* What are covariant return types?
* Example of polymorphism in Collections API?

---

