# CountDownLatch


1. A latch that allows one or more threads to wait until a set of operations complete.
2. You initialize with a count.
3. Each time countDown() is called → count decreases.
4. When count reaches zero, all waiting threads are released.

Example: Wait for multiple services to start before proceeding.

```java
import java.util.concurrent.*;

class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        Runnable service = () -> {
            System.out.println(Thread.currentThread().getName() + " started");
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            System.out.println(Thread.currentThread().getName() + " finished");
            latch.countDown(); // decrease count
        };

        new Thread(service, "Service1").start();
        new Thread(service, "Service2").start();
        new Thread(service, "Service3").start();

        latch.await(); // main waits
        System.out.println("All services finished. Main thread continues.");
    }
}
```

### why count down latch is required even if it use future.get() as well serves the same right?

#### 1. What Future.get() does

* Tied to one specific task.
* Calling future.get():
  - Waits until that single task finishes (or throws).
  - Returns the task result.
  - Propagates exceptions (ExecutionException, InterruptedException, etc.).

If you have N tasks in a list of futures:

```java
for (Future<?> f : futures) {
    f.get();   // waits for each future, one by one
}
```

This means:
1. You wait for the first task to finish before even looking at the second.
2. If the first task is slow, you won’t process results of faster tasks earlier.
3. You don’t have an easy “all finished” signal.


#### 2. What CountDownLatch does

1. A separate synchronization aid.
2. You initialize it with a count = number of tasks.
3. Each task calls latch.countDown() when finished.
4. Another thread calls latch.await(), which blocks until the count reaches zero (i.e., all tasks have finished).


### Example Difference

1. Using only Future.get()

```java
ExecutorService executor = Executors.newFixedThreadPool(3);

List<Future<String>> futures = new ArrayList<>();
for (int i = 1; i <= 3; i++) {
    int id = i;
    futures.add(executor.submit(() -> {
        Thread.sleep(id * 1000);
        return "Task " + id;
    }));
}

// waits task by task
for (Future<String> f : futures) {
    System.out.println(f.get()); // sequential waiting
}
```

> If Task 1 is slow, you wait before seeing Task 2’s result, even if Task 2 already finished.


2. Using CountDownLatch

```java
ExecutorService executor = Executors.newFixedThreadPool(3);
CountDownLatch latch = new CountDownLatch(3);

for (int i = 1; i <= 3; i++) {
    int id = i;
    executor.submit(() -> {
        Thread.sleep(id * 1000);
        System.out.println("Task " + id + " done");
        latch.countDown();
        return null;
    });
}

latch.await(); // waits until ALL 3 tasks finish
System.out.println("All tasks completed!");
```

> Here, you don’t care about task results, but you unblock only when all tasks are done.


✅ Summary:
1. Future.get() → result/exception of one task, sequential blocking if in loop.
2. CountDownLatch → group completion signal, no results, better for coordination.