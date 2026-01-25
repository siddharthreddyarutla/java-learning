### 🧠 **Iterator Pattern — Short & Simple Explanation**

**Definition:**
Iterator pattern lets you **traverse elements of a collection one by one** **without exposing** how that collection is implemented.

**Purpose:**
➡️ Provides a standard way to loop through collections (like lists, sets, etc.)
➡️ Keeps traversal logic separate from the collection’s internal structure.

**Example (Java):**

```java
import java.util.*;

public class IteratorExample {
    public static void main(String[] args) {
        List<String> fruits = List.of("Apple", "Banana", "Cherry");

        Iterator<String> it = fruits.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}
```

**In short:**

> 🌀 **Iterator = "Get next element without knowing how data is stored."**
