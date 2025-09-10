package com.example.javaLearning.multiThreading.executorFramework;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceImpl {

  public static void main(String[] args) {

    ExecutorExample example = new ExecutorExample();

    // Can use runnable when there is no return type is required
    example.executorRunnable();

    System.out.println("--------------");
    // Can use callable if in case of return type is expected
    example.executorCallable();

    System.out.println("--------------");
    example.executorRunnableResponse();

    System.out.println("--------------");
    // list of callable
    example.executorInvokeAll();

    System.out.println("--------------");
    example.executorInvokeAllTimed();

    System.out.println("--------------");
    //    example.scheduledExecutorService();

    System.out.println("--------------");
    example.cachedThreadPool();
  }

  public static class ExecutorExample {

    public void executorRunnable() {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      Runnable runnable = () -> System.out.println("Runnable Hello");
      executorService.submit(runnable);
      executorService.shutdown();
    }

    public void executorCallable() {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      Callable<Integer> callable = () -> 8 + 3;
      Future<Integer> future = executorService.submit(callable);
      try {
        System.out.println(future.get());
      } catch (Exception e) {
        Thread.currentThread().interrupt();
        System.out.println(e);
      }
      executorService.shutdown();
    }

    public void executorRunnableResponse() {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      Runnable runnable = () -> System.out.println("Runnable Hello");
      // Returns Success upon successful completion.
      Future<String> future = executorService.submit(runnable, "Success");
      try {
        System.out.println(future.get());
      } catch (Exception e) {
        Thread.currentThread().interrupt();
        System.out.println(e);
      }
      executorService.shutdown();
    }

    public void executorInvokeAll() {

      Callable<Integer> callable1 = () -> {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " executing Task 1");
        return 1;
      };

      Callable<Integer> callable2 = () -> {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " executing Task 2");
        return 2;
      };

      Callable<Integer> callable3 = () -> {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " executing Task 3");
        return 3;
      };

      List<Callable<Integer>> callableList = List.of(callable1, callable2, callable3);

      ExecutorService executorService = Executors.newFixedThreadPool(3);
      List<Future<Integer>> futureList = List.of();
      try {
        futureList = executorService.invokeAll(callableList);
      } catch (Exception e) {
        System.out.println(e);
      }

      try {
        for (Future<Integer> future : futureList) {
          System.out.println(future.get());
        }
      } catch (Exception e) {
        System.out.println(e);
      }
      System.out.println("Task completed");
      executorService.shutdown();
    }


    public void executorInvokeAllTimed() {

      Callable<Integer> callable1 = () -> {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " executing Task 1");
        return 1;
      };

      Callable<Integer> callable2 = () -> {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " executing Task 2");
        return 2;
      };

      Callable<Integer> callable3 = () -> {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " executing Task 3");
        return 3;
      };

      List<Callable<Integer>> callableList = List.of(callable1, callable2, callable3);

      ExecutorService executorService = Executors.newFixedThreadPool(2);
      List<Future<Integer>> futureList = List.of();
      try {
        futureList = executorService.invokeAll(callableList, 1, TimeUnit.SECONDS);
      } catch (CancellationException e) {
        System.out.println("Exception due to: " + e);
      } catch (Exception e) {
        System.out.println(e);
      }

      try {
        for (Future<Integer> future : futureList) {
          System.out.println(future.get());
        }
      } catch (Exception e) {
        System.out.println(e);
      }
      System.out.println("Task completed");
      executorService.shutdown();
    }

    public void scheduledExecutorService() {

      ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

      // Executes at 5 seconds
      scheduledExecutorService.schedule(() -> System.out.println("Executing task...."), 5,
          TimeUnit.SECONDS);

      // It executes the tasks with fixed rate every 5 seconds with 5 seconds initial delay
      scheduledExecutorService.scheduleAtFixedRate(
          () -> System.out.println("Executing task with fixed rate"), 5, 5, TimeUnit.SECONDS);

      scheduledExecutorService.scheduleWithFixedDelay(
          () -> System.out.println("Executing task with fixed delay"), 5, 5, TimeUnit.SECONDS);

      scheduledExecutorService.schedule(() -> {
        System.out.println("Task waiting to complete fixed rate one");
        scheduledExecutorService.shutdown();
      }, 20, TimeUnit.SECONDS);
    }

    public void cachedThreadPool() {

      ExecutorService executorService = Executors.newCachedThreadPool();
      Callable<Integer> callable1 = () -> {
        Thread.sleep(60000);
        System.out.println(Thread.currentThread().getName() + " executing Task 1");
        return 1;
      };

      Callable<Integer> callable2 = () -> {
        Thread.sleep(10000);
        System.out.println(Thread.currentThread().getName() + " executing Task 2");
        return 2;
      };

      List<Callable<Integer>> callableList = List.of(callable1, callable2);
      try {
        executorService.invokeAll(callableList);
      } catch (Exception e) {
        System.out.println(e);
      }
      executorService.shutdown();
    }
  }
}
