package com.example.javaLearning.multiThreading.synchronizationTools.CyclicBarrier;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {

  public static void main(String[] args) {

    CyclicBarrierDemo cyclicBarrierDemo = new CyclicBarrierDemo();
    cyclicBarrierDemo.execute();
  }


  public static class CyclicBarrierDemo {

    public void execute() {
      int players = 3;
      CyclicBarrier barrier =
          new CyclicBarrier(players, () -> System.out.println("All players ready! Game starts."));

      Runnable player = () -> {
        try {
          System.out.println(Thread.currentThread().getName() + " is ready");
          Thread.sleep(10000);
          barrier.await(); // wait for others
          System.out.println(Thread.currentThread().getName() + " starts playing");
        } catch (Exception e) {
          System.out.println(e);
        }
      };

      for (int i = 1; i <= players; i++) {
        new Thread(player, "Player " + i).start();
      }
    }
  }

}
