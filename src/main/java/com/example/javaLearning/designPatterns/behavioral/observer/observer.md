# Observer (Publish/Subscribe) — Java module (complete, step-by-step, MD notes for learning & interviews)

Nice — moving on! Below is a full module for the **Observer (Publish/Subscribe)** pattern, laid out like your SOLID overview: concept, tradeoffs, UML/participants, detailed implementations (plain Java, thread-safe + async, Spring), code you can copy/run, pitfalls, best practices, and interview questions. Use this as a study note and implement side-by-side.

---

# 1. Quick summary (what & why)

**Observer (Publish/Subscribe)** lets objects (Observers/subscribers) register to receive updates from another object (Subject/publisher) when its state changes. It decouples senders from receivers. Useful for event systems, UI notifications, caches, reactive flows, messaging between modules.

Two flavors:

* **Push model**: Subject pushes new data to observers on notify.
* **Pull model**: Subject notifies observers *something changed*; observers pull needed data from subject.

When to use:

* Many components need to react to changes of one component.
* Decoupling producers and consumers.
* Implementing event-driven flows inside an app (or across services).

When **not** to use:

* You need strict ordering & transactional commits across subscribers.
* Tight synchronous processing requirements where failures must rollback a producer action.
* Very simple one-on-one interactions (overkill).

---

# 2. Participants (roles)

* **Subject / Publisher**: Accepts registration/unregistration of observers; keeps list of observers; notifies them when events occur.
* **Observer / Subscriber**: Implements an interface to receive notifications.
* **ConcreteSubject**: Implementation of the subject that triggers notifications.
* **Event / Payload (optional)**: Object representing event data.

---

# 3. UML (informal)

```
+----------------+          0..*         +----------------+
|   Subject      |<--------------------->|   Observer     |
| - observers[]  | register()/remove()   | + update(event)|
| + notify(event)|                       +----------------+
+----------------+
       ^
       |
+--------------------+
| ConcreteSubject    |
| + setState(...)    |
| + getState()       |
+--------------------+
```

---

# 4. Plain Java (minimal but correct) — pull and push

**Explanation:** This is the canonical minimal implementation with generics. Use this to understand core mechanics.

```java
// File: ObserverPatternSimple.java
package com.example.designpatterns.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Observer interface - receives Event<E>
public interface Observer<E> {
    void onUpdate(E event);
}

// Subject / Publisher interface
public interface Subject<E> {
    void register(Observer<E> observer);
    void unregister(Observer<E> observer);
    void notifyObservers(E event); // push model
}

// Concrete subject with push model
public class ConcreteSubject<E> implements Subject<E> {
    private final List<Observer<E>> observers = new ArrayList<>();

    @Override
    public synchronized void register(Observer<E> observer) {
        Objects.requireNonNull(observer);
        observers.add(observer);
    }

    @Override
    public synchronized void unregister(Observer<E> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(E event) {
        // Make snapshot to avoid ConcurrentModificationException
        List<Observer<E>> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(observers);
        }
        for (Observer<E> obs : snapshot) {
            obs.onUpdate(event);
        }
    }
}

// Example usage
class Demo {
    public static void main(String[] args) {
        ConcreteSubject<String> subject = new ConcreteSubject<>();

        Observer<String> o1 = event -> System.out.println("Observer1 got: " + event);
        Observer<String> o2 = event -> System.out.println("Observer2 got: " + event);

        subject.register(o1);
        subject.register(o2);

        subject.notifyObservers("Hello observers!");
        subject.unregister(o1);
        subject.notifyObservers("Second event");
    }
}
```

Notes:

* `notifyObservers` above uses **push** (payload passed into observers).
* Use snapshots to avoid concurrency issues if observers register/unregister during notification.

---

# 5. Push vs Pull (concrete examples)

**Push** (subject sends payload):

```java
subject.notifyObservers(new PriceChangeEvent(symbol, newPrice));
```

**Pull** (subject just signals and observers query state):

```java
subject.notifyObservers(null);        // or a simple "changed" token
// observer calls subject.getPrice(symbol)
```

Use **push** when events carry all needed data and you want fewer fetches. Use **pull** when payloads are big and observers may want different slices of state.

---

# 6. Thread-safe + asynchronous notify (production ready patterns)

