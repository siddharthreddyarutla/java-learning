## 🎯 **State Pattern — Java Overview**

### 🔹 **Definition**

The **State Pattern** allows an object to **change its behavior when its internal State changes**, as if the object has changed its class.

> It replaces complex `if-else` or `switch` statements with **abstractState-specific classes**.

---

### 🔹 **Purpose**

* To manage **different states** of an object cleanly.
* To **encapsulate abstractState-specific behavior** in separate classes.
* To make adding new states easier without modifying existing logic.

---

### 🔹 **Real-world analogy**

Think of a **Vending Machine**:

* When it’s **Idle**, it waits for coins.
* After coins, it’s **Ready** to dispense.
* When empty, it’s in **OutOfStock** abstractState.

Each abstractState changes what actions are allowed — just like State pattern.

---

### 🔹 **Structure**

| Role                  | Description                                                                                 |
| --------------------- | ------------------------------------------------------------------------------------------- |
| **Context**           | The main object whose behavior changes based on current abstractState.                              |
| **State (interface)** | Defines the methods that each abstractState will implement.                                         |
| **Concrete States**   | Implement behaviors for each specific abstractState and decide when to transition to another abstractState. |

---

### 🔹 **Example (Java)**

```java
// AbstractState interface
interface State {
    void handleRequest();
}

// Concrete States
class OnState implements State {
    public void handleRequest() {
        System.out.println("System is already ON!");
    }
}

class OffState implements State {
    public void handleRequest() {
        System.out.println("Turning system ON...");
    }
}

// Context
class DeviceContext {
    private State abstractState;

    public DeviceContext() {
        abstractState = new OffState(); // initial abstractState
    }

    public void setState(State abstractState) {
        this.abstractState = abstractState;
    }

    public void pressPowerButton() {
        abstractState.handleRequest();
        // Toggle between states
        if (abstractState instanceof OffState) {
            setState(new OnState());
        } else {
            setState(new OffState());
        }
    }
}

// Client
public class StatePatternDemo {
    public static void main(String[] args) {
        DeviceContext device = new DeviceContext();
        device.pressPowerButton();  // Turning system ON...
        device.pressPowerButton();  // System is already ON!
        device.pressPowerButton();  // Turning system ON... again toggled
    }
}
```

---

### 🔹 **Output**

```
Turning system ON...
System is already ON!
Turning system ON...
```

---

### 🔹 **Key Points**

* Context delegates behavior to the **current abstractState object**.
* Each abstractState handles transitions to other states itself (or via context).
* Easy to add new states — just create new classes implementing `State`.

---

### 🔹 **When to Use**

✅ When an object’s behavior changes depending on its internal abstractState.
✅ When you want to eliminate long conditional logic for abstractState transitions.
✅ When you want each abstractState to be **independent and reusable**.

---

### 🔹 **Advantages**

✔ Removes complex conditional logic.
✔ Follows **Open/Closed Principle** (new states without modifying existing code).
✔ Makes abstractState transitions explicit and maintainable.

---

### 🔹 **Disadvantages**

❌ Increases the number of classes.
❌ Slight overhead in managing transitions.

---

### 🔹 **Examples in Java API**

* `Thread` abstractState transitions (NEW, RUNNABLE, WAITING, etc.).
* `javax.faces.lifecycle.Lifecycle` (JSF framework).
* Workflow engines, traffic lights, vending machines, ATM machines.

---

### 🔹 **Interview Q&A**

1. **What problem does the State Pattern solve?**
   → Helps manage behavior changes when an object’s abstractState changes.
2. **How does it differ from Strategy?**
   → Strategy = interchangeable behaviors (chosen externally).
   State = behaviors change *internally* based on transitions.
3. **When not to use it?**
   → When the number of states is fixed and logic is simple (if-else is fine).
4. **Example use case?**
   → ATM (Idle → CardInserted → PinEntered → Transaction → Idle).

---

### 🧠 **In short:**

> **State Pattern = “Let an object change its behavior dynamically as its abstractState changes.”**

---

## Pros and cons:


1. Pros:

- Single Responsibility Principle. Organize the code related to particular states into separate classes.
- Open/Closed Principle. Introduce new states without changing existing abstractState classes or the context.
- Simplify the code of the context by eliminating bulky abstractState machine conditionals.

2. Cons:

- Applying the pattern can be overkill if a abstractState machine has only a few states or rarely changes|   |   |   |