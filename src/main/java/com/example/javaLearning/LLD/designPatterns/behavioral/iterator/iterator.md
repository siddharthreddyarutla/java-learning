# Iterator Pattern — Java module (complete, clear, step-by-step, MD notes)

---

# 1. Quick summary (what & why)

**Definition:**
The Iterator pattern provides a way to access elements of a collection sequentially **without exposing** its underlying representation.

**Purpose:**

* Decouple traversal from storage.
* Let clients iterate over different collections with a uniform interface (`Iterator` / `Iterable`).
* Support multiple traversal algorithms and encapsulate iteration abstractState.

**One-line:**

> Iterator = “walk the collection without poking inside it.”

---

# 2. Participants (roles)

* **Iterator** — defines `hasNext()` and `next()` (and optionally `remove()`).
* **ConcreteIterator** — implements Iterator for a particular aggregate.
* **Aggregate / Collection** — an interface exposing a method to create an Iterator (e.g., `Iterable` in Java).
* **ConcreteAggregate** — the concrete collection (ArrayList, custom collection).

In Java these are `java.util.Iterator<E>` and `java.lang.Iterable<E>`.

---

# 3. UML (informal)

```
Client --> Iterable --> Iterator
ConcreteAggregate --> createIterator() --> ConcreteIterator
ConcreteIterator { index, reference to collection }
```

---

# 4. Plain Java — basic usage (built-in)

```java
import java.util.List;
import java.util.ArrayList;

public class IteratorBasic {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("apple"); list.add("banana"); list.add("cherry");

        // External iteration using Iterator explicitly
        var it = list.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        // Enhanced for-loop (internal calling of iterator, external to you)
        for (String s : list) {
            System.out.println(s);
        }
    }
}
```

Notes:

* `for-each` uses `Iterable` under the hood — less boilerplate.
* `Iterator.remove()` can remove last returned element (optional operation).

---

# 5. Implementing a custom Iterator

```java
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EvenNumbers implements Iterable<Integer> {
    private final int maxExclusive;

    public EvenNumbers(int maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new EvenIterator();
    }

    private class EvenIterator implements Iterator<Integer> {
        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < maxExclusive;
        }

        @Override
        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            int val = current;
            current += 2;
            return val;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

    public static void main(String[] args) {
        for (int i : new EvenNumbers(10)) {
            System.out.println(i);
        }
    }
}
```

Key points:

* Keep iterator abstractState (index, cursor) inside the iterator, not in the collection.
* `remove()` is optional — throw `UnsupportedOperationException` if not supported.

---

# 6. Fail-fast vs Fail-safe iterators

* **Fail-fast (most standard Java collections)**: Iterator detects structural modification (concurrent structural changes) and throws `ConcurrentModificationException`. E.g., `ArrayList`’s iterator.

    * Good for catching bugs quickly.
    * Not suitable for concurrent modification by other threads.

* **Fail-safe**: Iterators operate on a *copy* of data (e.g., `CopyOnWriteArrayList`) so concurrent modifications don't throw; changes may not be reflected in the iterator.

    * Good for concurrent read-heavy scenarios.
    * Expensive if the collection is large and modifications are frequent.

Example (fail-fast detection):

```java
List<String> list = new ArrayList<>(List.of("a","b","c"));
var it = list.iterator();
list.add("d"); // structural modification
it.next();     // throws ConcurrentModificationException
```

---

# 7. Filtering, mapping, and composable iterators (decorator style)

You can wrap iterators to add behavior without changing the underlying collection.

```java
import java.util.Iterator;
import java.util.function.Predicate;

public class FilteringIterator<T> implements Iterator<T> {
    private final Iterator<T> source;
    private final Predicate<? super T> predicate;
    private T nextItem;
    private boolean nextItemReady = false;

    public FilteringIterator(Iterator<T> source, Predicate<? super T> predicate) {
        this.source = source; this.predicate = predicate;
    }

    private void prepare() {
        while (!nextItemReady && source.hasNext()) {
            T candidate = source.next();
            if (predicate.test(candidate)) {
                nextItem = candidate;
                nextItemReady = true;
                return;
            }
        }
    }

    @Override
    public boolean hasNext() {
        prepare();
        return nextItemReady;
    }

    @Override
    public T next() {
        prepare();
        if (!nextItemReady) throw new java.util.NoSuchElementException();
        nextItemReady = false;
        return nextItem;
    }
}
```

Use case: wrap any iterator with `new FilteringIterator<>(list.iterator(), x -> x.startsWith("a"));`

---

# 8. Internal vs External iteration

