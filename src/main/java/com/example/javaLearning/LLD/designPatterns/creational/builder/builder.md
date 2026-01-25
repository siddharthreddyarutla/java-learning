# Module 4 — Builder Pattern

---

## 1) Definition & Intent

The **Builder Pattern** separates the construction of a complex object from its representation, so that the same construction process can create different representations.

👉 In simple terms:

* When you have a class with **lots of fields** (some required, some optional), instead of telescoping constructors or tons of setters, you use a **builder** to construct the object step by step.

---

## 2) Problem it solves

* **Telescoping constructor problem**:

  ```java
  new User("John", "Doe", 25, "john@gmail.com", "1234567890", "Engineer", true, "India");
  ```

  Hard to read, hard to maintain.

* **JavaBean with setters**:

  ```java
  User u = new User();
  u.setFirstName("John");
  u.setLastName("Doe");
  ...
  ```

  Object can be in an **inconsistent abstractState** until all setters are called.

---

## 3) ✅ Builder Pattern

```java
public class User {
    // Required parameters
    private final String firstName;
    private final String lastName;

    // Optional parameters
    private final int age;
    private final String email;
    private final String phone;
    private final String occupation;

    // Private constructor (only BuilderPattern can create)
    private User(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.email = builder.email;
        this.phone = builder.phone;
        this.occupation = builder.occupation;
    }

    // BuilderPattern static inner class
    public static class Builder {
        private final String firstName;
        private final String lastName;

        private int age;
        private String email;
        private String phone;
        private String occupation;

        public Builder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Builder age(int age) {
            this.age = age;
            return this; // chaining
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder occupation(String occupation) {
            this.occupation = occupation;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public String toString() {
        return "User [firstName=" + firstName + ", lastName=" + lastName +
               ", age=" + age + ", email=" + email + ", phone=" + phone +
               ", occupation=" + occupation + "]";
    }
}
```

### Client code

```java
public class Demo {
    public static void main(String[] args) {
        User user = new User.Builder("John", "Doe")
                .age(25)
                .email("john@gmail.com")
                .occupation("Engineer")
                .build();

        System.out.println(user);
    }
}
```

---

## 4) Real-world analogy

Think of ordering a **pizza**:

* You always need a **base** (mandatory fields).
* You can choose toppings (optional fields).
* A **Pizza Builder** allows you to specify step by step what to include.
* At the end, you call `build()` and get your pizza.

---

## 5) When to use

* When a class has **many optional parameters**.
* When object construction involves **complex steps** (e.g., parsing, validation, assembling sub-objects).
* To make the code more **readable and maintainable**.

---

## 6) Interview questions

1. What problem does the Builder Pattern solve?
2. Compare Builder vs Factory Method.
3. What are advantages of Builder over telescoping constructors?
4. In which scenarios would you prefer Builder over simple setters?
5. How does the Builder Pattern improve immutability?

---

## 7) Exercises for you

1. Create a `Computer` class with required fields: `CPU`, `RAM`; optional fields: `GPU`, `storage`, `wifiEnabled`. Implement Builder pattern.
2. Modify the `Report` example we discussed earlier to build a complex `ReportConfig` object using Builder.
3. Implement a **Fluent API** style builder where you chain multiple calls.