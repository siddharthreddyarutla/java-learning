package com.example.javaLearning.multiThreading.threadPriority;


import java.util.stream.IntStream;

public class ThreadPriorityClass {

  public static void main(String[] args) {
    /**
     * Thread priority
     */
    if (false) {
      ThreadPriority threadPriority = new ThreadPriority("Low priority Thread");
      threadPriority.setPriority(Thread.MIN_PRIORITY);
      threadPriority.start();

      ThreadPriority threadPriority1 = new ThreadPriority("Max priority Thread");
      threadPriority1.setPriority(Thread.MAX_PRIORITY);
      threadPriority1.start();

      ThreadPriority threadPriority2 = new ThreadPriority("Normal priority Thread");
      threadPriority2.setPriority(Thread.NORM_PRIORITY);
      threadPriority2.start();
    }


    // Thread interrupt - It says stop doing the current thread
    if (false) {
      Thread thread = new Thread(new ThreadInterrupted());
      thread.start();
      thread.interrupt();
    }

    // Thread yield - It says give CPU thread time to others as well
    if (false) {
      ThreadYield threadYield = new ThreadYield("t1");
      threadYield.start();
      ThreadYield threadYield1 = new ThreadYield("t2");
      threadYield1.start();
    }

    if (false) {
      ThreadJoin threadJoin = new ThreadJoin("Thread join");
      threadJoin.start();
      try {
        /* Thread join will make sure the current thread will until threadJoin is completed,
         Here current thread is Main */
        threadJoin.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println(e);
      }
      System.out.println("Thread - " + Thread.currentThread().getName());
    }

    /**
     * Daemon threads are background threads in java, they are different from user created threads
     * We can set thread to daemon if we set once main is done with impl thread will be terminated
     */
    if (true) {
      ThreadDaeman threadDaeman = new ThreadDaeman();
      threadDaeman.setDaemon(true);
      threadDaeman.start();
      System.out.println("Main is Done");
    }
  }

  public static class ThreadPriority extends Thread {

    public ThreadPriority(String name) {
      super(name);
    }

    @Override
    public void run() {
      IntStream.range(0, 5).forEach(i -> {
        System.out.println(
            Thread.currentThread().getName() + " Priority - " + Thread.currentThread().getPriority()
                + " Count - " + i);
      });
    }
  }


  public static class ThreadInterrupted implements Runnable {

    @Override
    public void run() {
      IntStream.range(0, 5).forEach(i -> {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          System.out.println("Thread is interrupted" + e);
        }
        System.out.println(Thread.currentThread().getName() + " count " + i);
      });
    }
  }


  public static class ThreadYield extends Thread {
    public ThreadYield(String name) {
      super(name);
    }

    @Override
    public void run() {
      IntStream.range(0, 5).forEach(i -> {
        System.out.println(Thread.currentThread().getName() + " count " + i);
        Thread.yield();
      });
    }
  }


  public static class ThreadJoin extends Thread {
    public ThreadJoin(String name) {
      super(name);
    }

    @Override
    public void run() {
      for (int i = 0; i < 10000; i++) {
        System.out.println(Thread.currentThread().getName());
      }
    }
  }


  public static class ThreadDaeman extends Thread {
    @Override
    public void run() {
      while (true) {
        System.out.println(Thread.currentThread().getName());
      }
    }
  }
}
