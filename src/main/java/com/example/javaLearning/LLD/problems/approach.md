# 📘 Standard Technique to Solve LLD (Low-Level Design) Problems

Use this **7-step framework** in every LLD interview or design exercise. It keeps you structured, readable, and extensible.

---

## 1️⃣ Clarify Requirements (Always Start Here)

**Goal:** Understand *what to build* before *how to build*.

### Ask / Define:

* **Functional requirements**

    * What actions must the system support?
    * Example: *park vehicle, generate ticket, calculate fee*
* **Actors**

    * Who interacts with the system?
    * Example: *User, Admin, External System*
* **Non-functional requirements**

    * Concurrency (multiple users?)
    * Scalability?
    * Extensibility (new vehicle types, new payment methods?)

📝 **Rule:**

> If requirements are unclear, your design will collapse later.

---

## 2️⃣ Identify Classes (Noun Extraction)

**Goal:** Find the core entities of the system.

### Technique:

* Read the problem statement
* **Underline all nouns**
* Convert meaningful nouns into classes

### Example (Parking Lot):

* ParkingLot
* ParkingSpot
* Vehicle
* Ticket
* Payment
* EntranceGate
* ExitGate

🚫 **Avoid**:

* Primitive nouns → attributes, not classes

    * `id`, `name`, `number`, `status`

---

## 3️⃣ Define Attributes & Behaviors (State + Actions)

**Goal:** Decide what each class *knows* and *does*.

### Template:

```text
Class: X
- Attributes (state)
- Methods (behavior)
```

### Example:

```text
Class: Vehicle
- Attributes: vehicleNumber, vehicleType
- Methods: getType()
```

```text
Class: ParkingSpot
- Attributes: spotId, spotType, isAvailable
- Methods: assignVehicle(), removeVehicle()
```

📝 **Rule:**

> If a method changes the state of another class → rethink responsibilities.

---

## 4️⃣ Establish Relationships Between Classes

**Goal:** Connect classes meaningfully and correctly.

### Relationship Types (Very Important in Interviews):

#### 🔹 Inheritance (Is-A)

* `Car` **is a** `Vehicle`
* Use when behavior is shared

#### 🔹 Composition (Strong Has-A)

* `ParkingLot` **has** `ParkingSpot`
* If parent dies → child dies

#### 🔹 Aggregation (Weak Has-A)

* `ParkingLot` **uses** `PaymentService`
* Independent lifecycles

#### 🔹 Association (Uses)

* `ExitGate` **uses** `Ticket`

📝 **Interview Tip:**

> Always justify **why** you chose composition vs aggregation.

---

## 5️⃣ Introduce Interfaces & Abstractions (Extensibility Step)

**Goal:** Make the design open for extension.

### Where to use interfaces:

* Payment methods
* Fee calculation strategies
* Notification mechanisms

### Example:

```text
Interface: Payment
- pay(amount)
```

```text
Class: CardPayment implements Payment
Class: UpiPayment implements Payment
```

📝 **Rule:**

> Code against interfaces, not implementations.

---

## 6️⃣ Apply Design Principles & Patterns (Selective)

**Goal:** Improve maintainability, not over-engineer.

### SOLID (Minimum Expected):

* **S**ingle Responsibility → One class = one job
* **O**pen/Closed → Add new behavior without modifying old code
* **D**ependency Inversion → Depend on abstractions

### Common LLD Patterns:

* **Factory** → object creation (VehicleFactory, PaymentFactory)
* **Strategy** → interchangeable logic (FeeCalculationStrategy)
* **Singleton** → shared system resource (ParkingLot, CoffeeMachine)

🚫 **Avoid:**
“Pattern dumping” — use only when it solves a problem.

---

## 7️⃣ Draw UML Class Diagram (Final Sanity Check)

**Goal:** Visualize and validate the design.

### Include:

* Classes + interfaces
* Relationships
* Multiplicity (1, 1..*, *)
* Access modifiers (`+ - #`)

### Ask Yourself:

* Is anything tightly coupled?
* Can I add a new feature easily?
* Does any class do too much?

---

## 🧠 LLD Mental Checklist (Use This in Interviews)

✔ Have I clarified requirements?
✔ Did I extract nouns correctly?
✔ Are responsibilities well-distributed?
✔ Did I choose the right relationships?
✔ Is the design extensible?
✔ Can I explain every design choice?

---

## 🧩 Example: Coffee Machine (LLD Summary)

### Classes:

* CoffeeMachine (Singleton)
* Beverage
* Ingredient
* Recipe
* Inventory

### Relationships:

* CoffeeMachine **has** Inventory (Composition)
* Beverage **uses** Recipe (Association)
* Recipe **is made of** Ingredients (Composition)

---

## 🔁 After Class Diagram → Sequence Diagram

Once LLD is done:

* Show **runtime flow**
* Focus on **one use case**
* Example: *Order Coffee → Validate → Deduct Ingredients → Dispense*

---

## 🏁 Final Interview Advice

> **LLD is not about perfection.
> It’s about clarity, structure, and reasoning.**

If you follow this **7-step technique**, you’ll:

* Never feel lost
* Sound systematic
* Impress with clean thinking
