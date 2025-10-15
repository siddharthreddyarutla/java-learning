package com.example.javaLearning.designPatterns.creational.abstractFactory;


public class AbstractDesignPattern {

  public static void main(String[] args) {

    String a = "sid";
    String b = "sid";
    System.out.println(a==b);

    Integer i = 127;
    Integer j = 127;
    System.out.println(i==j);

    Integer k = 129;
    Integer l = 129;
    System.out.println(k==l);

    Double d = 1.0;
    Double w = 1.0;
    System.out.println(d==w);

    // Choose a factory family at runtime
    TransportFamilyFactory transportFamilyFactory = new HeavyTransport();
    runTransport(transportFamilyFactory);

    System.out.println("-----------------------");

    TransportFamilyFactory transportFamilyFactory1 = new SpeedTransport();
    runTransport(transportFamilyFactory1);
  }

  // --- Product interfaces ---
  public interface RoadTransport {
    void deliver();
  }


  public interface SeaTransport {
    void deliver();
  }


  // --- Concrete products (heavy family) ---
  public static class Truck implements RoadTransport {

    @Override
    public void deliver() {
      System.out.println("Truck transports goods via road and it is heavy vehicle transport");
    }
  }

  // --- Concrete products (fast family) ---
  public static class Bike implements RoadTransport {

    @Override
    public void deliver() {
      System.out.println("Bike transports goods via road and it is speed transport");
    }
  }

  // --- Concrete products (heavy family) ---
  public static class Ship implements SeaTransport {

    @Override
    public void deliver() {
      System.out.println("Ship transports goods via sea and it is heavy transport");
    }
  }

  // --- Concrete products (fast family) ---
  public static class SpeedBoat implements SeaTransport {

    @Override
    public void deliver() {
      System.out.println("Speed boat transports goods via sea and it is speed transport");
    }
  }

  // --- Abstract factory (family factory) ---
  public interface TransportFamilyFactory {
    RoadTransport createRoadTransport();

    SeaTransport createSeaTransport();
  }

  // --- Concrete family factories ---
  // Fast family (bike + speedboat)
  public static class SpeedTransport implements TransportFamilyFactory {

    @Override
    public RoadTransport createRoadTransport() {
      // Speed transport via road is bike
      return new Bike();
    }

    // Speed transport via sea is speed boat
    @Override
    public SeaTransport createSeaTransport() {
      return new SpeedBoat();
    }
  }

  // Heavy family (truck + cargo ship)
  public static class HeavyTransport implements TransportFamilyFactory {

    @Override
    public RoadTransport createRoadTransport() {
      // Heavy transport via road is Truck
      return new Truck();
    }

    // Heavy transport via sea is ship
    @Override
    public SeaTransport createSeaTransport() {
      return new Ship();
    }
  }

  public static void runTransport(TransportFamilyFactory transportFamilyFactory) {

    System.out.println("Transport service was called");

    System.out.println("Selecting mode of transport..");

    RoadTransport roadTransport = transportFamilyFactory.createRoadTransport();
    SeaTransport seaTransport = transportFamilyFactory.createSeaTransport();

    System.out.println("Goods are been shipped with road transport as: " + roadTransport.getClass()
        .getSimpleName());
    roadTransport.deliver();
    System.out.println(
        "Goods are been shipped with sea transport as: " + seaTransport.getClass().getSimpleName());
    seaTransport.deliver();

    System.out.println("delivery is completed");
  }
}
