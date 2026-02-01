package com.example.javaLearning.LLD.problems.parkingLot.strategies.spotSelecterStrategy;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Spot;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RandomSpotSelector implements SpotSelectorStrategy {

  @Override
  public Spot chooseSpot(List<Spot> spotList) {
    for (Spot spot : spotList) {
      if (spot.isSlotAvailable()) {
        log.info("RandomSpotSelector: Spot is selected randomly and spot is: {}", spot);
        return spot;
      }
    }
    return null;
  }
}
