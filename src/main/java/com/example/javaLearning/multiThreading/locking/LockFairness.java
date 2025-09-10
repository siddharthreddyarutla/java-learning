package com.example.javaLearning.multiThreading.locking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockFairness {

  public static void main(String[] args) {
    Runnable fairNessTask = new Runnable() {
      final FairnessLock lockFairness = new FairnessLock();

      @Override
      public void run() {
        lockFairness.accessResource();
      }
    };

    Thread thread1 = new Thread(fairNessTask, "Thread 1");
    Thread thread2 = new Thread(fairNessTask, "Thread 2");
    Thread thread3 = new Thread(fairNessTask, "Thread 3");

    thread1.start();
    thread2.start();
    thread3.start();
  }

  public static class FairnessLock {
    private final Lock unFairLock = new ReentrantLock(true);

    public void accessResource() {
      unFairLock.lock();
      try {
        System.out.println(Thread.currentThread().getName() + " acquired lock");
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        System.out.println(Thread.currentThread().getName() + " lock released ");
        unFairLock.unlock();
      }
    }
  }
}