* **External iteration** (Iterator pattern): Client controls loop (`Iterator` + `hasNext()`/`next()`).
* **Internal iteration**: Collection controls traversal and calls a callback for each element (e.g., Java 8 `forEach`, streams `forEach`, collection’s `forEach` accepting `Consumer`).

Example internal iteration:

```java
list.forEach(item -> System.out.println(item));
list.stream().filter(...).forEach(...);
```

Internal iteration often enables parallelism (e.g., `parallelStream()`), optimization, and concise code.

---

# 9. Iterator + Streams (modern approach)

Java Streams provide powerful internal iteration, mapping, filtering, and lazy evaluation. Prefer streams for expressive transformations; use iterators when you need low-level control or incremental consumption.

Example conversion:

```java
Iterator<String> it = ...;
Iterable<String> iterable = () -> it;
StreamSupport.stream(iterable.spliterator(), false)
             .map(...)
             .filter(...)
             .forEach(...);
```

---

# 10. Performance and memory considerations

* Iterators themselves are lightweight; complexity depends on underlying collection.
* Copying for fail-safe behavior is expensive.
* `CopyOnWriteArrayList` is good when reads >> writes.
* Avoid materializing entire collections when you can lazily iterate.

---

# 11. Best practices

* Prefer `Iterable` + enhanced `for` or Streams for readability unless you need explicit control.
* Implement `remove()` only if you can do it correctly; otherwise throw `UnsupportedOperationException`.
* Keep iterator abstractState encapsulated in the iterator instance (no shared mutable abstractState).
* Document whether your iterator is fail-fast or fail-safe and thread-safety expectations.
* Use `Spliterator` if you need parallelizable iteration semantics (used by streams).

---

# 12. Practice tasks (do these to master Iterator)

* Implement a custom collection (e.g., `RingBuffer<T>`) and provide an iterator.
* Implement `FilteringIterator<T>` and `MappingIterator<T,R>` and compose them.
* Demonstrate fail-fast behavior by modifying a collection while iterating and catch `ConcurrentModificationException`.
* Implement an iterator that supports `remove()` correctly for a linked list.
* Convert an `Iterator` to a `Stream` and back.
* Implement a `ReverseIterator` that iterates an `ArrayList` from end to start.

---

# 13. Common interview questions (possible Q&A)

1. What is the Iterator pattern and why use it?
2. How does Java support the Iterator pattern? (`Iterable`, `Iterator`, `for-each`)
3. Explain fail-fast vs fail-safe iterators. Give examples.
4. How does `Iterator.remove()` work and what must you be careful about?
5. When would you use streams instead of iterators?
6. How would you implement a custom iterator for a binary tree (in-order traversal)?
7. What is the difference between internal and external iteration?
8. How can you make an iterator thread-safe? When is that necessary?
9. Explain `Spliterator` and when to use it.
10. Write code to create an iterator that only returns distinct elements (no duplicates) from another iterator.

---

# 14. Small runnable example: custom collection + reverse iterator

```java
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;

public class ReverseList<T> implements Iterable<T> {
    private final List<T> data = new ArrayList<>();

    public void add(T t) { data.add(t); }

    @Override
    public Iterator<T> iterator() {
        // default forward iterator
        return data.iterator();
    }

    public Iterator<T> reverseIterator() {
        return new Iterator<>() {
            private int cursor = data.size() - 1;
            @Override
            public boolean hasNext() { return cursor >= 0; }
            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return data.get(cursor--);
            }
        };
    }

    public static void main(String[] args) {
        ReverseList<String> rl = new ReverseList<>();
        rl.add("one"); rl.add("two"); rl.add("three");

        System.out.println("Forward:");
        for (String s : rl) System.out.println(s);

        System.out.println("Reverse:");
        var rit = rl.reverseIterator();
        while (rit.hasNext()) System.out.println(rit.next());
    }
}
```

---

# 15. Quick cheatsheet

* `Iterator<E>`: `hasNext()`, `next()`, `remove()` (optional).
* `Iterable<E>`: `iterator()` → enables `for-each`.
* Fail-fast: `ConcurrentModificationException` on structural changes.
* Fail-safe: iterates over snapshot (e.g., `CopyOnWriteArrayList`).
* Use Streams for expressive pipelines; iterators for fine-grained control.

---

15. Pros and cons:


1. Pros:

- Single Responsibility Principle. You can clean up the client code and the collections by extracting bulky traversal algorithms into separate classes.
- Open/Closed Principle. You can implement new types of collections and iterators and pass them to existing code without breaking anything.
-  You can iterate over the same collection in parallel because each iterator object contains its own iteration abstractState.
- For the same reason, you can delay an iteration and continue it when needed.


2. Cons:

- Applying the pattern can be an overkill if your app only works with simple collections.
-  Using an iterator may be less efficient than going through elements of some specialized collections directly
