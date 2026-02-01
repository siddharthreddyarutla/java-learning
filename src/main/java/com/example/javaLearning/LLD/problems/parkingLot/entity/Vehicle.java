package com.example.javaLearning.LLD.problems.parkingLot.entity;

import com.example.javaLearning.LLD.problems.parkingLot.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

  private VehicleType vehicleType;
  private String number;
}
