package com.example.javaLearning.LLD.designPatterns.creational;

import jakarta.persistence.Tuple;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

public class revision {

  public static void main(String[] args) {

    System.out.println("-----Singleton------");

    // Singleton
    SingletonClass singletonClass = SingletonClass.getInstance();
    SingletonClass singletonClass1 = SingletonClass.INSTANCE;
    System.out.println(singletonClass.hashCode());
    System.out.println(singletonClass1.hashCode());


    System.out.println("-----Factory method------");

    // factory method
    FactoryMethod.BusTransportFactory busTransportFactory = new FactoryMethod.BusTransportFactory();
    busTransportFactory.transportation();

    System.out.println("-----Builder------");

    Builder.User user = new Builder.User.UserBuilder().name("Sid").age(24).build();
    System.out.println(user);

    System.out.println("-----Prototype------");

    Prototype.Rectangle rectangle = new Prototype.Rectangle(10, "red", 20, 10, 30);
    Prototype.Rectangle rectangleClone = (Prototype.Rectangle) rectangle.clone();
    rectangleClone.setColor("blue");
    System.out.println(rectangleClone.toString());
    System.out.println(rectangle.toString());

    System.out.println("--------Abstract factory-------");

    AbstractFactory.TransportFactoryFamily transportFactoryFamily =
        new AbstractFactory.HeavyTransportFamily();
    AbstractFactory.runTransport(transportFactoryFamily);

    AbstractFactory.TransportFactoryFamily transportFactoryFamily1 =
        new AbstractFactory.SpeedTransportFamily();
    AbstractFactory.runTransport(transportFactoryFamily1);
  }


  // Initialization-on-demand holder idiom
  public static class SingletonClass {

    private static final SingletonClass INSTANCE = new SingletonClass();

    private SingletonClass() {
    }

    public static SingletonClass getInstance() {
      return INSTANCE;
    }
  }


  public static class FactoryMethod {

    public interface Transport {
      String deliver();
    }


    public abstract static class Transportation {

      protected abstract Transport getTransport();

      public void transportation() {

        System.out.println("Ready to transport");
        System.out.println("Got the transport mode: " + getTransport().getClass().getSimpleName());
        System.out.println(getTransport().deliver());
        System.out.println("Successfully done");
      }
    }


    public static class BusTransportFactory extends Transportation {

      @Override
      protected Transport getTransport() {
        return new Bus();
      }
    }


    public static class TruckTransportFactory extends Transportation {

      @Override
      protected Transport getTransport() {
        return new Truck();
      }
    }


    public static class Bus implements Transport {

      @Override
      public String deliver() {
        return "Bus is selected for the transport";
      }
    }


    public static class Truck implements Transport {

      @Override
      public String deliver() {
        return "Truck  is selected for the transport";
      }
    }

  }


  public static class Builder {

    public static class User {
      private String name;
      private Integer age;

      @Override
      public String toString() {
        return "User{" + "name='" + name + '\'' + ", age=" + age + '}';
      }

      private User(UserBuilder userBuilder) {
        this.age = userBuilder.age;
        this.name = userBuilder.name;
      }

      public static class UserBuilder {
        private String name;
        private Integer age;

        public UserBuilder name(String name) {
          this.name = name;
          return this;
        }

        public UserBuilder age(Integer age) {
          this.age = age;
          return this;
        }

        public User build() {
          return new User(this);
        }
      }
    }
  }



  public static class Prototype {

    @Setter
    public abstract static class Shape {

      public int x;
      public int y;
      public String color;

      public Shape(int y, String color, int x) {
        this.y = y;
        this.color = color;
        this.x = x;
      }

      public Shape(Shape shape) {
        this.x = shape.x;
        this.y = shape.y;
        this.color = shape.color;
      }

      public abstract Shape clone();
    }


    public static class Rectangle extends Shape {

      private int length;
      private int breadth;

      public Rectangle(Shape shape) {
        super(shape);
      }

      public Rectangle(int y, String color, int x, int length, int breadth) {
        super(y, color, x);
        this.length = length;
        this.breadth = breadth;
      }

      @Override
      public Shape clone() {
        return new Rectangle(this);
      }

      @Override
      public String toString() {
        return "Rectangle{" + "length=" + length + ", breadth=" + breadth + ", x=" + x + ", y=" + y
            + ", color='" + this.color + '\'' + '}';
      }
    }
  }

  public static class AbstractFactory {


    // product interfaces
    private interface RoadTransport {
      String deliver();
    }

    private interface SeaTransport {
      String deliver();
    }

    // concrete classes (Fast family)
    public static class SuperBike implements RoadTransport {

      @Override
      public String deliver() {
        return "Delivered by super bike";
      }
    }

    public static class SpeedBoat implements SeaTransport {

      @Override
      public String deliver() {
        return "Delivered by speed boat";
      }
    }

    // concrete classes (Slow family)
    public static class Ship implements SeaTransport {

      @Override
      public String deliver() {
        return "Delivered by ship";
      }
    }

    public static class Truck implements RoadTransport {

      @Override
      public String deliver() {
        return "Delivered by truck";
      }
    }

    public interface TransportFactoryFamily {

      RoadTransport createRoadTransport();

      SeaTransport createSeaTransport();
    }

    public static class SpeedTransportFamily implements TransportFactoryFamily {

      @Override
      public RoadTransport createRoadTransport() {
        return new SuperBike();
      }

      @Override
      public SeaTransport createSeaTransport() {
        return new SpeedBoat();
      }
    }

    public static class HeavyTransportFamily implements TransportFactoryFamily {

      @Override
      public RoadTransport createRoadTransport() {
        return new Truck();
      }

      @Override
      public SeaTransport createSeaTransport() {
        return new Ship();
      }
    }

    public static void runTransport(TransportFactoryFamily transportFactoryFamily) {

      RoadTransport roadTransport = transportFactoryFamily.createRoadTransport();
      SeaTransport seaTransport = transportFactoryFamily.createSeaTransport();

      System.out.println("Selected road transport: " + roadTransport.getClass().getSimpleName());
      System.out.println("Selected sea transport: " + seaTransport.getClass().getSimpleName());

      System.out.println(roadTransport.deliver());
      System.out.println(seaTransport.deliver());

      System.out.println("delivery is completed");
    }
  }
}
