# CompletableFuture


## ExecutorService Recap


1. ExecutorService (thread pools) lets you submit tasks (Runnable or Callable).
2. If you want a result → you use Future<T>.
3. 👉 Problem with plain Future:
4. Blocking → To get the result, you must call future.get(), which blocks until task completes.
5. No chaining → If you need to run task B after task A, you have to manually wait and then submit.
6. No composition → Combining multiple Futures (e.g., run two tasks in parallel and then combine results) is very difficult.
7. Error handling is clunky (need try-catch around get()).


# Why CompletableFuture

> CompletableFuture (Java 8) solves all of the above.
It’s part of java.util.concurrent and provides asynchronous, non-blocking programming.



✅ Key Advantages:

1. Non-blocking → you can register callbacks that run when the result is ready.
2. Chaining → you can link multiple async tasks together.
3. Combining → you can run tasks in parallel and then combine results.
4. Exception handling → fluent handling without ugly try-catch.


### Example 1: ExecutorService with Future (Old Way)

```java
import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(1000);
            return 10;
        });

        System.out.println("Doing other work...");

        // BLOCKS until result is ready
        Integer result = future.get(); 
        System.out.println("Result: " + result);

        executor.shutdown();
    }
}
```

Output:
```text
Doing other work...
Result: 10   (after 1 second)
```

> Problem → The thread is blocked at future.get().


### Example 2: CompletableFuture (Non-blocking)

```java
import java.util.concurrent.*;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            return 10;
        });

        // Non-blocking callback
        future.thenAccept(result -> 
            System.out.println("Result: " + result));

        System.out.println("Doing other work...");
    }
}
```

Output:

```text
Doing other work...
Result: 10
```

* ✅ Program does not block at all.
* ✅ Result is handled asynchronously.


### Example 3: Chaining with thenApply

```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 10)
    .thenApply(n -> n * 2) // multiply by 2
    .thenApply(n -> n + 5); // add 5

future.thenAccept(System.out::println); // prints 25
```

> 👉 This would require manual nested Futures if done with ExecutorService.


### Example 4: Combining Tasks

Run two tasks in parallel and then combine results:

```java
CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);

CompletableFuture<Integer> result = f1.thenCombine(f2, (a, b) -> a + b);

result.thenAccept(System.out::println); // prints 30
```


✅ With ExecutorService + Future, you’d have to manually wait for both, then combine.


### Example 5: Exception Handling

```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    if (true) throw new RuntimeException("Error!");
    return 10;
}).exceptionally(ex -> {
    System.out.println("Exception: " + ex.getMessage());
    return 0;
});

future.thenAccept(System.out::println);
```

👉 Cleaner error handling than wrapping future.get() in try-catch.


| Feature         | ExecutorService + Future    | CompletableFuture                                  |
| --------------- | --------------------------- | -------------------------------------------------- |
| Get result      | `future.get()` (blocking)   | Non-blocking callbacks (`thenApply`, `thenAccept`) |
| Chaining        | Manual                      | Built-in (`thenApply`, `thenCompose`)              |
| Combining tasks | Manual, clumsy              | Built-in (`thenCombine`, `allOf`, `anyOf`)         |
| Error handling  | try-catch around `get()`    | Fluent (`exceptionally`, `handle`)                 |
| Async           | Yes, but blocking on result | Fully async, non-blocking                          |


> CompletableFuture uses CompletableFuture to achieve async behaviour

> ForkJoinPool - the common pool’s worker threads are daemon threads by default. That means they do not keep the JVM alive. If only daemon threads remain, JVM exits and those tasks may terminate mid-work.
