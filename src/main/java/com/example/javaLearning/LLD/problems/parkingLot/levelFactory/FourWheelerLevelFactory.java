package com.example.javaLearning.LLD.problems.parkingLot.levelFactory;

import com.example.javaLearning.LLD.problems.parkingLot.spotManager.FourWheelerSpotManager;
import com.example.javaLearning.LLD.problems.parkingLot.spotManager.SpotManager;

public class FourWheelerLevelFactory implements LevelFactory{
  @Override
  public SpotManager getSportManager() {
    return new FourWheelerSpotManager();
  }
}
