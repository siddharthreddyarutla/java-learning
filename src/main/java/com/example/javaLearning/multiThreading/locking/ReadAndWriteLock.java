package com.example.javaLearning.multiThreading.locking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadAndWriteLock {

  public static void main(String[] args) {

    ReadAndWriteLockExample readAndWriteLockExample = new ReadAndWriteLockExample();

    Runnable readTask = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 10; i++) {
          System.out.println(Thread.currentThread().getName() + " reading value "
              + readAndWriteLockExample.read());
        }
      }
    };

    Runnable writeTask = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 10; i++) {
          readAndWriteLockExample.write();
          System.out.println(Thread.currentThread().getName() + " incremented");
        }
      }
    };

    Thread thread = new Thread(readTask, "Thread read 1");
    Thread thread2 = new Thread(readTask, "Thread read 2");
    Thread thread3 = new Thread(writeTask, "Thread write");

    thread.start();
    thread2.start();
    thread3.start();

    try {
      thread.join();
      thread2.join();
      thread3.join();
    } catch (Exception e) {
      System.out.println(e);
    }

    System.out.println("Task is completed " + Thread.currentThread().getName());
  }


  public static class ReadAndWriteLockExample {

    private int counter = 0;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    public void write() {
      writeLock.lock();
      try {
        //        System.out.println(Thread.currentThread().getName() + " Acquired write lock ");
        counter++;
        Thread.sleep(50); // Just telling OS to give time for other threads as well
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println(e);
      } finally {
        writeLock.unlock();
      }
    }

    public int read() {
      readLock.lock();
      try {
        //        System.out.println(Thread.currentThread().getName() + " Acquired read lock ");
        return counter;
      } finally {
        readLock.unlock();
      }
    }
  }
}
