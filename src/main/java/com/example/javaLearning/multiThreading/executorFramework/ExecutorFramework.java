package com.example.javaLearning.multiThreading.executorFramework;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ExecutorFramework {

  public static void main(String[] args) {

    ExecutorFrameworkExample executorFrameworkExample = new ExecutorFrameworkExample();

    // implementing one by one with single thread execution
    executorFrameworkExample.SynchronousImpl();

    // Implementing multi threading by creating and managing threads
    executorFrameworkExample.multiThreading();

    // Implementing multi threading using executors framework
    executorFrameworkExample.multiThreadingUsingExecutorFramework();
  }

  public static class ExecutorFrameworkExample {

    public void SynchronousImpl() {

      long startTime = System.currentTimeMillis();
      System.out.println("Start time: " + startTime);
      IntStream.range(1, 10).forEach(i -> System.out.println(factorial(i)));
      System.out.println("Time taken: " + (System.currentTimeMillis() - startTime));
    }

    public void multiThreading() {

      long startTime = System.currentTimeMillis();
      System.out.println("Start time: " + startTime);

      Thread[] threads = new Thread[9];
      IntStream.range(1, 10).forEach(i -> {
        threads[i - 1] = new Thread(() -> {
          System.out.println(factorial(i));
        });
        threads[i - 1].start();
      });

      for (Thread thread : threads) {
        try {
          thread.join();
        } catch (InterruptedException e) {
          Thread.interrupted();
          System.out.println(e);
        }
      }
      System.out.println("Time taken: " + (System.currentTimeMillis() - startTime));
    }

    public void multiThreadingUsingExecutorFramework() {
      long startTime = System.currentTimeMillis();

      ExecutorService executorService = Executors.newFixedThreadPool(9);
      IntStream.range(1, 10).forEach(i -> {
        executorService.submit(() -> {
          System.out.println(factorial(i));
        });
      });
      executorService.shutdown();

      // One it is shutdown cannot assign new tasks
      //      executorService.submit(() -> {
      //        factorial(1);
      //      });

      System.out.println("Total time taken  " + (System.currentTimeMillis() - startTime));
    }
  }

  public static long factorial(int n) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.interrupted();
      System.out.println(e);
    }
    long result = 1;
    while (n > 0) {
      result *= n;
      n--;
    }
    return result;
  }
}
