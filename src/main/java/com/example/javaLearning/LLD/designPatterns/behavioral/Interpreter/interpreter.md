# 🧠 Interpreter Pattern — Java Module (concise for learning & interview prep)

---

## 🔹 **Definition**

The **Interpreter Pattern** defines a way to **evaluate language grammar or expressions** by representing them as objects and interpreting them.

> 🧩 *In short: it’s about building a small language interpreter (like for math, rules, or filters).*

---

## 🔹 **Purpose**

* Define a **grammar** for a simple language.
* Create a **class for each grammar rule**.
* Evaluate or interpret expressions dynamically at runtime.

---

## 🔹 **When to use**

✅ When you need to **evaluate expressions or rules** (math, logical, filters).
✅ When grammar is simple and not frequently changing.
✅ When you want a flexible, extensible way to parse and evaluate logic.

---

## 🔹 **Structure**

| Role                       | Description                                            |
| -------------------------- | ------------------------------------------------------ |
| **Expression (interface)** | Declares `interpret(context)` method.                  |
| **TerminalExpression**     | Represents simple, atomic rules.                       |
| **NonTerminalExpression**  | Represents composite expressions combining others.     |
| **Context**                | Stores global info or data used during interpretation. |

---

## 🔹 **Example (Java)**

👉 A simple interpreter that checks if a given string matches certain rules.

```java
package com.example.patterns.interpreter;

// Expression interface
interface Expression {
    boolean interpret(String context);
}

// Terminal Expression
class TerminalExpression implements Expression {
    private String data;
    public TerminalExpression(String data) {
        this.data = data;
    }

    public boolean interpret(String context) {
        return context.contains(data);
    }
}

// Non-terminal Expressions
class OrExpression implements Expression {
    private Expression expr1;
    private Expression expr2;

    public OrExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public boolean interpret(String context) {
        return expr1.interpret(context) || expr2.interpret(context);
    }
}

class AndExpression implements Expression {
    private Expression expr1;
    private Expression expr2;

    public AndExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public boolean interpret(String context) {
        return expr1.interpret(context) && expr2.interpret(context);
    }
}

// Client
public class InterpreterDemo {
    public static void main(String[] args) {
        Expression java = new TerminalExpression("Java");
        Expression spring = new TerminalExpression("Spring");

        Expression javaOrSpring = new OrExpression(java, spring);
        Expression javaAndSpring = new AndExpression(java, spring);

        System.out.println("Does text contain Java or Spring? " +
            javaOrSpring.interpret("Java Developer")); // true

        System.out.println("Does text contain Java and Spring? " +
            javaAndSpring.interpret("Java Spring Project")); // true
    }
}
```

---

## 🔹 **Output**

```
Does text contain Java or Spring? true
Does text contain Java and Spring? true
```

---

## 🔹 **Real-world examples**

* Expression evaluators (e.g., math, rules engine).
* SQL query parsers.
* Regular expressions (conceptually).
* Spring Expression Language (SpEL).

---

## 🔹 **Advantages**

✅ Simple way to evaluate structured expressions.
✅ Easy to extend grammar (add new expression classes).

---

## 🔹 **Disadvantages**

❌ Complex grammars require many classes (can explode in number).
❌ Not efficient for large, complex languages (use parser libraries instead).

---

## 🔹 **Interview Q&A**

1. **What is the Interpreter pattern used for?**
   → To interpret structured grammar or expressions.
2. **How is it implemented?**
   → Each grammar rule is a class with an `interpret()` method.
3. **Example in Java?**
   → `java.util.regex.Pattern` and Spring Expression Language (SpEL).
4. **When not to use it?**
   → For complex languages; better use parser generators (ANTLR, etc.).

---

## 🔹 **In short**

> **Interpreter = “Represent and evaluate rules or expressions using objects.”**

---

Would you like the **next module — Memento Pattern** (used for undo/restore functionality)?
