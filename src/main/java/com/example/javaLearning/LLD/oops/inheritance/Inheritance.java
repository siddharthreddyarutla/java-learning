package com.example.javaLearning.LLD.oops.inheritance;

public class Inheritance {

  public static void main(String[] args) {

    Vehicle vehicle = new Car();
    vehicle.start();

    Vehicle vehicle1 = new Truck();
    vehicle1.start();
  }

  public static class Vehicle {

    public String engine;
    public String tires;
    public String brand;

    public Vehicle() {
      System.out.println("Vehicle is created...");
    }

    public void start() {
      System.out.println("Vehicle is ready to starting..");
    }
  }


  public static class Car extends Vehicle {

    private String AC;
    private String music;

    public Car() {
      super();
      System.out.println("Car is created...");
    }

    @Override
    public void start() {
      System.out.println("Car is ready to start...");
    }
  }


  // Multi Level inheritance
  public static class Maruti extends Car {

    private String mileage;

    @Override
    public void start() {
      System.out.println("Maruti car is ready to start...");
    }
  }


  public static class Truck extends Vehicle {

    private String power;
    private String torque;

    public Truck() {
      System.out.println("Truck is created...");
    }

    @Override
    public void start() {
      System.out.println("Truck is ready to start...");
    }
  }
}