* Use `CopyOnWriteArrayList` for light read-heavy lists.
* Use an `ExecutorService` for asynchronous dispatch.
* Protect from observer failures: catch exceptions per observer so one bad observer doesn't break others.
* Optionally, allow synchronous or asynchronous mode.

```java
// File: AsyncSubject.java
package com.example.designpatterns.observer;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class AsyncSubject<E> implements Subject<E>, AutoCloseable {
    private final List<Observer<E>> observers = new CopyOnWriteArrayList<>();
    private final ExecutorService executor;

    public AsyncSubject(int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public AsyncSubject(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void register(Observer<E> observer) {
        observers.add(observer);
    }

    @Override
    public void unregister(Observer<E> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(E event) {
        for (Observer<E> obs : observers) {
            // dispatch async, guard each observer
            executor.submit(() -> {
                try {
                    obs.onUpdate(event);
                } catch (Throwable t) {
                    // handle logging and swallow to avoid breaking others
                    System.err.println("Observer failed: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
```

Tradeoffs:

* Async improves responsiveness of the publisher but requires managing lifecycle of thread pools.
* Ordering between observers is not guaranteed unless you implement sequencing.

---

# 7. Alternate approaches & libraries

* **java.util.Observer / Observable**: exists historically but `Observable` is a class, not an interface, and both are effectively deprecated (bad design). **Do not use** in modern code.
* **EventBus libraries**: Guava's `EventBus`, Google Guice's event support, or Reactor / RxJava for reactive streams. These provide advanced features (threading, filtering, annotations).
* **Message brokers**: For cross-process/pub-sub (not in-process), use Redis Pub/Sub, Kafka, RabbitMQ — outside the scope of this in-process module.
* **Spring Events** (below) for application-level events.

---

# 8. Spring → ApplicationEvent / @EventListener example

**Why**: If you use Spring, prefer Spring's event publishing; it integrates with the framework (components, transactions, ordering, async).

Two modern patterns:

1. **Publish a payload** with `ApplicationEventPublisher.publishEvent(Object payload)` — recommended.
2. Or extend `ApplicationEvent` (older style).

**Spring Boot example - minimal**

```java
// 1) Event class (payload)
public record PriceChangedEvent(String symbol, double price) {}

// 2) Publisher component
@Component
public class PricePublisher {
    private final ApplicationEventPublisher publisher;
    public PricePublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(String symbol, double price) {
        publisher.publishEvent(new PriceChangedEvent(symbol, price));
    }
}

// 3) Listener
@Component
public class PriceListener {

    @EventListener
    public void handlePriceChange(PriceChangedEvent ev) {
        System.out.println("Received price update: " + ev.symbol() + " -> " + ev.price());
    }

    // Async listener (requires @EnableAsync and a TaskExecutor bean)
    @EventListener
    @Async
    public void handlePriceChangeAsync(PriceChangedEvent ev) {
        // will be handled in thread pool
    }
}

// 4) Bootstrap (Spring Boot main)
@SpringBootApplication
@EnableAsync // if you want @Async listeners
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(App.class, args);
        PricePublisher p = ctx.getBean(PricePublisher.class);
        p.publish("AAPL", 172.45);
    }
}
```

Notes:

* `@EventListener` methods can be async with `@Async`.
* Spring's event publisher is synchronous by default — use `@Async` or publish to an `Executor` if you want async behavior.

---

# 9. Common pitfalls & best practices

1. **Memory leaks (listeners never unregistered)**

    * Always remove/unregister observers if lifecycle ends (e.g., UI components, long-running services).
    * Use weak references if appropriate (e.g., `WeakReference` to observer) but be careful with complexity.

2. **Performance (lots of observers / high frequency)**

    * Use batching or sampling.
    * Use async dispatch or queueing.
    * Use `CopyOnWriteArrayList` for many read, few writes.

3. **Error handling**

    * Guard each observer call with `try/catch` so a failing observer doesn't stop notification to others.
    * Consider dead-letter queue or metrics for failed handlers.

4. **Ordering & ordering guarantees**

    * If ordering is required, document and enforce it (e.g., single-threaded executor or ordered dispatch).

5. **Backpressure & overload**

    * If observers are slow, consider dropping events, buffering, or using reactive streams (Reactor, RxJava).

6. **Strong coupling through event types**

    * Use well-defined event types (classes/interfaces) to avoid ad-hoc coupling through `Object` payloads.

