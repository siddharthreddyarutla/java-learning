# Thread

### What is a Thread?
* A thread is the smallest unit of execution within a process.
* In Java, a process is an instance of a running program, and a process can contain multiple threads that run concurrently.
* Each thread has:
* Its own stack (execution context)
* A program counter (PC) to keep track of execution
* But shares heap memory with other threads of the same process.


👉 Example:

If you open MS Word:
* One thread is checking for spelling,
* Another is handling UI,
* Another is saving the document in the background.

🔹 Benefits of Using Threads
* Concurrency → Multiple tasks at the same time (not true parallelism on single core, but context switching).
* Resource Sharing → Threads of same process share memory, unlike processes which need IPC.
* Improved Performance → Useful in multi-core CPUs where tasks can actually run in parallel.


### Ways to Create a Thread in Java


### 1. Extending Thread class

* Create a class that extends Thread.
* Override the run() method.
* Start the thread using start() (⚠️ not run() directly).

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

public class ThreadExample1 {
    public static void main(String[] args) {
        MyThread t1 = new MyThread();
        t1.start(); // starts new thread
    }
}
```


### 2. Implementing Runnable interface

* Create a class that implements Runnable.
* Override run().
* Pass it to a Thread object, then call start().

```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable thread: " + Thread.currentThread().getName());
    }
}

public class ThreadExample2 {
    public static void main(String[] args) {
        Thread t1 = new Thread(new MyRunnable());
        t1.start();
    }
}
```

✅ Preferred over extending Thread because:
> Java supports single inheritance, so implementing Runnable allows extending another class too.



### 3. Using Callable + Future (since Java 5)
*    Unlike Runnable, Callable can return a value and throw exceptions.
*    Needs ExecutorService to execute.

```java
import java.util.concurrent.*;

class MyCallable implements Callable<String> {
    @Override
    public String call() {
        return "Result from thread: " + Thread.currentThread().getName();
    }
}

public class ThreadExample3 {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new MyCallable());
        
        System.out.println(future.get()); // waits for result
        executor.shutdown();
    }
}
```


### 4. Using Lambda Expressions (Java 8+)

```java
public class ThreadExample4 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            System.out.println("Lambda thread: " + Thread.currentThread().getName());
        });
        t1.start();
    }
}
```

| Method             | Return Value?                     | Exception Handling? | Flexibility                     |
| ------------------ | --------------------------------- | ------------------- | ------------------------------- |
| `Thread` (extends) | ❌                                 | ❌                   | Less (single inheritance issue) |
| `Runnable`         | ❌                                 | ❌                   | Better (preferred)              |
| `Callable`         | ✅                                 | ✅                   | Best for tasks with results     |
| `Lambda`           | Depends (`Runnable` / `Callable`) | Yes                 | Concise                         |
