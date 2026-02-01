package com.example.javaLearning.LLD.problems.parkingLot.lot;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Spot;
import com.example.javaLearning.LLD.problems.parkingLot.entity.Vehicle;
import com.example.javaLearning.LLD.problems.parkingLot.enums.VehicleType;
import com.example.javaLearning.LLD.problems.parkingLot.levelFactory.SpotManagerFactory;
import com.example.javaLearning.LLD.problems.parkingLot.spotManager.SpotManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Level {

  public String number;
  Map<VehicleType, SpotManager> vehicleTypeSpotManagerMap;

  public Boolean hasAvailability(Vehicle vehicle) {

    //    SpotManager spotManager = SpotManagerFactory.getSpotManagerFactory(vehicle
    //    .getVehicleType());
    SpotManager spotManager = vehicleTypeSpotManagerMap.get(vehicle.getVehicleType());

    log.info(
        "Level: Selected spot manager for given vehicle type: {}, number: {} and spot manager: {}",
        vehicle.getVehicleType(), vehicle.getNumber(), spotManager.getClass().getSimpleName());

    Boolean isFree = spotManager.hasFreeSpace();

    if (isFree) {
      log.info("Slot is free in this level: {}", number);
      return isFree;
    }
    log.info("Slots are not available for this level: {}", number);
    return Boolean.FALSE;
  }

  public Spot bookSlot(Vehicle vehicle) {
    //    SpotManager spotManager = SpotManagerFactory.getSpotManagerFactory(vehicle
    //    .getVehicleType());
    SpotManager spotManager = vehicleTypeSpotManagerMap.get(vehicle.getVehicleType());

    log.info(
        "Level: Selected spot manager for given vehicle type : {}, number: {} and spot manager: {}",
        vehicle.getVehicleType(), vehicle.getNumber(), spotManager.getClass().getSimpleName());

    return spotManager.park();
  }

  public void freeSlot(Vehicle vehicle, Spot spot) {
    //    SpotManager spotManager = SpotManagerFactory.getSpotManagerFactory(vehicle
    //    .getVehicleType());
    SpotManager spotManager = vehicleTypeSpotManagerMap.get(vehicle.getVehicleType());

    log.info("Level: Selected spot manager for given vehicle type: {} and spot : {}",
        vehicle.getVehicleType(), spot.getId());

    spotManager.unPark(spot);

    log.info("Level: successfully freed the slot");
  }
}
