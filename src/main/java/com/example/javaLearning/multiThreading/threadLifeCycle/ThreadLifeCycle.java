package com.example.javaLearning.multiThreading.threadLifeCycle;

public class ThreadLifeCycle {

  public static void main(String[] args) {

    Thread thread = new Thread();
    printThreadState(thread);

    thread.start();
    printThreadState(thread);

    try {
      Thread.sleep(100);
      printThreadState(thread);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.println(e);
    }

    try {
      thread.join();
      printThreadState(thread);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.println(e);
    }
  }

  public static class Thread extends java.lang.Thread {

    @Override
    public void run() {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println(e);
      }
      System.out.println("Thread state - RUNNING  " + Thread.currentThread().getName());
    }
  }

  public static void printThreadState(Thread thread) {
    System.out.println("Thread state - " + thread.getState());
  }
}

