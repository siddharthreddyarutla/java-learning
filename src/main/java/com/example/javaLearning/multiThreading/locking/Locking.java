package com.example.javaLearning.multiThreading.locking;


public class Locking {


  public static void main(String[] args) throws InterruptedException {

    BankAccount bankAccount = new BankAccount();

    Runnable taskUsingIntrinsic = new Runnable() {
      @Override
      public void run() {
        bankAccount.withdraw(500);
      }
    };


    // Intrinsic locking
    Thread thread1 = new Thread(taskUsingIntrinsic, "Person 1");
    Thread thread2 = new Thread(taskUsingIntrinsic, "Person 2");

    thread1.start();
    thread2.start();

  }

  public static class BankAccount {

    private int balance = 1000;

    /* Draw back of this synchronized is if the task done by method is huge the other thread
    needs to wait for long time, and this is intrinsic locking */
    public synchronized void withdraw(int amount) {
      System.out.println(
          Thread.currentThread().getName() + " attempting to withdraw money of " + amount);

      if (balance >= amount) {
        System.out.println(
            Thread.currentThread().getName() + " proceeding with the withdraw " + amount);

        try {
          // 10 sec work simulation
          Thread.sleep(10000);
        } catch (InterruptedException e) {

        }
        balance -= amount;
        System.out.println(
            Thread.currentThread().getName() + " Successfully withdrawed amount available balance: "
                + balance);
      } else {
        System.out.println(Thread.currentThread().getName() + " Insufficient balance");
      }
    }
  }
}
