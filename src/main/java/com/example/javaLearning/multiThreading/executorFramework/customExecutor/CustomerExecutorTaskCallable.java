package com.example.javaLearning.multiThreading.executorFramework.customExecutor;

import java.util.concurrent.Callable;

public class CustomerExecutorTaskCallable implements Callable<Boolean> {

  private int sleepTime;

  public CustomerExecutorTaskCallable(int sleepTime) {
    this.sleepTime = sleepTime;
  }

  @Override
  public Boolean call() throws Exception {
    System.out.println("Implementing custom task.....");
    Thread.sleep(10000);
    return true;
  }
}
