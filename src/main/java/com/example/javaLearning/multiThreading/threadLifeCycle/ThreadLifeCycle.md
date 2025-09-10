# Thread Lifecycle in Java

>A thread in Java goes through different states from birth to death.
Java provides an enum called Thread.State which has 6 states.


## 1. NEW

* A thread is in NEW state when it is created but not started.
* Created using new Thread().
* Moves to RUNNABLE when start() is called.

```java
Thread t = new Thread(() -> System.out.println("Thread running"));
System.out.println(t.getState()); // NEW
```


## 2. RUNNABLE

* After calling start(), the thread is in RUNNABLE.
* It may be running or waiting for CPU time (depends on OS thread scheduler).
* This is not guaranteed execution—only “ready to run”.

```java
t.start();
System.out.println(t.getState()); // RUNNABLE (or maybe still NEW if checked too fast)
```


## 3. RUNNING

* A thread actually executes inside its run() method.
* JVM thread scheduler picks it from RUNNABLE.
* Only one thread per core can be in RUNNING state.


## 4. WAITING

* A thread is in WAITING state if it is waiting indefinitely for another thread to signal it.
* Enters this state when you call wait() without timeout.
* Comes back when another thread calls notify() / notifyAll().

## 5. TIMED_WAITING

*    A thread is waiting for a specific time.
*    Methods that cause this:
*    sleep(ms)
*    join(ms)
*    wait(ms)
*    parkNanos() / parkUntil()


## 6. TERMINATED

* A thread dies after completing execution.
* Once in this state, it cannot be restarted.

```java
Thread t = new Thread(() -> System.out.println("Thread finished"));
t.start();
t.join();
System.out.println(t.getState()); // TERMINATED
```


```text
 NEW  →  RUNNABLE  →  RUNNING
  ↑                   ↓
  |                   |
 TERMINATED    WAITING / TIMED_WAITING
```


## Important Methods Affecting Lifecycle

* ✅ start()
* Starts a new thread, calls run() internally.
* ⚠️ Calling run() directly will NOT create a new thread, it runs in the same thread.
* ✅ sleep(ms)
* Puts current thread into TIMED_WAITING.
* Does NOT release locks.
* ✅ join()
* One thread waits until another thread completes.
* Example: t1.join() → calling thread waits until t1 is finished.
* ✅ yield()
* Suggests the scheduler to pause current thread and give chance to others.
* But it’s only a hint, not guaranteed.
* ✅ wait() / notify() / notifyAll()
* Used for inter-thread communication (producer-consumer problems).
* Wait releases lock, unlike sleep.