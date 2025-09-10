# Inter Thread Communication

## Why do we need Inter-thread Communication?


* Locks (synchronized, ReentrantLock) ensure mutual exclusion → only one thread at a time.
* But sometimes, threads need to coordinate with each other.

#### Example: Producer-Consumer Problem


* Producer thread generates data and puts into a buffer.
* Consumer thread takes data from buffer.
* Without coordination:
  - Producer might overwrite data before consumer reads it.
  - Consumer might try to read before producer writes.

We need a way for one thread to pause and another to signal when it’s ready.


## wait(), notify(), notifyAll()

> These methods are defined in Object class (not Thread), because every object has a lock (monitor).

### 1. wait()

Releases the lock and makes the thread wait until notify() or notifyAll() is called.

### 2. notify()

Wakes up one waiting thread (chosen randomly by JVM).


### 3. notifyAll()

Wakes up all waiting threads, but only one can acquire the lock at a time.


> ⚠️ Must be called inside a synchronized block/method, otherwise IllegalMonitorStateException.


### Producer-Consumer with wait/notify

```java
class SharedBuffer {
    private int data;
    private boolean hasData = false;

    public synchronized void produce(int value) throws InterruptedException {
        while (hasData) {  // wait if buffer already full
            wait();
        }
        data = value;
        System.out.println("Produced: " + value);
        hasData = true;
        notify(); // wake up consumer
    }

    public synchronized int consume() throws InterruptedException {
        while (!hasData) { // wait if buffer empty
            wait();
        }
        System.out.println("Consumed: " + data);
        hasData = false;
        notify(); // wake up producer
        return data;
    }
}

public class ProducerConsumerDemo {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    buffer.produce(i);
                } catch (InterruptedException e) { }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    buffer.consume();
                } catch (InterruptedException e) { }
            }
        });

        producer.start();
        consumer.start();
    }
}

```

### Key Points about wait/notify

1. Must be used inside synchronized block/method.
2. wait() releases the lock; sleep() does not.
3. Always use while (not if) around wait() → prevents spurious wakeups.
4. notify() wakes only one thread, notifyAll() wakes all.


### Limitations of wait/notify

1. Complex to manage.
2. Easy to cause deadlocks or missed signals.
3. Hard to build scalable concurrent apps.

> That’s why modern Java prefers BlockingQueue and other java.util.concurrent utilities.


### Producer-Consumer with BlockingQueue (Better Way)

```java
import java.util.concurrent.*;

public class BlockingQueueDemo {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put(i);
                    System.out.println("Produced: " + i);
                }
            } catch (InterruptedException e) {}
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    int value = queue.take();
                    System.out.println("Consumed: " + value);
                }
            } catch (InterruptedException e) {}
        });

        producer.start();
        consumer.start();
    }
}
```

## Summary

1. wait(), notify(), notifyAll() → low-level mechanism for thread coordination.
2. Must be used inside synchronized.
3. Used in classic problems like Producer-Consumer.
4. Modern alternative → BlockingQueue, Semaphore, CountDownLatch, CyclicBarrier.