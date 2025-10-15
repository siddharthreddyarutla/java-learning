package com.example.javaLearning.designPatterns.behavioral.state;

public class state {

  public static void main(String[] args) {

    VendingMachineContext vendingMachineContext = new VendingMachineContext();
    vendingMachineContext.dispense();
    vendingMachineContext.insertCoin(10L);
    vendingMachineContext.insertCoin(100L);
    vendingMachineContext.dispense();
    vendingMachineContext.dispense();
  }

  public static class VendingMachineContext {

    private VendingState vendingState;
    private int balance;


    public VendingMachineContext() {
      this.vendingState = new IdleState();
      this.balance = 0;
    }

    public void changeState(VendingState vendingState) {
      this.vendingState = vendingState;
    }

    public void addBalance(Long balance) {
      this.balance += balance;
    }

    public void deductBalance(Long balance) {
      this.balance -= balance;
    }

    public void insertCoin(Long coin) {
      this.vendingState.insertCoin(this, coin);
    }

    public void dispense() {
      this.vendingState.dispense(this);
    }
  }


  public interface VendingState {

    void insertCoin(VendingMachineContext context, Long coin);

    void dispense(VendingMachineContext context);
  }


  public static class IdleState implements VendingState {
    private static final int PRICE = 50;

    @Override
    public void insertCoin(VendingMachineContext context, Long coin) {
      System.out.println("inserting coin");
      if (coin <= 0) {
        System.out.println("No enough balance....");
        return;
      }
      context.addBalance(coin);
      System.out.println("Coin is inserted and current balance is: " + context.balance);
      if (context.balance >= PRICE) {
        context.changeState(new DispenseState());
      }
    }

    @Override
    public void dispense(VendingMachineContext context) {
      System.out.println("Not enough balance. Please insert coin.");
    }
  }


  public static class DispenseState implements VendingState {
    private static final int PRICE = 50;

    @Override
    public void insertCoin(VendingMachineContext context, Long coin) {
      context.addBalance(coin);
      System.out.println("Additional coin accepted. Balance = " + context.balance);
    }

    @Override
    public void dispense(VendingMachineContext context) {
      if (context.balance >= PRICE) {
        System.out.println("Successfully dispensed the item");
        context.deductBalance((long) PRICE);
        System.out.println("Current balance is: " + context.balance);
      }
      // Back to idle state
      context.changeState(new IdleState());
    }
  }
}
