package com.example.javaLearning.multiThreading.locking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExtrinsicLocking {

  public static void main(String[] args) {

    BankAccount bankAccount = new BankAccount();
    Runnable taskUsingExtrinsic = new Runnable() {
      @Override
      public void run() {
        bankAccount.withdrawUsingLock(500);
      }
    };

    Thread thread3 = new Thread(taskUsingExtrinsic, "Person 3");
    Thread thread4 = new Thread(taskUsingExtrinsic, "Person 4");
    thread3.start();
    thread4.start();
  }

  public static class BankAccount {
    private int balance = 1000;
    private final Lock lock = new ReentrantLock();

    public void withdrawUsingLock(int amount) {

      System.out.println(
          Thread.currentThread().getName() + " attempting to withdraw money of " + amount);

      try {
        if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
          try {
            if (balance >= amount) {
              System.out.println(
                  Thread.currentThread().getName() + " proceeding with the withdraw " + amount);

              try {
                // 20 sec work simulation
                Thread.sleep(2000);
              } catch (InterruptedException e) {

              }
              balance -= amount;
              System.out.println(Thread.currentThread().getName()
                  + " Successfully withdrawal amount available balance: " + balance);
            } else {
              System.out.println(Thread.currentThread().getName() + " Insufficient balance");
            }
          } catch (Exception e) {
            System.out.println(e);
          }
        } else {
          System.out.println(Thread.currentThread().getName() + " Lock acquisition failed");
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println(e);
      } finally {
        lock.unlock();
      }
    }
  }
}
