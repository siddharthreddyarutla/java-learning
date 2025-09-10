# Executor Framework

## Why Thread Pools?


Creating a new thread every time is expensive:
* OS has to allocate stack memory, schedule thread, manage lifecycle.
* If you have thousands of tasks, creating thousands of threads is wasteful.
* Manual thread management
* Resource management
* scalability
* Thread reuse
* Error handling


Instead, use a Thread Pool:
* Pre-create a fixed number of worker threads.
* Tasks are submitted to the pool.
* Threads are reused for multiple tasks.

By this way:
* ✅ Saves resources
* ✅ Improves performance
* ✅ Prevents system overload


### Executor Framework (Java 5+)


Introduced in java.util.concurrent to manage threads.

Main Interfaces:
1. **Executor** → basic interface to run tasks.
2. **ExecutorService** → extends Executor, adds task management (submit, shutdown, etc).
3. **ScheduledExecutorService** → schedules tasks with delays or periodically.


### Example 1: Using ExecutorService

```java
import java.util.concurrent.*;

public class ExecutorDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3); // 3 threads in pool

        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " running on " +
                        Thread.currentThread().getName());
            });
        }

        executor.shutdown(); // no new tasks, finish current ones
    }
}
```

Output (tasks shared among 3 threads):

```java
Task 1 running on pool-1-thread-1
Task 2 running on pool-1-thread-2
Task 3 running on pool-1-thread-3
Task 4 running on pool-1-thread-1
Task 5 running on pool-1-thread-2
```


### Example 2: Callable + Future with ExecutorService

* Unlike Runnable, Callable can return a value.
* Submit with submit(), get result with Future.get().


```java
import java.util.concurrent.*;

public class CallableFutureDemo {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<String> task = () -> {
            Thread.sleep(1000);
            return "Result from " + Thread.currentThread().getName();
        };

        Future<String> future = executor.submit(task);

        System.out.println("Waiting for result...");
        System.out.println("Got: " + future.get()); // blocks until ready

        executor.shutdown();
    }
}
```

## Types of Thread Pools (Factory Methods in Executors)


### Fixed Thread Pool
  - Executors.newFixedThreadPool(n)
  - Good for constant workload.

### Cached Thread Pool
  - Executors.newCachedThreadPool()
  - Expands/shrinks dynamically. 
  - Good for short-lived tasks.

### Single Thread Executor
  - Executors.newSingleThreadExecutor()
  - Executes tasks sequentially.

### Scheduled Thread Pool
  - Executors.newScheduledThreadPool(n)
  - For delayed or periodic tasks (schedule(), scheduleAtFixedRate()).


## Example 3: Scheduled Executor

```java
import java.util.concurrent.*;

public class ScheduledDemo {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        Runnable task = () -> System.out.println("Running at " + System.currentTimeMillis());

        // run every 2 seconds after initial 1 second delay
        scheduler.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);
    }
}
```

## ThreadPoolExecutor (Custom)

If you need full control, use ThreadPoolExecutor directly:

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    2, 4, 10, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(2)
);
```

### What does .get() do?
*    future.get() is a blocking call.
*    It makes the calling thread (here main) wait until the task finishes.
*    It also:
     - Returns the result of the Callable<T> (or null if it was a Runnable).
     - Throws exceptions if the task failed (ExecutionException, InterruptedException, TimeoutException if using get(timeout)).
*    👉 If you don’t call .get(), your task still runs, but:
*    You won’t know if it completed successfully.
*    You won’t see exceptions from the task (they’ll be swallowed by the executor, unless logged).


### Why is shutdown() required?
* ExecutorService creates non-daemon threads (user threads).
* JVM does not exit while user threads are alive.
* If you never call executorService.shutdown(), the JVM keeps running, waiting for more tasks forever.
* That’s why we usually call
* which:
  - Rejects new tasks.
  - Lets current tasks finish.
  - Eventually terminates the executor threads.


### Summary

* ExecutorService manages threads efficiently.
* Callable + Future → get results from tasks.
* Different pool types for different workloads:
  - Fixed, Cached, Single, Scheduled.
* For fine control → ThreadPoolExecutor.