package com.example.javaLearning.designPatterns.creational.factoryMethod;

public class FactoryMethod {

  public static void main(String[] args) {

    TransportFactory transportFactory = new TruckTransportFactory();
    transportFactory.createProductAndTransport();

    TransportFactory transportFactory1 = new ShipTransportFactory();
    transportFactory1.createProductAndTransport();

    // Violating OCP (to reduce factory subclasses)
    // If you only need a few options, a parameterized factory method can replace subclasses:
    System.out.println("----------------Violating OCP----------------");
    TransportFactory truck = TransportFactoryNew.getTransportFactory("TRUCK");
    truck.createProductAndTransport();

    /**
     * Extendability
     * If tomorrow you add PlaneTransportFactory, you don’t change existing code — you just add a new class.
     * That shows your code respects OCP ✅.
     */
  }

  public interface Transport {

    void deliver();
  }


  public static class Truck implements Transport {

    @Override
    public void deliver() {
      System.out.println("Truck will deliver items");
    }
  }


  public static class Ship implements Transport {

    @Override
    public void deliver() {
      System.out.println("Ship will deliver items");
    }
  }


  public static abstract class TransportFactory {

    protected abstract Transport getTransportType();

    public void createProductAndTransport() {

      System.out.println("Product was created successfully, ready to transport.....");
      System.out.println("Selecting mode of transport..");
      Transport transport = getTransportType();
      System.out.println("Got the mode of transport " + transport.getClass().getSimpleName());
      transport.deliver();
      System.out.println("Product is delivered");
    }
  }


  public static class TruckTransportFactory extends TransportFactory {

    @Override
    protected Transport getTransportType() {
      return new Truck();
    }
  }


  public static class ShipTransportFactory extends TransportFactory {

    @Override
    protected Transport getTransportType() {
      return new Ship();
    }
  }

  public static class TransportFactoryNew {
    public static TransportFactory getTransportFactory(String type) {
      return switch (type) {
        case "TRUCK" -> new TruckTransportFactory();
        case "SHIP" -> new ShipTransportFactory();
        default -> throw new IllegalArgumentException("Unknown type");
      };
    }
  }
}
