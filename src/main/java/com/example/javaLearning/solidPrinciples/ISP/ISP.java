package com.example.javaLearning.solidPrinciples.ISP;

public class ISP {

  public static void main(String[] args) {

    WorkerClass.Robot robot = new WorkerClass.Robot();
    robot.work();
    try {
      robot.eat();
    } catch (Exception e) {
      System.out.println(e);
    }

    System.out.println("_______________________");

    WorkerISP.Worker worker = new WorkerISP.Worker();
    worker.work();
    worker.eat();

    WorkerISP.Robot robot1 = new WorkerISP.Robot();
    robot1.work();
  }

  public static class WorkerClass {

    interface Worker {
      void work();

      void eat();
    }


    public static class Robot implements Worker {
      @Override
      public void work() {
        System.out.println("Robot working...");
      }

      @Override
      public void eat() {
        throw new UnsupportedOperationException("Robots don’t eat!");
      }
    }
  }

  /**
   * Separate interfaces → Workable and Eatable.
   * Worker (human) implements both → because humans can work and eat.
   * Robot only implements Workable → no unnecessary eat() method.
   * No throw new UnsupportedOperationException, no fat interfaces.
   * Clients can depend on only what they need:
   * A cafeteria system depends on Eatable.
   * A factory system depends on Workable.
   * This is textbook ISP.
   */
  public static class WorkerISP {

    public interface Workable {
      void work();
    }


    public interface Eatable {

      void eat();
    }


    public static class Worker implements Workable, Eatable {

      @Override
      public void work() {
        System.out.println("Man is working....");
      }

      @Override
      public void eat() {
        System.out.println("Man is eating");
      }
    }


    public static class Robot implements Workable {

      @Override
      public void work() {
        System.out.println("Robot is working....");
      }
    }
  }
}
