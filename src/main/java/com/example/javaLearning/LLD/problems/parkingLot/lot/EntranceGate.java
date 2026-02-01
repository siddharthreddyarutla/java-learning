package com.example.javaLearning.LLD.problems.parkingLot.lot;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Ticket;
import com.example.javaLearning.LLD.problems.parkingLot.entity.Vehicle;

public class EntranceGate {

  public Ticket enter(Building building, Vehicle vehicle) {
    return building.allocate(vehicle);
  }
}
