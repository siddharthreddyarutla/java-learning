package com.example.javaLearning.LLD.problems.parkingLot.strategies.spotSelecterStrategy;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Spot;

import java.util.List;

public interface SpotSelectorStrategy {

  Spot chooseSpot(List<Spot> spotList);
}
