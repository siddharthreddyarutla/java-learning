package com.example.javaLearning.LLD.problems.parkingLot.payment;

public class CardPayment implements Payment {
  @Override
  public Boolean pay(Double amount) {
    System.out.println("Card paid: " + amount);
    return true;
  }
}
