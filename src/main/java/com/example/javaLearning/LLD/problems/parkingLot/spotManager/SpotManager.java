package com.example.javaLearning.LLD.problems.parkingLot.spotManager;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Spot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public abstract class SpotManager {

  public abstract Spot park();

  public abstract void unPark(Spot spot);

  public abstract Boolean hasFreeSpace();
}
