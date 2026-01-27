package com.example.javaLearning.LLD.designPatterns.behavioral;

public class revision {

  public static void main(String[] args) {

    System.out.println("---------Strategy-------");

    int clientBankBalance = 1000;
    int withdrawAmount = 10001;
    if (clientBankBalance > withdrawAmount) {
      Strategy.Ram client = new Strategy.Ram(new Strategy.UPI());
      client.doPayment();
    } else {
      Strategy.Ram client = new Strategy.Ram(new Strategy.CARD());
      client.doPayment();
    }
  }

  public static class Strategy {

    // Strategy interface
    public interface PaymentStrategy {
      String pay();
    }


    // Concrete strategies
    public static class UPI implements PaymentStrategy {

      @Override
      public String pay() {
        return "Payment is done via UPI";
      }
    }


    public static class CARD implements PaymentStrategy {
      @Override
      public String pay() {
        return "Payment is doe via CARD";
      }
    }


    // Context
    public static class PaymentGateway {

      private static PaymentStrategy paymentStrategy;

      public PaymentGateway(PaymentStrategy paymentStrategy) {
        this.setPaymentStrategy(paymentStrategy);
      }

      public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
      }

      public void doPayment() {
        System.out.println(
            "Payment is done via strategy: " + paymentStrategy.getClass().getSimpleName());
      }
    }


    // Client
    public static class Ram extends PaymentGateway {

      public Ram(PaymentStrategy paymentStrategy) {
        super(paymentStrategy);
      }
    }
  }
}
