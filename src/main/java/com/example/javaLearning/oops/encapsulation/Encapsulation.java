package com.example.javaLearning.oops.encapsulation;

public class Encapsulation {

  public static void main(String[] args) {

    Account account = new Account(1000L);

    System.out.println("Current balance: " + account.balance);

    account.deposit(100L);

    System.out.println("Current balance post deposit: " + account.balance);

    account.withdraw(1000L);

    System.out.println("Current balance post withdraw: " + account.balance);

    account.withdraw(1000L);
  }

  public static class Account {

    // Encapsulation using private keyword
    private Long balance;

    public Account(Long balance) {
      this.balance = balance;
    }

    public void deposit(Long amount) {
      if (amount > 0) {
        this.balance += amount;
      }
    }

    public void withdraw(Long amount) {
      if (balance > amount) {
        this.balance -= amount;

      } else {
        System.out.println("low balance, current balance is: " + this.balance);
      }
    }
  }
}
