## 1. Intrinsic vs Extrinsic Lock

### Intrinsic Locks (a.k.a. Monitor Locks)
* These are the locks built into every Java object.
* Used when you write synchronized:
* synchronized (this) { … } → acquires this object’s intrinsic lock.
* synchronized static method → acquires class object’s intrinsic lock.
* Managed automatically by JVM.
* Easy to use, but limited:
* No fairness guarantee.
* No ability to try acquiring lock without blocking.
* No ability to interrupt while waiting.

Example:
```java
synchronized (someObject) {
    // only one thread at a time can enter this block per object
}
```


### Extrinsic Locks

* Provided in java.util.concurrent.locks package (Lock, ReentrantLock, ReadWriteLock).
* More flexible than intrinsic locks:
  - Support fairness policies.
  - Support tryLock() (non-blocking lock attempt).
  - Support lockInterruptibly() (can respond to interrupts).
  - Can be used with Condition objects (advanced wait/notify replacement).


Example with ReentrantLock:

```java
import java.util.concurrent.locks.ReentrantLock;

class SharedResource {
    private final ReentrantLock lock = new ReentrantLock();

    public void access() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is using resource");
        } finally {
            lock.unlock();
        }
    }
}
```


## 2. Reentrant Lock vs synchronized
A reentrant lock allows the same thread to acquire the lock multiple times without deadlocking itself.
Both synchronized and ReentrantLock are reentrant.

```java
class ReentrantDemo {
    private final ReentrantLock lock = new ReentrantLock();

    public void outer() {
        lock.lock();
        try {
            System.out.println("Outer acquired");
            inner(); // same thread calls again
        } finally {
            lock.unlock();
        }
    }

    public void inner() {
        lock.lock();
        try {
            System.out.println("Inner acquired");
        } finally {
            lock.unlock();
        }
    }
}
```


#### Why prefer ReentrantLock?
* synchronized → simple, automatic unlocking, good for most cases.
* ReentrantLock → advanced features:
  - tryLock()
  - lockInterruptibly()
  - fair vs non-fair policy
  - Multiple Condition variables


## 3. Fairness in Locks

* Fair lock → threads acquire lock in the order they requested it (FIFO).
* Non-fair lock (default) → thread scheduling may allow barging (a thread can “jump the line”).
* Use in ReentrantLock:

```java
ReentrantLock fairLock = new ReentrantLock(true);  // fair lock
ReentrantLock unfairLock = new ReentrantLock();   // default, non-fair
```


## 4. Deadlock Scenario

> Deadlock = two or more threads wait forever because each holds a lock the other needs.

```java
class DeadlockDemo {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void method1() {
        synchronized (lock1) {
            System.out.println("Thread 1 got lock1");
            synchronized (lock2) {
                System.out.println("Thread 1 got lock2");
            }
        }
    }

    public void method2() {
        synchronized (lock2) {
            System.out.println("Thread 2 got lock2");
            synchronized (lock1) {
                System.out.println("Thread 2 got lock1");
            }
        }
    }
}
```

If Thread 1 enters method1() and Thread 2 enters method2(), they can deadlock:
* T1 holds lock1, waiting for lock2
* T2 holds lock2, waiting for lock1.


## 5. Thread Starvation

1. Happens when low-priority threads never get CPU because high-priority threads keep hogging resources.
2. Also happens in unfair locks, where one thread might repeatedly acquire the lock before others get a chance.
3. Fair locks (ReentrantLock(true)) help reduce starvation.


## 6. Read-Write Locks

Sometimes multiple threads read-only → safe to allow them concurrently.
But writes should be exclusive.

```java
import java.util.concurrent.locks.*;

class SharedData {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    private int data = 0;

    public void write(int value) {
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " writing " + value);
            data = value;
        } finally {
            writeLock.unlock();
        }
    }

    public int read() {
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " reading " + data);
            return data;
        } finally {
            readLock.unlock();
        }
    }
}

```

1. Multiple readers can read in parallel.
2. Writer has exclusive lock, blocking readers & other writers.



## Summary
1. [ ] Intrinsic Locks → automatic, via synchronized.
2. [ ] Extrinsic Locks → manual control (ReentrantLock, ReadWriteLock).
3. [ ] Reentrant → same thread can re-acquire lock multiple times.
4. [ ] Fairness → fair locks avoid starvation but are slower.
5. [ ] Deadlock → circular waiting on locks.
6. [ ] Starvation → low-priority threads never run.
7. [ ] Read-Write Locks → improve performance for read-heavy scenarios.