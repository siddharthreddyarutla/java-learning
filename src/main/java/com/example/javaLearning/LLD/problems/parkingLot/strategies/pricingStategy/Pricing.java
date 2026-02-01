package com.example.javaLearning.LLD.problems.parkingLot.strategies.pricingStategy;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Ticket;

public interface Pricing {

  Double calculate(Ticket ticket);
}
