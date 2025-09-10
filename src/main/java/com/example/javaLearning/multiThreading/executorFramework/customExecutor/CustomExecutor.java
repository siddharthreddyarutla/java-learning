package com.example.javaLearning.multiThreading.executorFramework.customExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CustomExecutor {

  public static void main(String[] args) {

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    long startTime = System.currentTimeMillis();

    Future<Boolean> future = executorService.submit(new CustomerExecutorTaskCallable(10000));

    try {
      future.get();
    } catch (Exception e) {
      System.out.println(e);
    }

    System.out.println("Task completed, time taken: " + (System.currentTimeMillis() - startTime));

    executorService.shutdown();
  }
}
