package com.example.javaLearning.LLD.problems.parkingLot.levelFactory;

import com.example.javaLearning.LLD.problems.parkingLot.enums.VehicleType;
import com.example.javaLearning.LLD.problems.parkingLot.spotManager.FourWheelerSpotManager;
import com.example.javaLearning.LLD.problems.parkingLot.spotManager.SpotManager;
import com.example.javaLearning.LLD.problems.parkingLot.spotManager.TwoWheelerSpotManager;

public class SpotManagerFactory {

  public static SpotManager getSpotManagerFactory(VehicleType vehicleType) {

    return switch (vehicleType) {
      case BIKE -> new TwoWheelerSpotManager();
      case CAR -> new FourWheelerSpotManager();
    };
  }
}
