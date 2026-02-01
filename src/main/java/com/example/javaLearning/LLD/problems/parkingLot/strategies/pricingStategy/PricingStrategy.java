package com.example.javaLearning.LLD.problems.parkingLot.strategies.pricingStategy;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Ticket;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PricingStrategy {

  private Pricing pricing;

  public Double calculate(Ticket ticket) {
    return pricing.calculate(ticket);
  }
}
