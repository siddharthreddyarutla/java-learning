package com.example.javaLearning.LLD.problems.parkingLot.lot;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Ticket;
import com.example.javaLearning.LLD.problems.parkingLot.payment.Payment;
import com.example.javaLearning.LLD.problems.parkingLot.strategies.pricingStategy.PricingStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ExitGate {

  private PricingStrategy pricingStrategy;

  public boolean CompleteExit(Ticket ticket , Payment payment) {
    Double amount = pricingStrategy.calculate(ticket);

    Boolean paymentStatus = payment.pay(amount);
    if (paymentStatus) {
      log.info("Payment successful completing exit");
    }
    return paymentStatus;
  }
}
