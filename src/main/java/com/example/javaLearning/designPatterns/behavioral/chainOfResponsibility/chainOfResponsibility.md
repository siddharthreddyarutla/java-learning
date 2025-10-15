# Chain of Responsibility Pattern — Java module (concise for development + interview prep)

---

## 🔹 **Definition**

The **Chain of Responsibility (CoR)** pattern lets you **pass a request along a chain of handlers**, where each handler decides either to **process** the request or **pass it to the next** handler in the chain.

> 🧩 *Think of it like a customer support escalation system — if one level can’t handle it, it passes to the next.*

---

## 🔹 **Purpose**

* Decouple senders and receivers of a request.
* Simplify request handling by organizing logic in sequential “links.”
* Avoid massive `if-else` or `switch` blocks.

---

## 🔹 **Structure**

| Role                   | Description                                                    |
| ---------------------- | -------------------------------------------------------------- |
| **Handler (abstract)** | Declares a method to process request and link to next handler. |
| **ConcreteHandler**    | Processes request or forwards to next handler.                 |
| **Client**             | Starts the request through the chain.                          |

---

## 🔹 **Example (Java)**

```java
package com.example.patterns.chainofresponsibility;

abstract class Handler {
    protected Handler next;

    public Handler setNext(Handler next) {
        this.next = next;
        return next; // for chaining
    }

    public abstract void handle(String request);
}

// Concrete handlers
class InfoLogger extends Handler {
    public void handle(String request) {
        if (request.equals("INFO")) {
            System.out.println("INFO: Logging info message");
        } else if (next != null) {
            next.handle(request);
        }
    }
}

class DebugLogger extends Handler {
    public void handle(String request) {
        if (request.equals("DEBUG")) {
            System.out.println("DEBUG: Logging debug message");
        } else if (next != null) {
            next.handle(request);
        }
    }
}

class ErrorLogger extends Handler {
    public void handle(String request) {
        if (request.equals("ERROR")) {
            System.out.println("ERROR: Logging error message");
        } else if (next != null) {
            next.handle(request);
        } else {
            System.out.println("No handler found for: " + request);
        }
    }
}

// Client
public class ChainDemo {
    public static void main(String[] args) {
        Handler info = new InfoLogger();
        Handler debug = new DebugLogger();
        Handler error = new ErrorLogger();

        info.setNext(debug).setNext(error);

        info.handle("INFO");
        info.handle("DEBUG");
        info.handle("ERROR");
        info.handle("TRACE");
    }
}
```

---

## 🔹 **Output**

```
INFO: Logging info message
DEBUG: Logging debug message
ERROR: Logging error message
No handler found for: TRACE
```

---

## 🔹 **Key Points**

* Each handler does its job or passes to the next.
* You can easily **add/remove** handlers without breaking others.
* The chain can be built dynamically at runtime.

---

## 🔹 **Real-world examples**

* Servlet filters in Java EE / Spring Boot.
* Exception handling (`try-catch-finally` sequence).
* Middleware pipelines (logging, authentication, authorization).

---

## 🔹 **Advantages**

✅ Reduces coupling between sender and receiver.
✅ Easy to extend — just add a new handler.
✅ Promotes single responsibility per handler.

---

## 🔹 **Disadvantages**

❌ May be hard to debug — request flow is not always obvious.
❌ No guarantee that the request will be handled (if no handler accepts it).

---

## 🔹 **Interview Q&A**

1. **What is the Chain of Responsibility pattern?**
   → Passes a request through a chain of handlers until one handles it.
2. **When would you use it?**
   → When multiple objects might handle a request, and the handler isn’t known in advance.
3. **Difference between Chain of Responsibility and Decorator?**
   → CoR passes a request; Decorator wraps and enhances functionality.
4. **Give an example in Java framework.**
   → Servlet Filter chain or Spring’s `HandlerInterceptor` chain.

---

## 🔹 **In short**

> **CoR = “Pass it along the chain until someone handles it.”**