7. **Threading model**

    * Decide early whether notifications are sync or async, make it configurable, and document it.

---

# 10. Advanced: typed registry enforcing generic types per event type

If you want compile-time safety that a `ValidationEvent<Long>` goes to `Observer<Long>`, you can create a typed registry keyed by event class:

```java
public class TypedEventBus {
    private final Map<Class<?>, List<Observer<?>>> map = new ConcurrentHashMap<>();

    public <E> void register(Class<E> eventType, Observer<E> observer) {
        map.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(observer);
    }

    @SuppressWarnings("unchecked")
    public <E> void publish(E event) {
        List<Observer<?>> list = map.get(event.getClass());
        if (list == null) return;
        for (Observer<?> o : list) {
            ((Observer<E>) o).onUpdate(event);
        }
    }
}
```

This trades some casting but gives clear separation by concrete event classes.

---

# 11. Complete runnable example (single file) — synchronous safe variant

```java
// File: ObserverCompleteDemo.java
package com.example.observer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObserverCompleteDemo {

    public static void main(String[] args) {
        NewsPublisher publisher = new NewsPublisher();

        NewsSubscriber a = new NewsSubscriber("Alice");
        NewsSubscriber b = new NewsSubscriber("Bob");

        publisher.register(a);
        publisher.register(b);

        publisher.publish(new News("sports", "Team A won"));
        publisher.unregister(b);
        publisher.publish(new News("weather", "Rain tomorrow"));
    }

    record News(String topic, String content) {}

    interface Observer<E> {
        void onUpdate(E event);
    }

    interface Subject<E> {
        void register(Observer<E> obs);
        void unregister(Observer<E> obs);
        void publish(E event);
    }

    static class NewsPublisher implements Subject<News> {
        private final List<Observer<News>> observers = new CopyOnWriteArrayList<>();

        @Override
        public void register(Observer<News> obs) { observers.add(obs); }

        @Override
        public void unregister(Observer<News> obs) { observers.remove(obs); }

        @Override
        public void publish(News news) {
            for (Observer<News> obs : observers) {
                try {
                    obs.onUpdate(news);
                } catch (Exception e) {
                    System.err.println("Observer failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    static class NewsSubscriber implements Observer<News> {
        private final String name;
        NewsSubscriber(String name) { this.name = name; }

        @Override
        public void onUpdate(News event) {
            System.out.println(name + " received: [" + event.topic() + "] " + event.content());
        }
    }
}
```

---

# 12. Checklist to “master” Observer pattern (practice tasks)

* Implement both push and pull variants; test with multiple observers.
* Add thread-safety (use `CopyOnWriteArrayList`) and unit tests for concurrent register/unregister while notifying.
* Implement async notifications using `ExecutorService` and measure latency differences.
* Implement a typed event bus (registry keyed by event class) and test compile-time safety.
* Try Spring `ApplicationEventPublisher` and `@EventListener` integration; test `@Async` listeners.
* Experiment with Guava `EventBus` (if allowed by your project) and compare features.

---

# 13. Interview questions (possible Q&A)

1. Explain the Observer pattern. When would you use it?
2. What is the difference between push and pull models in Observer? Give examples.
3. How would you make an Observer implementation thread-safe?
4. Why is `java.util.Observer` / `Observable` considered a bad API today?
5. How would you prevent memory leaks with observers in long-running applications?
6. How do you handle errors thrown by observers so they don’t stop notifications to others?
7. Explain tradeoffs between synchronous and asynchronous event delivery.
8. How would you ensure ordering of event processing among observers?
9. Implement an event bus that only notifies observers registered for matching event subclasses.
10. How would you implement backpressure in an observer pattern handling high-volume events?
11. How do Spring application events differ from a home-grown Observer pattern?
12. Give a real-world example where Observer would be a poor fit and explain why.

---

# 14. Short cheatsheet (one-page)

* Use `EnumMap` or `CopyOnWriteArrayList` for efficiency and concurrency.
* Use snapshots or concurrent collections to avoid `ConcurrentModificationException`.
* Guard observer execution with `try/catch`.
* Decide sync vs async early; document it.
* Unregister to avoid leaks; consider weak refs.
* Use framework solution (Spring EventBus, Reactor) for complex apps — they solve threading, backpressure, retries.

