package com.example.javaLearning.multiThreading.basics_0;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Revision {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    MyThread myThread = new MyThread();
    myThread.start();

    Thread thread = new Thread(new RunnableThread());
    thread.start();

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future = service.submit(new CallableThread());
    System.out.println(future.get());
    service.shutdown();
  }

  public static class MyThread extends Thread {

    @Override
    public void run() {
      for (int i = 0; i < 5; i++) {
        System.out.println(
            "Thread is running: " + i + " " + "by " + Thread.currentThread().getName());
      }
    }
  }


  public static class RunnableThread implements Runnable {

    @Override
    public void run() {
      for (int i = 0; i < 5; i++) {
        System.out.println(
            "Thread is running by runnable: " + i + " " + "by " + Thread.currentThread().getName());
      }
    }
  }


  public static class CallableThread implements Callable<String> {

    @Override
    public String call() {
      for (int i = 0; i < 5; i++) {
        return "Thread is running by callable: " + i + " " + "by " + Thread.currentThread()
            .getName();
      }
      return "";
    }
  }
}
