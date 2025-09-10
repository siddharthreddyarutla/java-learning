package com.example.javaLearning.multiThreading.interThreadCommunication;

public class InterThreadCommunication {

  public static void main(String[] args) throws InterruptedException {

    SharedResource sharedResource = new SharedResource();

    Thread producerThread = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        try {
          sharedResource.produce(i);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          System.out.println(e);
        }
      }
    });

    Thread consumerThread = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        try {
          sharedResource.consume();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          System.out.println(e);
        }
      }
    });

    producerThread.start();
    Thread.sleep(500); // give consumer time to reach wait()
    System.out.println(
        producerThread.getState()); // U can see here producer thread status as 'BLOCKED'
    consumerThread.start();
  }

  public static class SharedResource {

    private int data;

    private boolean hasData;

    public synchronized void produce(int value) throws InterruptedException {

      while (hasData) {
        wait(); // On wait current thread will be
      }
      data = value;
      System.out.println(Thread.currentThread().getName() + " produced data: " + value);
      hasData = true;
      notify();
    }

    public synchronized int consume() throws InterruptedException {

      while (!hasData) {
        wait();
      }
      System.out.println(Thread.currentThread().getName() + " Consumed data: " + data);
      hasData = false;
      notify();
      return data;
    }
  }
}
