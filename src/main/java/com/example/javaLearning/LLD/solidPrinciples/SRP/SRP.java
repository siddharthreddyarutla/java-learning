package com.example.javaLearning.LLD.solidPrinciples.SRP;

import lombok.RequiredArgsConstructor;

public class SRP {


  public static void main(String[] args) {

    System.out.println("Violating single responsibility principle");
    Restaurant restaurant = new Restaurant();
    restaurant.cashier();
    restaurant.chef();
    restaurant.waiter();
    restaurant.cleaner();

    System.out.println("------------------------------");
    System.out.println("Using single responsibility principle");

    RestaurantSRP restaurantSRP =
        new RestaurantSRP(new Chiefs(), new CleanerClass(), new CashierClass(), new Waiter());
    restaurantSRP.runRestaurant();
  }

  public static class Restaurant {

    public void chef() {
      System.out.println("Cooks food");
    }

    public void waiter() {
      System.out.println("Serves food");
    }

    public void cashier() {
      System.out.println("Takes orders and billing");
    }

    public void cleaner() {
      System.out.println("Cleans the tables after each meal");
    }
  }


  @RequiredArgsConstructor
  public static class RestaurantSRP {

    private final Chiefs chiefs;
    private final Cleaner cleaner;
    private final CashierClass cashierClass;
    private final Waiter waiter;

    /* Here if any change needs to be done in cashier section like introducing digital platform
     only cashier class needs to be changes not other classes which de-couples the dependency
     on a single class */
    public void runRestaurant() {
      cashierClass.createOrder();
      cashierClass.generateBill();
      cashierClass.collectPayment();
      chiefs.receiveOrder();
      chiefs.cooks();
      waiter.server();
      cleaner.clean();
    }
  }


  public interface Chief {

    void receiveOrder();

    void cooks();
  }


  public static class Chiefs implements Chief {

    @Override
    public void receiveOrder() {
      System.out.println("chief received order");
    }

    @Override
    public void cooks() {
      System.out.println("Chief cooks food");
    }
  }


  public static class Waiter {

    public void server() {
      System.out.println("Waiter servers");
    }
  }


  public interface Cashier {

    void createOrder();

    void generateBill();

    void collectPayment();
  }


  public static class CashierClass implements Cashier {

    @Override
    public void createOrder() {
      System.out.println("Taken order");
    }

    @Override
    public void generateBill() {
      System.out.println("Generates bill");
    }

    @Override
    public void collectPayment() {
      System.out.println("Collecting payment");
    }
  }


  public interface Cleaner {
    void clean();
  }


  public static class CleanerClass implements Cleaner {

    public void clean() {
      System.out.println("Cleaner cleans");
    }
  }
}
