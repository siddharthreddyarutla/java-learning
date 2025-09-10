package com.example.javaLearning.multiThreading.threadSafety;

public class VolatileImpl {

  public static void main(String[] args) {

    SharedResource sharedResource = new SharedResource();

    Thread writerThread = new Thread(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.interrupted();
        System.out.println(e);
      }
      sharedResource.setFlagTrue();
    });
    writerThread.setName("Writer thread");

    Thread readerThread = new Thread(() -> {
      System.out.println(sharedResource.printFlag());
    });
    readerThread.setName("Reader thread");

    writerThread.start();
    readerThread.start();

    try {
      writerThread.join();
      readerThread.join();
    } catch (Exception e) {
      System.out.println(e);
    }

    System.out.println("Main completed the execution");
  }


  public static class SharedResource {

    /**
     * Guarantees visibility: a write to a volatile variable is immediately visible to other
     * threads.
     * <p>
     * Prevents caching/optimizations that would hide updates.
     */
    private volatile boolean flag = false;

    public void setFlagTrue() {
      System.out.println("Writer thread set flag to true " + Thread.currentThread().getName());
      this.flag = true;
    }

    /**
     * Each thread may keep a cached copy of flag in registers or CPU cache.
     * <p>
     * The reader thread may never see the updated value written by the writer thread.
     *
     * @return
     */
    public boolean printFlag() {
      while (!flag) {

      }

      System.out.println("Flag is true! " + Thread.currentThread().getName());
      return flag;
    }
  }
}
