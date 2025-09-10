
# 🔐 Module 2: Encapsulation in Java

## 1. Concept Overview

* **Encapsulation** = **data hiding + controlled access**.
* A class should **hide its internal state (fields)** and only expose controlled access through **methods (getters/setters)**.
* Prevents accidental corruption of data and enforces rules.

👉 Think of a **capsule medicine**: the drug (data) is hidden, you only interact with the capsule (methods).

---

## 2. Key Points / Checklist

* Use **`private`** for fields (variables).
* Provide **public getters/setters** for controlled access.
* Can enforce rules inside setters (e.g., age must be > 0).
* Improves **data security**, **maintainability**, and **flexibility**.
* Often paired with **immutability** (make class unmodifiable).

---

## 3. Code Examples

### Example 1: Encapsulation with Getters and Setters

```java
// Student.java
public class Student {
    private String name;  // hidden from outside
    private int age;

    // Getter
    public String getName() {
        return name;
    }

    // Setter with validation
    public void setName(String name) {
        if(name == null || name.isEmpty()) {
            System.out.println("Name cannot be empty!");
        } else {
            this.name = name;
        }
    }

    // Getter
    public int getAge() {
        return age;
    }

    // Setter with validation
    public void setAge(int age) {
        if(age > 0) {
            this.age = age;
        } else {
            System.out.println("Age must be positive!");
        }
    }
}
```

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        Student s = new Student();
        s.setName("Alice");
        s.setAge(20);

        System.out.println("Student: " + s.getName() + ", Age: " + s.getAge());

        s.setAge(-5); // will print: Age must be positive!
    }
}
```

---

### Example 2: Immutable Encapsulation

```java
// ImmutableEmployee.java
public final class ImmutableEmployee {
    private final String name;
    private final double salary;

    public ImmutableEmployee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    // Only getters, no setters
    public String getName() { return name; }
    public double getSalary() { return salary; }
}
```

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        ImmutableEmployee emp = new ImmutableEmployee("John", 50000);
        System.out.println(emp.getName() + " earns $" + emp.getSalary());

        // emp.salary = 60000; ❌ Not allowed, fields are private + final
    }
}
```

---

## 4. Mini Exercises

1. Create an `Account` class with `balance` (private). Add methods `deposit(double amount)` and `withdraw(double amount)` with validation (no negative withdrawals).
2. Create a `BankCustomer` class with fields `name`, `phoneNumber`. Use setters with validation (phoneNumber must be 10 digits).
3. Modify the `Student` class to allow only names starting with a capital letter.
4. Create an `ImmutableBook` class with fields `title`, `author`. Provide only getters, no setters.
5. (Challenge) Create a `SecureLocker` class with a `password` field. Only allow `openLocker(String enteredPassword)` if the password matches.

---

## 5. Common Interview Questions (with hints)

1. **What is encapsulation?**

    * Wrapping data and methods together, and restricting direct access to fields.

2. **How is encapsulation implemented in Java?**

    * Use `private` fields + `public` getters/setters.

3. **Difference between encapsulation and abstraction?**

    * Encapsulation = *hiding data implementation*.
    * Abstraction = *hiding implementation details of methods/behavior*.

4. **Why is encapsulation important?**

    * Improves security, reduces coupling, makes classes maintainable.

5. **What is an immutable class?**

    * A class whose state cannot change after creation (fields are `private final`, no setters).

---

## 6. Real-World Usage

* Encapsulation is used in **Java libraries** everywhere.
  Example: `ArrayList` hides its internal array — you access it through methods (`add()`, `remove()`, `get()`).
* In enterprise apps:

    * Entities (like `User`, `Account`, `Transaction`) are encapsulated.
    * Prevents direct tampering with sensitive fields (like balance, password).

---

## 7. Suggested Mini-Project Task

Extend the **Library System (from Module 1)**:

* Encapsulate `Book` fields (`title`, `author`, `availableCopies`) as private.
* Add validation in setters (copies cannot be negative).
* Add method `isAvailable()` → returns `true` if copies > 0.
