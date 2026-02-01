package com.example.javaLearning.LLD.problems.parkingLot.payment;

public class UPIPayment implements Payment {
  @Override
  public Boolean pay(Double amount) {
    System.out.println("UPI paid: " + amount);
    return true;
  }
}
