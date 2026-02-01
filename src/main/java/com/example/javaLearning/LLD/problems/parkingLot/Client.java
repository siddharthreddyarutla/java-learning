package com.example.javaLearning.LLD.problems.parkingLot;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Spot;
import com.example.javaLearning.LLD.problems.parkingLot.entity.Ticket;
import com.example.javaLearning.LLD.problems.parkingLot.entity.Vehicle;
import com.example.javaLearning.LLD.problems.parkingLot.enums.VehicleType;
import com.example.javaLearning.LLD.problems.parkingLot.lot.Building;
import com.example.javaLearning.LLD.problems.parkingLot.lot.EntranceGate;
import com.example.javaLearning.LLD.problems.parkingLot.lot.ExitGate;
import com.example.javaLearning.LLD.problems.parkingLot.lot.Level;
import com.example.javaLearning.LLD.problems.parkingLot.lot.ParkingLot;
import com.example.javaLearning.LLD.problems.parkingLot.payment.UPIPayment;
import com.example.javaLearning.LLD.problems.parkingLot.spotManager.SpotManager;
import com.example.javaLearning.LLD.problems.parkingLot.spotManager.TwoWheelerSpotManager;
import com.example.javaLearning.LLD.problems.parkingLot.strategies.pricingStategy.FixedPricing;
import com.example.javaLearning.LLD.problems.parkingLot.strategies.pricingStategy.PricingStrategy;
import com.example.javaLearning.LLD.problems.parkingLot.strategies.spotSelecterStrategy.RandomSpotSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Client {

  private static final Logger log = LoggerFactory.getLogger(Client.class);

  public static void main(String[] args) {

    Vehicle vehicle = new Vehicle(VehicleType.BIKE, "TS30J8383");
    Spot spot1 = new Spot("L0-S1", Boolean.TRUE);
    Spot spot2 = new Spot("L0-S2", Boolean.TRUE);
    List<Spot> spotList = new ArrayList<>();
    spotList.add(spot1);
    spotList.add(spot2);

    SpotManager twoWheelerSpotManager =
        new TwoWheelerSpotManager(spotList, new RandomSpotSelector(), new ReentrantLock());

    Level level = new Level("L0", Map.of(VehicleType.BIKE, twoWheelerSpotManager));
    Level level1 = new Level("L1", Map.of(VehicleType.BIKE, twoWheelerSpotManager));
    List<Level> levelList = new ArrayList<>();
    levelList.add(level);
    levelList.add(level1);


    Building building = new Building(levelList);

    ParkingLot parkingLot = new ParkingLot(new EntranceGate(), building,
        new ExitGate(new PricingStrategy(new FixedPricing())));

    Ticket ticket = parkingLot.vehicleArrives(vehicle);

    if (null != ticket) {
      log.info("Ticket generated: {}", ticket.toString());
      parkingLot.vehicleExits(ticket, new UPIPayment());
    } else {
      log.info("Building is full");
    }
  }
}
