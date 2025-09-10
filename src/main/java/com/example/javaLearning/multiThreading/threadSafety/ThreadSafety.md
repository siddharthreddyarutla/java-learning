# Thread safety

## 1. Atomic Variables

* 👉 Problem with normal variables:
* int x = x + 1; is not atomic → it does read → modify → write.
* With multiple threads, it causes race conditions.


* 👉 Solution: Use atomic classes (from java.util.concurrent.atomic).
* They use lock-free, hardware-level atomic operations (CAS – Compare-And-Swap).


### Common Atomic Classes

1. AtomicInteger → atomic int.
2. AtomicLong → atomic long.
3. AtomicBoolean → atomic boolean.
4. AtomicReference<T> → atomic object reference.

```java
import java.util.concurrent.atomic.AtomicInteger;

class AtomicCounter {
    private AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet(); // atomic operation
    }

    public int getCount() {
        return count.get();
    }
}

public class AtomicDemo {
    public static void main(String[] args) throws InterruptedException {
        AtomicCounter counter = new AtomicCounter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Final count = " + counter.getCount()); // always 2000
    }
}
```

> ✅ No need for synchronized, yet still thread-safe.

