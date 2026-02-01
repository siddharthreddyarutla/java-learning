## 📌 Design Patterns Used in Your Code (Reference Table)

| # | Design Pattern                              | Where in Your Code                             | What Problem It Solves                  | How You Used It                                                      |
| - | ------------------------------------------- | ---------------------------------------------- | --------------------------------------- | -------------------------------------------------------------------- |
| 1 | **Strategy**                                | `SpotSelectorStrategy` → `RandomSpotSelector`  | Different ways to choose a parking spot | Encapsulated spot-selection logic and injected it into `SpotManager` |
| 2 | **Strategy**                                | `Pricing` → `FixedPricing` → `PricingStrategy` | Different pricing calculations          | Pricing logic is interchangeable and used by `ExitGate`              |
| 3 | **Facade**                                  | `ParkingLot`                                   | Simplify client interaction             | Exposes `vehicleArrives()` and `vehicleExits()` hiding internal flow |
| 4 | **Template Method (implicit)**              | `SpotManager` (abstract class)                 | Define required parking operations      | Subclasses implement `park`, `unPark`, `hasFreeSpace`                |
| 5 | **Composition over Inheritance**            | `SpotManager` has `SpotSelectorStrategy`       | Avoid rigid inheritance                 | Behavior injected instead of subclassing                             |
| 6 | **Composition over Inheritance**            | `ExitGate` has `PricingStrategy`               | Decouple pricing logic                  | Pricing logic varies without modifying `ExitGate`                    |
| 7 | **Factory Method (attempted / planned)**    | `SpotManagerFactory` (commented usage)         | Centralized creation of SpotManagers    | Identified creation complexity but not fully implemented             |
| 8 | **Concurrency Control (not a GoF pattern)** | `ReentrantLock` in `TwoWheelerSpotManager`     | Prevent race conditions                 | Ensures thread-safe parking and unparking                            |

---

## ❌ Patterns Explicitly **Not** Used (for clarity)

| Pattern                 | Status in Your Code |
| ----------------------- | ------------------- |
| Singleton               | ❌ Not used          |
| Builder                 | ❌ Not used          |
| Observer                | ❌ Not used          |
| Decorator               | ❌ Not used          |
| Command                 | ❌ Not used          |
| Chain of Responsibility | ❌ Not used          |

---

## 🎯 How to Use This Table

* **Rows 1–4** → Core design patterns (interview-relevant)
* **Row 7** → Improvement opportunity (factory)
* **Row 8** → Engineering concern (thread safety, not a pattern)

If you want next, I can:

* ✅ Add a **“should be used” vs “used”** column
* 🔁 Map **violations of SOLID** to rows
* 🎤 Convert this into a **spoken interview answer**

