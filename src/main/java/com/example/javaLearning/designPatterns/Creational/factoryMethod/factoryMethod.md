# Module 2 — Factory Method (Creational)

## 1) Definition & Intent

> Factory Method is a creational design pattern that provides an interface for creating objects in a superclass, but allows subclasses to alter the type of objects that will be created.

**Factory Method** defines an interface for creating an object, but lets subclasses decide which class to instantiate.
It **defers object creation** to subclasses (or implementations) so the client depends on an abstraction and not on concrete classes.

**Intent:** decouple object creation from use, enable adding new product types without changing client code.

---

## 2) Problem it solves (when to use)

> Imagine that you’re creating a logistics management application. The first version of your app can only handle transportation by trucks, so the bulk of your code lives inside the Truck class.

> Great news, right? But how about the code? At present, most of your code is coupled to the Truck class. Adding Ships into the app would require making changes to the entire codebase. Moreover, if later you decide to add another type of transportation to the app, you will probably need to make all of these changes again.

As a result, you will end up with pretty nasty code, riddled with conditionals that switch the app’s behavior depending on the class of transportation objects.

After a while, your app becomes pretty popular. Each day you receive dozens of requests from sea transportation companies to incorporate sea logistics into the app.

* When a class can’t anticipate the exact types of objects it must create.
* When you want to localize knowledge of which concrete class to use.
* When you want to let subclasses provide different implementations of an object used by a superclass.
* Useful for frameworks or libraries that rely on user-provided implementations.

---

## 3) ❌ Bad example (no factory — client instantiates concretes)

```java
// Product hierarchy
interface Notification {
    void send(String message);
}

class EmailNotification implements Notification {
    @Override
    public void send(String message) { System.out.println("Email: " + message); }
}

class SMSNotification implements Notification {
    @Override
    public void send(String message) { System.out.println("SMS: " + message); }
}

// Client uses concrete classes directly
public class NotificationService {
    public void notifyUser(String userPref, String msg) {
        if ("EMAIL".equals(userPref)) {
            EmailNotification n = new EmailNotification(); // direct instantiation
            n.send(msg);
        } else {
            SMSNotification n = new SMSNotification();
            n.send(msg);
        }
    }
}
```

**Problems:**

* Client (`NotificationService`) is coupled to concrete classes.
* Adding a new notification type requires editing `NotificationService`.
* Violates OCP and DIP.

---

## 4) ✅ Good example (Factory Method applied)

```java
// Product
public interface Notification {
    void send(String message);
}

public class EmailNotification implements Notification {
    @Override public void send(String message) { System.out.println("Email: " + message); }
}

public class SMSNotification implements Notification {
    @Override public void send(String message) { System.out.println("SMS: " + message); }
}

// Creator (factory method declared)
public abstract class NotificationFactory {
    // Factory method: subclasses decide product instantiation
    protected abstract Notification createNotification();

    // Business logic uses product via abstraction
    public void notify(String message) {
        Notification notification = createNotification();
        notification.send(message);
    }
}

// Concrete creators
public class EmailNotificationFactory extends NotificationFactory {
    @Override protected Notification createNotification() {
        return new EmailNotification();
    }
}

public class SMSNotificationFactory extends NotificationFactory {
    @Override protected Notification createNotification() {
        return new SMSNotification();
    }
}

// Client code
public class App {
    public static void main(String[] args) {
        NotificationFactory factory = getFactoryFromConfig("EMAIL");
        factory.notify("Hello world");
    }

    private static NotificationFactory getFactoryFromConfig(String pref) {
        if ("EMAIL".equals(pref)) return new EmailNotificationFactory();
        return new SMSNotificationFactory();
    }
}
```

**Why this is better:**

* `App` and `NotificationFactory` depend on `Notification` abstraction.
* To add a new notification type, add a new `Notification` and `NotificationFactory` subclass — you don’t modify existing factories or `notify` logic.
* Creator encapsulates product creation.

---

## 5) Variations & related patterns

* **Parameterised factory method:** factory chooses product based on parameters instead of subclasses.
* **Factory Method vs Abstract Factory:** Factory Method creates one family product via inheritance. Abstract Factory returns a family of related products via composition (use both when appropriate).
* **Factory Method + Template Method:** Creator provides the algorithm while factory method supplies product objects.

---

## 6) Real-world analogy

A **restaurant**: the menu (creator) defines the process for serving dishes, but the chef (concrete creator) decides which exact recipe (product) to prepare. If you change the chef, the process remains the same but the created dishes can change.

---

## 7) When NOT to use / pitfalls

> Use the Factory Method when you don’t know beforehand the exact types and dependencies of the objects your code should work with.

> The Factory Method separates product construction code from the code that actually uses the product. Therefore it’s easier to extend the product construction code independently from the rest of the code.

For example, to add a new product type to the app, you’ll only need to create a new creator subclass and override the factory method in it.

* Overuse leads to many small factory classes (boilerplate). Consider simple factory or dependency injection if that fits.
* If creation logic is simple and stable, direct construction is clearer. Don’t prematurely abstract.
* Avoid huge inheritance hierarchies solely for factories — prefer composition/registration-based factories when flexibility needed.

---

## 8) Testability notes

* Factory Method improves testability: tests can provide a test factory that returns mocks/fakes of `Notification`.
* You can unit-test product behavior separately and factory logic separately.
* If using many concrete factory subclasses, prefer wiring via DI (Spring) so tests can inject mocks easily.

---

## 9) Exercises (try in your IDE)

1. **Simple exercise:** Convert the `PaymentFactory` you implemented earlier into a Factory Method style: create an abstract `PaymentFactory` and concrete factories for `UPI`, `CreditCard`, etc. Show usage from client without `switch`.
2. **Variant:** Implement a `LoggerFactory` that creates `FileLogger` or `ConsoleLogger` based on runtime config. Add a `setLogLevel()` method in the factory to demonstrate extra configuration before creating the product.
3. **Test task:** Write a unit test where a test factory returns a mock `Notification` to `NotificationFactory.notify()` and verify `send()` was called.

---

## 10) Common interview questions (Factory Method)

1. What is the Factory Method pattern and when would you use it?
2. How does Factory Method support OCP and DIP?
3. How is Factory Method different from Abstract Factory? Give an example.
4. Are there downsides to Factory Method? What are alternatives?
5. How do you unit test code that uses Factory Method?

---

## 11) Quick implementation alternatives (practical notes)

* For simple selection by type, a **registration-based factory** or map (key → supplier) reduces the number of concrete factory classes.
  * In frameworks (Spring), prefer **injection of strategy beans** (e.g., `Map<String, Notification>` autowired) over manual factory hierarchies for better runtime flexibility.

> here are three clear implementations of the same idea (create a Transport and use it to deliver) so you can compare patterns side-by-side:
1. A — Factory Method (your original, classic pattern: one concrete factory per product)
2. B — Registration-based Factory (map of suppliers; less boilerplate)
3. C — Spring + Map injection (DI framework approach — add a new implementation by just adding a bean)

## 12)  Pros and Cons

### Pros:
1. You avoid tight coupling between the creator and the concrete products.
2.  Single Responsibility Principle. You can move the product creation code into one place in the program, making the code easier to support.
3.  Open/Closed Principle. You can introduce new types of products into the program without breaking existing client code.

### cons:

1.  The code may become more complicated since you need to introduce a lot of new subclasses to implement the pattern. The best case scenario is when you’re introducing the pattern into an existing hierarchy of creator classes.