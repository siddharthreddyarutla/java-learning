#1 What SOLID is (short)

**SOLID** = five object-oriented design principles that help your code be:

* **S**ingle Responsibility Principle (SRP)
* **O**pen/Closed Principle (OCP)
* **L**iskov Substitution Principle (LSP)
* **I**nterface Segregation Principle (ISP)
* **D**ependency Inversion Principle (DIP)

They aim to make systems easier to **understand, change, extend, test, and maintain**.

---

# #2 Why SOLID is required — practical reasons

1. **Maintainability**
   Small, well-focused classes are easier to read and change. When a bug appears, you know where to look.

2. **Extensibility**
   Add features without touching (and risking breaking) existing working code.

3. **Testability**
   Smaller responsibilities + clear abstractions = easier unit tests and mocks.

4. **Loose coupling & clear contracts**
   Less interdependence between components; you can swap implementations easily.

5. **Team scalability**
   Clear boundaries let multiple developers work independently with fewer merge conflicts.

6. **Lower risk during refactor**
   If every class has a single reason to change, refactoring has lower blast radius.

---

# #3 When to use SOLID — practical guidance & tradeoffs

* **Apply early for core modules** (domain logic, APIs, services).
* **Be pragmatic**: SOLID is a guide, not a religion. Over-applying leads to **over-abstraction** (too many tiny interfaces, excessive indirection).
* **Follow YAGNI**: Don’t add interfaces “just in case” — add them when you actually need to vary behavior or test.
* **Iterate**: Start simple; refactor toward SOLID when code smells appear or requirements change.

---

# #4 Common code smells that indicate SOLID violations

* Huge classes (God objects).
* Methods with many unrelated steps (logging + DB + formatting + networking).
* Long `switch` / `if-else` chains on types.
* Classes hard to unit test (lots of concrete dependencies).
* Interfaces with many methods (clients forced to implement unused methods).
* Subclasses that throw `UnsupportedOperationException`.

---

# #5 Quick refactoring checklist (practice)

When you see messy code, ask:

* Does this class do more than one thing? → **SRP**
* Do I need to change existing code to add a new feature? → **OCP**
* Can a child class truly substitute parent behavior? → **LSP**
* Does any client depend on methods it doesn’t use? → **ISP**
* Do high-level modules depend on low-level concretes? → **DIP**

If you answer “yes” to any, consider refactor patterns: extract class, extract interface, dependency injection, replace conditionals with polymorphism.
