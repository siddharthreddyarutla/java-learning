package com.example.javaLearning.multiThreading.locking;

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
     * Here outermethod is calling innermethod but lock is acquired by outermethod where
     * innermethod will
     * for outermethod to complete to acquire lock but at same time outermethod waits until
     * innermethod to complete
     * to release lock this case is Deadlock but Reentranlock will be re entered again but
     * unlocked the same no of times
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
      } catch (Exception e) {
        System.out.println(e);
      } finally {
        lock.unlock();
      }
    }
  }
}
