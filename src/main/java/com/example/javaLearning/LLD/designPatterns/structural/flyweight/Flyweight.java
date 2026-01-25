package com.example.javaLearning.LLD.designPatterns.structural.flyweight;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class Flyweight {

  public static void main(String[] args) {

    Vehicle vehicle = VehicleFactory.getVehicle(VehicleType.CAR, "RED", "PETROL");
    Car car = new Car(150L, "BMW", vehicle);
    car.manufacture();

    Vehicle vehicle1 = VehicleFactory.getVehicle(VehicleType.CAR, "RED", "PETROL");
    Car car1 = new Car(200L, "Benz", vehicle1);
    car1.manufacture();

    Vehicle vehicle2 = VehicleFactory.getVehicle(VehicleType.CAR, "RED", "PETROL");
    Car car2 = new Car(250L, "Audi", vehicle2);
    car2.manufacture();

    System.out.println(
        "Total number of unique vehicles created: " + VehicleFactory.totalVehiclesCreated());
  }

  public static enum VehicleType {

    CAR,
    BUS,
    TRUCK
  }


  // Intrinsic
  public static class Vehicle {

    private final VehicleType vehicleType;
    private final String color;
    private final String fuelType;

    public Vehicle(VehicleType vehicleType, String color, String fuelType) {
      this.vehicleType = vehicleType;
      this.color = color;
      this.fuelType = fuelType;
    }

    public void manufacture(String brand, Long speed) {
      System.out.println(
          "Manufacturing vehicle of type " + vehicleType + " and of brand " + brand + " of color "
              + color + " and fuel type " + fuelType + " and speed it goes " + speed);
    }
  }


  // Extrinsic
  public static class Car {

    public final Long speed;
    public final String brand;
    public final Vehicle vehicle;

    public Car(Long speed, String brand, Vehicle vehicle) {
      this.speed = speed;
      this.brand = brand;
      this.vehicle = vehicle;
    }

    public void manufacture() {
      vehicle.manufacture(brand, speed);
    }
  }


  public static class VehicleFactory {

    private static final ConcurrentHashMap<String, Vehicle> vehicleMap = new ConcurrentHashMap<>();

    private VehicleFactory() {
    }

    public static Vehicle getVehicle(VehicleType vehicleType, String color, String fuelType) {
      String key =
          vehicleType.toString() + "_" + color.toLowerCase().trim() + "_" + fuelType.toLowerCase()
              .trim();
      return vehicleMap.computeIfAbsent(key, k -> new Vehicle(vehicleType, color, fuelType));
    }

    public static int totalVehiclesCreated() {
      return vehicleMap.size();
    }
  }
}
