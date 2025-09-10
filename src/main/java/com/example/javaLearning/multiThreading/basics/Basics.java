package com.example.javaLearning.multiThreading.basics;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Basics {

  public static void main(String[] args) {
    System.out.println(Thread.currentThread().getName());

    // Creating instance of a class to run thread
    ExtendingThread test = new ExtendingThread();
    test.start();

    // Creating thread object to call
    Thread thread = new Thread(new ImplementingRunnable());
    Thread thread1 = new Thread(new ImplementingRunnable());
    thread.start();
    thread1.start();

    // Callable for return types
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<String> future = executorService.submit(new CallableThread());

    try {
      System.out.println(future.get());
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException executionException) {
      System.out.println(executionException);
    } finally {
      executorService.shutdown();
    }

    // Lambda thread
    LamdaThread lamdaThread = new LamdaThread();
    lamdaThread.thread.start();

  }

  /**
   * One Way of creating thread using Extending Thread class
   */
  public static class ExtendingThread extends Thread {

    @Override
    public void run() {
      for (int i = 0; i < 100; i++) {
        System.out.println("Thread class " + Thread.currentThread().getName());
      }
    }
  }


  /**
   * Another way of creating thread by implementing Runnable interface
   * For this Need to create instance of the class passing to Thread default constructor
   * 1. obj instance
   * 2. Thread instance
   * 3. Then start / run
   */
  public static class ImplementingRunnable implements Runnable {

    @Override
    public void run() {
      for (int i = 0; i < 100; i++) {
        System.out.println("Runnable thread " + Thread.currentThread().getName());
      }
    }
  }


  public static class CallableThread implements Callable<String> {

    @Override
    public String call() throws Exception {
      return "Callable Thread " + Thread.currentThread().getName();
    }
  }


  public static class LamdaThread {

    /* Here Thread implements runnable interface which is functional interface where lamda
     expression can be used here */

    Thread thread = new Thread(() -> {
      System.out.println("Lamda Thread " + Thread.currentThread().getName());
    });
  }
}
