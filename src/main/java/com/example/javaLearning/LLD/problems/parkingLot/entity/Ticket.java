package com.example.javaLearning.LLD.problems.parkingLot.entity;

import com.example.javaLearning.LLD.problems.parkingLot.lot.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Ticket {

  public Vehicle vehicle;
  public Level level;
  public Spot spot;
  public Long entryTime;

  @Override
  public String toString() {
    return "Ticket{" + "vehicle=" + vehicle + ", level=" + level + ", spot=" + spot + ", entryTime="
        + entryTime + '}';
  }
}
