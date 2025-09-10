package com.example.javaLearning.multiThreading.threadSynchronisation;

public class ThreadSynchronisation {

  public static void main(String[] args) {

    Counter counter = new Counter();
    /**
     * In race condition count is not always as expected, shared resource will not be updated
     * correct
     */
    Thread thread = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        counter.incrementCount();
      }
    });
    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        counter.incrementCount();
      }
    });

    thread.start();
    thread1.start();

    try {
      thread.join();
      thread1.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.println(e);
    }
    System.out.println(counter.count);
  }

  public static class Counter {

    private int count = 0;

    // Here without synchronisation multiple thread can execute same method simultaneously and
    // results in incorrect increment
    public synchronized void incrementCount() {
      count++;
    }

    public void increment() {
      // Add synchronized block only at race condition or critical section where shared resource
      // is accessed or modified, if not required on entire method
      synchronized (this) {
        count++;
      }
    }
  }
}