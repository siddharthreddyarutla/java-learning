# 🧭 **Memento Pattern — Java Module (for development + interview prep)**

---

## 🔹 **Definition**

The **Memento Pattern** lets you **save and restore an object’s internal state** without exposing its internal details.

> 🧩 Think of it like an **“undo” feature** in editors or games.

---

## 🔹 **Purpose**

* Capture an object’s state (snapshot).
* Restore it later without violating encapsulation.
* Used for **undo**, **rollback**, or **version history** mechanisms.

---

## 🔹 **Structure**

| Role           | Description                                                               |
| -------------- | ------------------------------------------------------------------------- |
| **Originator** | The object whose state we want to save or restore.                        |
| **Memento**    | Stores the internal state (snapshot).                                     |
| **Caretaker**  | Manages mementos — saves and restores them but doesn’t modify their data. |

---

## 🔹 **Example (Java)**

👉 A simple text editor with undo functionality.

```java
package com.example.patterns.memento;

import java.util.Stack;

// Memento: stores the state
class TextMemento {
    private final String text;

    public TextMemento(String text) {
        this.text = text;
    }

    public String getSavedText() {
        return text;
    }
}

// Originator: text editor
class TextEditor {
    private String text = "";

    public void write(String newText) {
        text += newText;
    }

    public TextMemento save() {
        return new TextMemento(text); // create snapshot
    }

    public void restore(TextMemento memento) {
        text = memento.getSavedText(); // revert to saved state
    }

    public String getText() {
        return text;
    }
}

// Caretaker: manages snapshots
class History {
    private final Stack<TextMemento> history = new Stack<>();

    public void save(TextMemento memento) {
        history.push(memento);
    }

    public TextMemento undo() {
        if (!history.isEmpty()) {
            return history.pop();
        }
        return null;
    }
}

// Client
public class MementoDemo {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        History history = new History();

        editor.write("Hello ");
        history.save(editor.save()); // save state 1

        editor.write("World!");
        history.save(editor.save()); // save state 2

        System.out.println("Current text: " + editor.getText());

        // Undo (restore previous state)
        editor.restore(history.undo());
        System.out.println("After undo: " + editor.getText());

        // Undo again
        editor.restore(history.undo());
        System.out.println("After second undo: " + editor.getText());
    }
}
```

---

## 🔹 **Output**

```
Current text: Hello World!
After undo: Hello 
After second undo: 
```

---

## 🔹 **Key Points**

* Memento stores state **privately** (no external access).
* Originator creates & restores mementos.
* Caretaker only manages them — it **never changes** their contents.
* Enables **undo / redo**, **rollback**, **version control**, etc.

---

## 🔹 **Real-world uses**

* Text editors (undo/redo).
* Database transactions (rollback).
* Games (save checkpoints).
* IDEs (restore last configuration).

---

## 🔹 **Advantages**

✅ Preserves encapsulation (no direct access to internal state).
✅ Simple to implement for small state objects.
✅ Enables undo/redo, rollback logic cleanly.

---

## 🔹 **Disadvantages**

❌ Consumes memory for many snapshots.
❌ Hard to maintain if object’s state is large or complex.
❌ Doesn’t track *what* changed — only saves entire state.

---

## 🔹 **Interview Q&A**

1. **What does Memento pattern do?**
   → Captures and restores object state without exposing its internals.
2. **Which design principle it follows?**
   → Encapsulation — internal state stays hidden from outside.
3. **Where is it used?**
   → Undo/Redo systems, checkpoints, transactions.
4. **Who creates and uses the Memento?**
   → Originator creates it; Caretaker stores it.
5. **When is it not suitable?**
   → When state objects are huge (memory overhead).

---

## 🔹 **In short**

> 🧠 **Memento = “Save now, restore later — without revealing secrets.”**

---