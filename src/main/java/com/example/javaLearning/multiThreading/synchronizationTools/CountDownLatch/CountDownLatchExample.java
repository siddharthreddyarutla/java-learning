package com.example.javaLearning.multiThreading.synchronizationTools.CountDownLatch;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CountDownLatchExample {

  public static void main(String[] args) {

    CustomTaskExecutor customTaskExecutor = new CustomTaskExecutor();
    customTaskExecutor.execute();

    CustomTaskExecutorWithCountDownLatch countDownLatch =
        new CustomTaskExecutorWithCountDownLatch();
    countDownLatch.execute();
  }

  public static class CustomTaskExecutor {

    public void execute() {
      ExecutorService executorService = Executors.newFixedThreadPool(3);

      long startTime = System.currentTimeMillis();

      Future<String> future1 = executorService.submit(new CustomTaskCallable());
      Future<String> future2 = executorService.submit(new CustomTaskCallable());
      Future<String> future3 = executorService.submit(new CustomTaskCallable());

      try {
        future1.get();
        future2.get();
        future3.get();
      } catch (Exception e) {
        System.out.println(e);
      }
      System.out.println("time taken: " + (System.currentTimeMillis() - startTime));
      System.out.println("All dependent services are started, Starting the main service");
      executorService.shutdown();
    }
  }


  public static class CustomTaskCallable implements Callable<String> {

    @Override
    public String call() throws Exception {

      Thread.sleep(2000);
      System.out.println(Thread.currentThread().getName() + " starting service....");
      return "OK";
    }
  }


  public static class CustomTaskExecutorWithCountDownLatch {

    public void execute() {

      int executionCount = 3;

      ExecutorService executorService = Executors.newFixedThreadPool(executionCount);

      CountDownLatch countDownLatch = new CountDownLatch(executionCount);

      long startTime = System.currentTimeMillis();

      executorService.submit(new CustomCountDownLatchTaskCallable(countDownLatch));
      executorService.submit(new CustomCountDownLatchTaskCallable(countDownLatch));
      executorService.submit(new CustomCountDownLatchTaskCallable(countDownLatch));

      try {
        countDownLatch.await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println(e);
      }

      System.out.println("time taken: " + (System.currentTimeMillis() - startTime));
      System.out.println("All dependent services are started, Starting the main service");
      executorService.shutdown();
    }
  }


  public static class CustomCountDownLatchTaskCallable implements Callable<String> {

    private final CountDownLatch countDownLatch;

    public CustomCountDownLatchTaskCallable(CountDownLatch countDownLatch) {
      this.countDownLatch = countDownLatch;
    }

    @Override
    public String call() throws Exception {

      try {
        Thread.sleep(2000);
        System.out.println(Thread.currentThread().getName() + " starting service....");
      } catch (Exception e) {
        System.out.println(e);
      } finally {
        countDownLatch.countDown();
      }
      return "OK";
    }
  }
}
