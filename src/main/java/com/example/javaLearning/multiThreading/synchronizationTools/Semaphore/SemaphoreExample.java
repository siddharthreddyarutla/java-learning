package com.example.javaLearning.multiThreading.synchronizationTools.Semaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreExample {

  public static void main(String[] args) {

    List<Callable<String>> callableList = new ArrayList<>();

    /* here there are 5 threads and 5 callables which each thread can pick one but semaphore
    allows only 3 threads will invoke the callable rest 2 will wait until any one is released

     its like at once how many threads can execute the method or impl
     */
    Semaphore semaphore = new Semaphore(3);

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < 5; i++) {
      int finalI = i;
      callableList.add(() -> {
        try {
          semaphore.acquire();
          System.out.println(Thread.currentThread().getName() + " is trying to acquire lock");
          Thread.sleep(10000);
          return "car" + finalI + " starts...";
        } finally {
          semaphore.release();
        }
      });
    }

    ExecutorService executorService = Executors.newFixedThreadPool(5);

    try {
      executorService.invokeAll(callableList);
    } catch (Exception e) {
      System.out.println(e);
    }

    System.out.println(
        "ALl tasks completed.... Time taken: " + (System.currentTimeMillis() - startTime));
    executorService.shutdown();
  }
}
