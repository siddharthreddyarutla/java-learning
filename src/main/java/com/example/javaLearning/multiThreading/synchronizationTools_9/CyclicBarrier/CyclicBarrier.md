# Cyclic Barrier

1. A barrier where a set of threads must all wait at a common point before proceeding.
2. Unlike CountDownLatch, it can be reused (cyclic).


Example: Multiple players must reach the starting line before the game begins.

```java
import java.util.concurrent.*;

class CyclicBarrierDemo {
    public static void main(String[] args) {
        int players = 3;
        CyclicBarrier barrier = new CyclicBarrier(players, 
            () -> System.out.println("All players ready! Game starts."));

        Runnable player = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " is ready");
                barrier.await(); // wait for others
                System.out.println(Thread.currentThread().getName() + " starts playing");
            } catch (Exception e) {}
        };

        for (int i = 1; i <= players; i++) {
            new Thread(player, "Player " + i).start();
        }
    }
}
```
