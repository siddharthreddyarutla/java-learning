package com.example.javaLearning.multiThreading.completableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureImpl {

  public static void main(String[] args) {

    CompletableFutureImplExample completableFutureImplExample = new CompletableFutureImplExample();
    //    completableFutureImplExample.runAsyncDaemon();

    System.out.println("--------------------");

    // Here run async accepts runnable which doesn't results any thing
    //    completableFutureImplExample.runAsyncCustomExecutor();

    System.out.println("--------------------");

    //    completableFutureImplExample.supplyAsyncThenAccept();

    System.out.println("--------------------");

    //    completableFutureImplExample.supplyAsyncThenApply();

    System.out.println("--------------------");

//    completableFutureImplExample.supplyAsyncThenCombine();

    System.out.println("--------------------");

    completableFutureImplExample.supplyAsyncAllOf();
  }

  public static class CompletableFutureImplExample {

    /**
     * Just supplyAsync is async which doesn't block main thread Uses ForkJoinPool if not given own
     * executor and ForkJoinPool threads are daemon threads JVM will terminates before execution
     * inside run async
     */
    public void runAsyncDaemon() {

      CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
        try {
          Thread.sleep(5000);
          System.out.println(
              "Worker/User thread is working......" + Thread.currentThread().getName());
        } catch (Exception e) {
          System.out.println(e);
        }
      });

      // Adding main thread to sleep to simulate work as to check above 5sec async to complete
      try {
        System.out.println("Main is doing complex task " + Thread.currentThread().getName());
        Thread.sleep(6000);
        System.out.println("Main is done with the task " + Thread.currentThread().getName());
      } catch (Exception e) {
        System.out.println(e);
      }

      System.out.println("Main is completed... by " + Thread.currentThread().getName());

      // Output of above
      /*
      Main is doing complex task main
      Worker/User thread is working......ForkJoinPool.commonPool-worker-1
      Main is done with the task main
      Main is completed... by main
      */
    }

    /**
     * Passing custom executor to runAsync then it treats as user threads JVM will be alive
     */
    public void runAsyncCustomExecutor() {

      ExecutorService executorService = Executors.newFixedThreadPool(1);

      CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
        try {
          Thread.sleep(5000);
          System.out.println(
              "Worker/User thread is working......" + Thread.currentThread().getName());
        } catch (Exception e) {
          System.out.println(e);
        }
      }, executorService).thenAccept(result -> System.out.println(result));

      System.out.println("Main is completed... by " + Thread.currentThread().getName());

      executorService.shutdown();
    }

    /**
     * Here .get is a blocks the main thread which implements sequentially and doesn't achieve
     * async here preferred one is just Future
     */
    public void supplyAsyncAndGet() {

      ExecutorService executorService = Executors.newFixedThreadPool(1);

      CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(5000);
          System.out.println(
              "Worker/User thread is working......" + Thread.currentThread().getName());
        } catch (Exception e) {
          System.out.println(e);
        }
        return 10L;
      }, executorService);

      try {
        completableFuture.get();
      } catch (Exception e) {
        System.out.println(e);
      }

      System.out.println("Main is completed... by " + Thread.currentThread().getName());

      executorService.shutdown();
    }


    /**
     * chaining the response of one and another
     */
    public void supplyAsyncThenAccept() {

      ExecutorService executorService = Executors.newFixedThreadPool(1);

      CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(5000);
          System.out.println(
              "Worker/User thread is working......" + Thread.currentThread().getName());
        } catch (Exception e) {
          System.out.println(e);
        }
        return 10L;
      }, executorService);

      // Here main doesn't resume on async call, executor pool thread will be accepting it
      completableFuture.thenAccept(result -> {
        System.out.println(result + " given by thread " + Thread.currentThread().getName());
      });

      System.out.println("Main is completed... by " + Thread.currentThread().getName());

      executorService.shutdown();
    }


    public void supplyAsyncThenApply() {

      ExecutorService executorService = Executors.newFixedThreadPool(1);

      CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(5000);
          System.out.println(
              "Worker/User thread is working......" + Thread.currentThread().getName());
        } catch (Exception e) {
          System.out.println(e);
        }
        return 10L;
      }, executorService);

      // Performing operations async on each execution output
      completableFuture.thenApply(result -> result * 2).thenApply(result -> result / 5)
          .thenAccept(result -> System.out.println(result));
      System.out.println("Main is completed... by " + Thread.currentThread().getName());

      executorService.shutdown();
    }

    public void supplyAsyncThenCombine() {

      ExecutorService executorService = Executors.newFixedThreadPool(1);

      CompletableFuture<Long> completableFuture1 =
          CompletableFuture.supplyAsync(() -> 10L, executorService);
      CompletableFuture<Long> completableFuture2 =
          CompletableFuture.supplyAsync(() -> 20L, executorService);

      CompletableFuture<Long> result =
          completableFuture1.thenCombine(completableFuture2, (a, b) -> a + b);

      result.thenAccept(System.out::println);

      // Performing operations async on each execution output
      System.out.println("Main is completed... by " + Thread.currentThread().getName());

      executorService.shutdown();
    }

    public void supplyAsyncAllOf() {

      ExecutorService executorService = Executors.newFixedThreadPool(1);

      CompletableFuture<Void> result =
          CompletableFuture.allOf(CompletableFuture.supplyAsync(() -> 10L, executorService),
              CompletableFuture.supplyAsync(() -> 10L, executorService),
              CompletableFuture.supplyAsync(() -> 10L, executorService));

      System.out.println(result);

      // Performing operations async on each execution output
      System.out.println("Main is completed... by " + Thread.currentThread().getName());

      executorService.shutdown();
    }
  }
}
