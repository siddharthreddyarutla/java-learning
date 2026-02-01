package com.example.javaLearning.LLD.problems.parkingLot.levelFactory;

import com.example.javaLearning.LLD.problems.parkingLot.spotManager.SpotManager;
import com.example.javaLearning.LLD.problems.parkingLot.spotManager.TwoWheelerSpotManager;

public class TwoWheelerLevelFactory implements LevelFactory {
  @Override
  public SpotManager getSportManager() {
    return new TwoWheelerSpotManager();
  }
}
