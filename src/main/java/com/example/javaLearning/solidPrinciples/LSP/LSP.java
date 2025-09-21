package com.example.javaLearning.solidPrinciples.LSP;

public class LSP {


  public static void main(String[] args) {

    // This vehicle implementation break the LSP principle
    System.out.println("Violating LSP principle");
    try {
      Vehicle vehicle = new Car();
      vehicle.start();

      Vehicle vehicle1 = new Bicycle();
      vehicle1.start();
    } catch (Exception e) {
      System.out.println(e);
    }

    System.out.println();
    System.out.println("LSP principle");

    /**
     * Movable interface → for anything that can move (Cycle, Bus, Car, Scooter, etc.).
     * Startable interface → for things that require starting an engine (Bus, Car, etc.).
     * Cycle doesn’t have start() because it doesn’t need it.
     * Bus has both move() and start() because it does both.
     * 👉 Now if client code needs only movement, it depends on Movable.
     * 👉 If it needs engine control, it depends on Startable.
     * 👉 No one can mistakenly call start() on a Cycle → so substitution is safe.
     */
    Bus bus = new Bus();
    bus.start();
    bus.move();

    Cycle cycle = new Cycle();
    cycle.move();

    /**
     * testDrive(cycle) works fine → Cycle can substitute Movable.
     * testDrive(bus) works fine → Bus can substitute Movable.
     * testEngine(bus) works fine → Bus can substitute Startable.
     * You cannot pass cycle into testEngine (compiler blocks it) → prevents runtime errors. ✅
     */
    TestDrive testDrive = new TestDrive();
    testDrive.testDrive(new Bus());

    // Compile error which says operation is not supported
//        testDrive.testDrive(new Cycle());
  }

  public interface Vehicle {

    void start() throws Exception;
  }


  public static class Car implements Vehicle {

    @Override
    public void start() throws Exception {
      System.out.println("Car is ready to start......");
    }
  }


  public static class Bicycle implements Vehicle {


    @Override
    public void start() throws Exception {
      throw new Exception("Cannot perform this action");
    }
  }


  public interface Movable {

    void move();
  }


  public interface Startable {

    void start();
  }


  public static class Cycle implements Movable {

    @Override
    public void move() {
      System.out.println("Cycle is moving");
    }
  }


  public static class Bus implements Startable, Movable {

    @Override
    public void move() {
      System.out.println("Bus is moving");
    }

    @Override
    public void start() {
      System.out.println("Bus is ready to start");
    }
  }


  public static class TestDrive {

    public void testDrive(Startable startable) {

      System.out.println("Vehicle is came for test drive and type of vehicle is: ");
      startable.start();
    }
  }
}
