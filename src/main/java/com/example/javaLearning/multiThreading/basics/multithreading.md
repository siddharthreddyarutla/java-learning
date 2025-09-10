
# 1. Multithreading

1. Multithreading means a program can run multiple threads inside the same process.
2. Each thread runs independently but shares the same memory (heap).
3. Java has built-in support (Thread, Runnable, ExecutorService).

Example:

```java
class MyTask extends Thread {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}

public class MultithreadDemo {
    public static void main(String[] args) {
        new MyTask().start();
        new MyTask().start();
    }
}
```

Output: Two threads run simultaneously.

---

# 2. Concurrency


1. Concurrency = dealing with lots of things at once.
2. A system is concurrent if it can switch between tasks and make progress on multiple tasks (even on a single-core CPU via time-slicing).
3. Concurrency ≠ always parallel. It’s about structure (managing multiple tasks).

Example:

```java
public class ConcurrencyDemo {
    public static void main(String[] args) {
        Runnable task1 = () -> { for (int i = 0; i < 5; i++) System.out.println("Task1"); };
        Runnable task2 = () -> { for (int i = 0; i < 5; i++) System.out.println("Task2"); };

        new Thread(task1).start();
        new Thread(task2).start();
    }
}
```

Even on a single-core CPU, both tasks progress concurrently (interleaved execution).

---

# 3. Parallel Processing

1. Parallelism = actually doing lots of things at the same time.
2. Requires multi-core CPUs (true simultaneous execution).
3. Parallelism is about speed (performance via simultaneous execution).
4. Parallel Streams & Fork/Join Pool in Java make parallel processing easy.

```java
import java.util.*;

public class ParallelDemo {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        numbers.parallelStream()
               .forEach(n -> System.out.println("Processed " + n + " in " + Thread.currentThread().getName()));
    }
}
```

Output: numbers processed by different worker threads at the same time.

---

# 4. Asynchronous Programming

1. Asynchronous = don’t block, continue with other work, react later when result is ready.
2. The program doesn’t wait for the task to complete.
3. Asynchronous ≠ always multithreaded — you can have async I/O in a single thread.
4. In Java, async is usually implemented with CompletableFuture, @Async in Spring, or reactive APIs.

Example:

```java
import java.util.concurrent.*;

public class AsyncDemo {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            return "Result from async task";
        }).thenAccept(result -> System.out.println("Got: " + result));

        System.out.println("Main thread continues without waiting...");
    }
}
```

output:
```text
Main thread continues without waiting...
Got: Result from async task
```

| Concept            | Focus                                    | Needs Multi-core?          | Blocking?                         | Example in Java               |
| ------------------ | ---------------------------------------- | -------------------------- | --------------------------------- | ----------------------------- |
| **Multithreading** | Multiple threads inside one process      | No (time-slicing possible) | Blocking if `join()`/`get()` used | `Thread`, `ExecutorService`   |
| **Concurrency**    | Structure: making progress on many tasks | No                         | May block                         | Two threads interleaving work |
| **Parallelism**    | Speed: simultaneous execution            | ✅ Yes (multi-core)         | Not necessarily                   | Parallel Streams, Fork/Join   |
| **Asynchronous**   | Non-blocking execution & callbacks       | No (can be event loop)     | ❌ Non-blocking                    | `CompletableFuture`, `@Async` |



## Real-World Analogy

1. Multithreading → A restaurant kitchen with multiple cooks (threads) working on different dishes.
2. Concurrency → A single cook multitasking (switching between cutting veggies and stirring soup).
3. Parallelism → Multiple cooks actually cooking at the same time on different stoves.
4. Asynchronous → You place an order, leave, and get a notification (callback) when your food is ready.


## Rule of thumb:

1. More threads than cores = concurrency (switching).
2. Threads ≤ cores = true parallelism (all progress at once).

## Summary

1. [X] Multithreading → Mechanism (multiple threads in a program).
2. [X] Concurrency → Design for handling multiple tasks (not always parallel).
3. [X] Parallel Processing → Actual simultaneous execution on multiple cores.
4. [X] Asynchronous → Non-blocking style, do other work and react when done.