# Records

# 1️⃣ Why Java introduced records (the real problem)

Before records, for a simple data carrier you had to write:

```java
public class BreakConfigDTO {
    private final Long id;
    private final String type;
    private final LocalTime start;
    private final LocalTime end;

    public BreakConfigDTO(Long id, String type, LocalTime start, LocalTime end) {
        this.id = id;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    // getters
    // equals
    // hashCode
    // toString
}
```

### Problems:

* Too much boilerplate
* Error-prone (`equals`, `hashCode`)
* Hard to see **intent** (“this is just data”)

---

# 2️⃣ What is a record?

A **record** is a **special kind of class** that represents **immutable data**.

```java
public record BreakConfigRecord(
    Long id,
    String breakTypeConfigId,
    LocalTime startTime,
    LocalTime endTime
) {}
```

### This single line automatically gives you:

✔ private final fields
✔ constructor
✔ getters
✔ `equals()`
✔ `hashCode()`
✔ `toString()`

---

# 3️⃣ What Java actually generates (important)

This record:

```java
public record A(int x, int y) {}
```

Is roughly equivalent to:

```java
public final class A {
    private final int x;
    private final int y;

    public A(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    public boolean equals(Object o) { ... }
    public int hashCode() { ... }
    public String toString() { ... }
}
```

> ⚠️ Records are **final** — you cannot extend them.

---

# 4️⃣ Core characteristics of records (must remember)

## 1️⃣ Immutable by design

* All fields are `final`
* No setters
* State cannot change after creation

```java
record User(Long id, String name) {}

User u = new User(1L, "John");
// u.name = "Alex"; ❌ not allowed
```

---

## 2️⃣ State = constructor parameters

In a record:

* The **constructor parameters define the state**
* You cannot have “hidden” fields

```java
record Shift(Long id, String name) {}
```

This is illegal:

```java
record Shift(Long id, String name) {
    private int count; // ❌ not allowed
}
```

---

## 3️⃣ Value-based semantics

Records represent **values**, not identities.

```java
record Point(int x, int y) {}

new Point(1,2).equals(new Point(1,2)) // true
```

This is intentional.

---

# 5️⃣ Constructors in records (very important)

## 5.1 Canonical constructor

The constructor with **all components**:

```java
public record BreakConfigRecord(
    Long id,
    String type,
    LocalTime start,
    LocalTime end
) {
    public BreakConfigRecord(Long id, String type,
                             LocalTime start, LocalTime end) {
        this.id = id;   // ❌ NOT allowed
    }
}
```

❌ Wrong because records control field assignment.

---

## 5.2 Compact canonical constructor (best way)

```java
public record BreakConfigRecord(
    Long id,
    String type,
    LocalTime start,
    LocalTime end
) {
    public BreakConfigRecord {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid break time");
        }
    }
}
```

✔ Validation
✔ Defaulting
✔ Clean

---

## 5.3 Non-canonical constructors (overloads)

Allowed **only if they delegate**

```java
public record BreakConfigRecord(
    Long id,
    String type,
    LocalTime start,
    LocalTime end
) {
    public BreakConfigRecord(String type, LocalTime start, LocalTime end) {
        this(null, type, start, end); // delegation required
    }
}
```

---

# 6️⃣ Methods in records

Yes, records **can have methods**.

```java
public record BreakConfigRecord(
    LocalTime start,
    LocalTime end
) {
    public long durationMinutes() {
        return Duration.between(start, end).toMinutes();
    }
}
```

---

# 7️⃣ What records are NOT

❌ Not entities
❌ Not mutable models
❌ Not inheritance-friendly
❌ Not for business logic

```java
@Entity
record UserEntity(...) {} // ❌ very bad idea
```

---

# 8️⃣ Records vs Classes (clear comparison)

| Feature           | Class           | Record        |
| ----------------- | --------------- | ------------- |
| Boilerplate       | High            | Very low      |
| Mutability        | Mutable         | Immutable     |
| Inheritance       | Yes             | ❌ No          |
| Use as JPA entity | Yes             | ❌ No          |
| Equality          | Manual          | Automatic     |
| Intent            | Behavior + data | **Data only** |

---

# 9️⃣ When SHOULD you use records (your project context)

Given your codebase (Shift, BreakConfig, Kafka, APIs):

### ✅ Perfect use cases

* API request/response models
* DTOs between services
* Kafka payloads
* Projections
* Read-only data

Example:

```java
public record BreakConfigRecord(
    Long id,
    String breakTypeConfigId,
    BreakCategory breakCategory,
    LocalTime startTime,
    LocalTime endTime,
    Integer margin
) {}
```

---

### ❌ Avoid records for

* JPA entities
* Objects with lifecycle
* Objects with setters
* Hibernate-managed classes

---

# 🔟 Records + JPA + Spring (best practice)

✔ Entity → **class**
✔ API / DTO → **record**

```java
// Entity
class BreakConfig { ... }

// DTO
record BreakConfigDTO(...) {}
```

Mapping:

```java
BreakConfigDTO dto = new BreakConfigDTO(
    entity.getId(),
    entity.getBreakTypeConfigId(),
    entity.getBreakCategory(),
    entity.getStartTime(),
    entity.getEndTime(),
    entity.getMargin()
);
```

---

# 1️⃣1️⃣ Why Java enforces these rules so strictly

Java wants records to be:

* Simple
* Predictable
* Thread-safe
* Transparent

No hidden state. No side effects. No surprises.

---

# 1️⃣2️⃣ TL;DR (save this)

✔ Records = immutable data carriers
✔ State defined by constructor
✔ No setters, no inheritance
✔ Non-canonical constructors must delegate
✔ Perfect for DTOs, not entities

---

If you want next, I can:

* Refactor one of your DTOs into a **record**
* Show **record + Jackson + Spring** serialization tricks
* Explain **record performance vs Lombok**
* Show **Kafka payload design with records**

Just tell me 👍
