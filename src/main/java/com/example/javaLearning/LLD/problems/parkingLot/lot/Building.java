package com.example.javaLearning.LLD.problems.parkingLot.lot;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Spot;
import com.example.javaLearning.LLD.problems.parkingLot.entity.Ticket;
import com.example.javaLearning.LLD.problems.parkingLot.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class Building {

  public List<Level> levelList;

  public Ticket allocate(Vehicle vehicle) {
    for (Level level : levelList) {
      if (level.hasAvailability(vehicle)) {
        log.info("Building: Got free level: {}", level.getNumber());
        Spot spot = level.bookSlot(vehicle);
        return new Ticket(vehicle, level, spot, System.currentTimeMillis());
      }
    }

    log.info("Building: No slot available in building");
    return null;
  }

  public void release(Ticket ticket) {
    if (null == ticket) {
      log.info("Invalid ticket details");
    }
    log.info("Building: Got ticket detail to un park: {}", ticket.toString());
    ticket.getLevel().freeSlot(ticket.getVehicle(), ticket.getSpot());
  }
}
