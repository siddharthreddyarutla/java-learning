package com.example.javaLearning.LLD.problems.parkingLot.strategies.pricingStategy;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Ticket;

public class FixedPricing implements Pricing {
  @Override
  public Double calculate(Ticket ticket) {
    return 100D;
  }
}
