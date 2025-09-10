# Thread Priority

> Since multiple threads may be RUNNABLE, JVM needs to decide which one to run first.
> This is called Thread Scheduling.


## 1. Thread Scheduling

* Java uses a preemptive, priority-based, time-sliced scheduling model.
* Actual behavior depends on OS + JVM implementation (so not 100% deterministic).
* Scheduler decides:
  - Which thread to run. 
  - How long a thread runs.


## 2. Thread Priority

* Every thread has a priority (an int value from 1 to 10):
* Thread.MIN_PRIORITY = 1
* Thread.NORM_PRIORITY = 5 (default)
* Thread.MAX_PRIORITY = 10

```java
Thread t1 = new Thread(() -> System.out.println("t1 running"));
Thread t2 = new Thread(() -> System.out.println("t2 running"));

t1.setPriority(Thread.MIN_PRIORITY); // 1
t2.setPriority(Thread.MAX_PRIORITY); // 10
```

# 3. Example of Thread Priority

```java
class PriorityThread extends Thread {
    public PriorityThread(String name) {
        super(name);
    }
    @Override
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println(getName() + " running with priority " + getPriority());
        }
    }
}

public class ThreadPriorityDemo {
    public static void main(String[] args) {
        PriorityThread t1 = new PriorityThread("Low");
        PriorityThread t2 = new PriorityThread("Normal");
        PriorityThread t3 = new PriorityThread("High");

        t1.setPriority(Thread.MIN_PRIORITY);   // 1
        t2.setPriority(Thread.NORM_PRIORITY);  // 5
        t3.setPriority(Thread.MAX_PRIORITY);   // 10

        t1.start();
        t2.start();
        t3.start();
    }
}
```

## 4. Thread Yielding

* Thread.yield() → tells the scheduler:
  - “I am willing to give up my current CPU time slice.”
* The thread goes back to RUNNABLE state, allowing equal/higher priority threads a chance.
* Not guaranteed that it will immediately switch.

```java
Thread t = new Thread(() -> {
    for (int i = 1; i <= 5; i++) {
        System.out.println(Thread.currentThread().getName() + " - " + i);
        Thread.yield(); // hint to scheduler
    }
});
t.start();
```

## 5. Thread Join (revisited in scheduling context)

* join() lets one thread wait for another to finish before continuing.
* It influences scheduling since waiting threads don’t compete for CPU.

```java
Thread t1 = new Thread(() -> {
    for (int i = 1; i <= 5; i++) {
        System.out.println("t1 - " + i);
    }
});

Thread t2 = new Thread(() -> {
    try {
        t1.join(); // waits for t1 to finish
        System.out.println("t2 starts after t1 finishes");
    } catch (InterruptedException e) {
      System.out.println(e);
    }
});

t1.start();
t2.start();
```


