package com.example.javaLearning.multiThreading.locking_6;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLock {

  public static void main(String[] args) {
    Deadlock deadlock = new Deadlock();
    deadlock.outerMethod();
  }

  public static class Deadlock {

    private final Lock lock = new ReentrantLock();

    /**
     * This is NOT a deadlock case.
     * <p>
     * ReentrantLock allows the same thread to acquire the same lock multiple times.
     * So when outerMethod calls innerMethod (which again calls outerMethod),
     * the lock is re-acquired by the same thread and the hold count increases.
     * <p>
     * This results in infinite recursive calls, eventually causing StackOverflowError,
     * not a deadlock.
     * <p>
     * For each lock() call, unlock() must be called the same number of times.
     */

    public void outerMethod() {
      try {
        lock.lock();
        System.out.println("In outer method");
        innerMethod();
      } catch (Exception e) {
        System.out.println(e);
      } finally {
        lock.unlock();
      }
    }

    public void innerMethod() {
      try {
        lock.lock();
        System.out.println("In inner method");
        outerMethod();
      } catch (Exception e) {
        System.out.println(e);
      } finally {
        lock.unlock();
      }
    }
  }
}
