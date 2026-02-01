package com.example.javaLearning.LLD.problems.parkingLot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Spot {

  private String id;
  private Boolean isFree;


  public Boolean isSlotAvailable() {
    return isFree;
  }

  public void occupySlot() {
    this.isFree = Boolean.FALSE;
  }

  public void releaseSlot() {
    this.isFree = Boolean.TRUE;
  }
}
