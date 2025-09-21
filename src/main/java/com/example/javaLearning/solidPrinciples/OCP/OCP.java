package com.example.javaLearning.solidPrinciples.OCP;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OCP {

  public static void main(String[] args) {

    PaymentProcessor paymentProcessor = new PaymentProcessor();
    System.out.println("Violation of OCP principle");
    paymentProcessor.processPayment(PaymentType.CREDIT_CARD);
    paymentProcessor.processPayment(PaymentType.CASH);

    System.out.println();
    System.out.println("OCP principle");

    PaymentProcessorOCP paymentProcessorOCP = new PaymentProcessorOCP();
    paymentProcessorOCP.processPayment(PaymentType.UPI);
    paymentProcessorOCP.processPayment(PaymentType.DEBIT_CARD);
  }

  public static enum PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    CASH,
    UPI
  }


  public static class PaymentProcessor {

    public void processPayment(PaymentType paymentType) {
      if (PaymentType.CREDIT_CARD.equals(paymentType)) {
        System.out.println("credit card");
      } else if (PaymentType.DEBIT_CARD.equals(paymentType)) {
        System.out.println("debit card");
      } else if (PaymentType.CASH.equals(paymentType)) {
        System.out.println("cash");
      } else if (PaymentType.UPI.equals(paymentType)) {
        System.out.println("UPI");
      }
    }
  }


  public static class PaymentProcessorOCP {

    private static final Map<String, Payment> PAYMENT_TYPE_AND_IMPL_MAP = new ConcurrentHashMap<>();

    static {
      PAYMENT_TYPE_AND_IMPL_MAP.put(PaymentType.CASH.name(), new Cash());
      PAYMENT_TYPE_AND_IMPL_MAP.put(PaymentType.DEBIT_CARD.name(), new DebitCard());
      PAYMENT_TYPE_AND_IMPL_MAP.put(PaymentType.CREDIT_CARD.name(), new CreditCard());
      PAYMENT_TYPE_AND_IMPL_MAP.put(PaymentType.UPI.name(), new UPI());
    }

    public void processPayment(PaymentType paymentType) {

      // Can use factory but violates the SRP principle
      //      Payment payment = PaymentFactory.getHandler(paymentType);

      // Using static map to get the impl based on the payment type
      Payment payment = PAYMENT_TYPE_AND_IMPL_MAP.get(paymentType.name());
      payment.makePayment();
      payment.generateReceipt(paymentType);
    }
  }


  public interface Payment {

    void makePayment();

    void generateReceipt(PaymentType paymentType);
  }


  public static class CreditCard implements Payment {

    @Override
    public void makePayment() {
      System.out.println("Made payment using credit card");
    }

    @Override
    public void generateReceipt(PaymentType paymentType) {
      System.out.println("Generated receipt for: " + paymentType);
    }
  }


  public static class Cash implements Payment {

    @Override
    public void makePayment() {
      System.out.println("Made payment using Cash");
    }

    @Override
    public void generateReceipt(PaymentType paymentType) {
      System.out.println("Generated receipt for: " + paymentType);
    }
  }


  public static class DebitCard implements Payment {

    @Override
    public void makePayment() {
      System.out.println("Made payment using debit card");
    }

    @Override
    public void generateReceipt(PaymentType paymentType) {
      System.out.println("Generated receipt for: " + paymentType);
    }
  }


  public static class UPI implements Payment {

    @Override
    public void makePayment() {
      System.out.println("Made payment using UPI");
    }

    @Override
    public void generateReceipt(PaymentType paymentType) {
      System.out.println("Generated receipt for: " + paymentType);
    }
  }


  public static class PaymentFactory {

    public static Payment getHandler(PaymentType paymentType) {
      return switch (paymentType) {
        case CREDIT_CARD -> new CreditCard();
        case DEBIT_CARD -> new DebitCard();
        case CASH -> new Cash();
        case UPI -> new UPI();
      };
    }
  }
}
