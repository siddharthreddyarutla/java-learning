package com.example.javaLearning.multiThreading.threadSafety;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class AtomicImpl {

  public static void main(String[] args) {

    Counter counter = new Counter();

    Thread thread1 = new Thread(() -> {
      IntStream.range(0, 1000).forEach(i -> {
        counter.increment();
      });
    });

    Thread thread2 = new Thread(() -> {
      IntStream.range(0, 1000).forEach(i -> {
        counter.increment();
      });
    });

    thread1.start();
    thread2.start();

    try {
      thread1.join();
      thread2.join();
    } catch (Exception e) {
      System.out.println(e);
    }

    System.out.println(counter.getCounter());

    System.out.println("Main completed execution");
  }

  public static class Counter {

    /**
     * For these kind of operations atomic classes can be used
     */
    private int counter = 0;

    private AtomicInteger atomicInteger = new AtomicInteger();

    /**
     * One way to tackle in correct count is by using synchronized or extrinsic lock
     */
    public synchronized void increment() {
      atomicInteger.incrementAndGet();
    }

    public int getCounter() {
      return atomicInteger.get();
    }
  }
}
