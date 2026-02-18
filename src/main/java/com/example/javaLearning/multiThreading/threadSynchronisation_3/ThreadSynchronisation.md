# Thread synchronisation

## The Problem: Race Conditions

> When multiple threads access a shared resource (like a variable, file, DB record) without
> synchronization, results can become inconsistent or unpredictable.
> This is called a Race Condition.

### Example of Race Condition

```java
class Counter {
  private int count = 0;

  public void increment() {
    count++; // not atomic!
  }

  public int getCount() {
    return count;
  }
}


public class RaceConditionDemo {
  public static void main(String[] args) throws InterruptedException {
    Counter counter = new Counter();

    Thread t1 = new Thread(() -> {
      for (int i = 0; i < 1000; i++)
        counter.increment();
    });

    Thread t2 = new Thread(() -> {
      for (int i = 0; i < 1000; i++)
        counter.increment();
    });

    t1.start();
    t2.start();

    t1.join();
    t2.join();

    System.out.println("Final Count = " + counter.getCount());
  }
}
```

1. Expected output: 2000
2. 👉 Actual output: something less than 2000 (because count++ is not atomic, it breaks into read → add → write steps).


### Solution: Synchronization

Synchronization ensures that only one thread can access a critical section (shared resource) at a time.

## 1. Synchronized Methods

Use synchronized keyword on a method → only one thread can execute it at a time per object.

```java
class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++; // now thread-safe
    }

    public int getCount() {
        return count;
    }
}
```


## 2. Synchronized Blocks

Instead of synchronizing the whole method, you can synchronize only the critical section.

```java
class Counter {
    private int count = 0;

    public void increment() {
        synchronized (this) {
            count++;
        }
    }
}
```

## Synchronization Locks

Behind the scenes, synchronization works with Intrinsic Locks / Monitors:

1. Each object in Java has a lock.
2. When a thread enters a synchronized block/method, it acquires the lock.
3. Other threads must wait until the lock is released.


## Problems with Synchronization

* Thread Contention → If too many threads compete for lock, performance drops.
* Deadlocks → Two threads waiting forever for each other’s lock.
* Starvation → A low-priority thread never gets the lock.
* Blocking → Threads are paused while waiting for locks. 
