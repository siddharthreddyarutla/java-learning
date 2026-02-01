package com.example.javaLearning.LLD.problems.parkingLot.lot;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Ticket;
import com.example.javaLearning.LLD.problems.parkingLot.entity.Vehicle;
import com.example.javaLearning.LLD.problems.parkingLot.payment.Payment;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ParkingLot {

  private EntranceGate entranceGate;
  private Building building;
  private ExitGate exitGate;

  public Ticket vehicleArrives(Vehicle vehicle) {
    return entranceGate.enter(building, vehicle);
  }

  public void vehicleExits(Ticket ticket, Payment payment) {
    Boolean isPaymentSuccess = exitGate.CompleteExit(ticket, payment);
    if (isPaymentSuccess) {
      building.release(ticket);
    }
  }
}
