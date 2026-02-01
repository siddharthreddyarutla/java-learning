package com.example.javaLearning.LLD.designPatterns.structural;

import java.util.ArrayList;
import java.util.List;

public class revision {

  public static void main(String[] args) {

    System.out.println("--------Decorator-------");
    Decorator.SimplePizza simplePizza = new Decorator.SimplePizza();
    System.out.println(simplePizza.description());
    System.out.println(simplePizza.prize());

    Decorator.CheesePizza cheesePizza = new Decorator.CheesePizza(simplePizza);
    System.out.println(cheesePizza.description());
    System.out.println(cheesePizza.prize());

    Decorator.CheeseAndCornPizza cheeseAndCornPizza = new Decorator.CheeseAndCornPizza(cheesePizza);
    System.out.println(cheeseAndCornPizza.description());
    System.out.println(cheeseAndCornPizza.prize());

    System.out.println("--------Adaptor--------");

    Adaptor.MediaAdapter mediaAdapter = new Adaptor.MediaAdapter(new Adaptor.VideoPlayer());
    mediaAdapter.play();

    System.out.println("------bridge---------");

    new Bridge.Circle(new Bridge.Red()).draw();

    System.out.println("------composite------");


    Composite.Manager revanth = new Composite.Manager("Revanth");
    Composite.Manager akshay = new Composite.Manager("akshay");
    Composite.Manager mayuresh = new Composite.Manager("mayuresh");

    Composite.Employee sakshi = new Composite.Employee(30_000, "sakshi");
    Composite.Employee kalyan = new Composite.Employee(10_000, "kalyan");
    Composite.Employee sowjanya = new Composite.Employee(15_000, "sowjanya");

    akshay.addSalary(kalyan);
    akshay.addSalary(sowjanya);
    mayuresh.addSalary(sakshi);
    revanth.addSalary(mayuresh);
    revanth.addSalary(akshay);

    revanth.printSalary();
    System.out.println("Total salary of revanth: " + revanth.totalSalary());


  }


  public static class Decorator {

    public interface Pizza {
      String description();

      Long prize();
    }


    public static class SimplePizza implements Pizza {
      @Override
      public String description() {
        return "This is simple pizza";
      }

      @Override
      public Long prize() {
        return 100L;
      }
    }


    public static abstract class PizzaDecorator implements Pizza {

      public Pizza pizza;

      public PizzaDecorator(Pizza pizza) {
        this.pizza = pizza;
      }

      @Override
      public String description() {
        return this.pizza.description();
      }

      @Override
      public Long prize() {
        return this.pizza.prize();
      }
    }


    public static class CheesePizza extends PizzaDecorator {

      public CheesePizza(Pizza pizza) {
        super(pizza);
      }

      @Override
      public String description() {
        return pizza.description() + " plus cheese";
      }

      @Override
      public Long prize() {
        return super.prize() + 50;
      }
    }


    public static class CheeseAndCornPizza extends PizzaDecorator {

      public CheeseAndCornPizza(Pizza pizza) {
        super(pizza);
      }

      @Override
      public String description() {
        return pizza.description() + " plus corn";
      }

      @Override
      public Long prize() {
        return super.prize() + 50;
      }
    }
  }


  public static class Adaptor {

    // Client expects
    public interface Media {
      void play();
    }


    // adaptee
    public static class VideoPlayer {

      public void playMedia() {
        System.out.println("playing old media video player");
      }
    }

    // Adaptor to make the adaptee compatable


    public static class MediaAdapter implements Media {

      private VideoPlayer videoPlayer;

      public MediaAdapter(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
      }

      @Override
      public void play() {
        videoPlayer.playMedia();
      }
    }
  }


  public static class Bridge {

    public static abstract class Shape {

      protected Colour colour;

      public Shape(Colour colour) {
        this.colour = colour;
      }

      public abstract void draw();
    }


    public interface Colour {
      void paint(Shape shape);
    }


    public static class Circle extends Shape {

      public Circle(Colour colour) {
        super(colour);
      }

      @Override
      public void draw() {
        System.out.println("Circle is drawn");
        colour.paint(this);
      }
    }


    public static class Square extends Shape {

      public Square(Colour colour) {
        super(colour);
      }

      @Override
      public void draw() {
        System.out.println("Square is drawn....");
        colour.paint(this);
      }
    }


    public static class Red implements Colour {

      @Override
      public void paint(Shape shape) {
        System.out.println(
            shape.getClass().getSimpleName() + " with colour " + this.getClass().getSimpleName()
                + " is painted");
      }
    }


    public static class Blue implements Colour {

      @Override
      public void paint(Shape shape) {
        System.out.println(
            shape.getClass().getSimpleName() + " with colour " + this.getClass().getSimpleName()
                + " is painted");
      }
    }
  }


  public static class Composite {

    public interface CalculateSalary {

      Integer totalSalary();

      void printSalary();
    }


    // leaf
    public static class Employee implements CalculateSalary {

      public Integer salary;
      public String name;

      public Employee(Integer salary, String name) {
        this.salary = salary;
        this.name = name;
      }

      @Override
      public Integer totalSalary() {
        return salary;
      }

      @Override
      public void printSalary() {
        System.out.println("     - " + this.name + " salary is: " + this.salary);
      }
    }


    // composite
    public static class Manager implements CalculateSalary {

      public String name;

      public Manager(String name) {
        this.name = name;
      }

      public List<CalculateSalary> calculateSalaryList = new ArrayList<>();

      public void addSalary(CalculateSalary calculateSalary) {
        calculateSalaryList.add(calculateSalary);
      }

      @Override
      public Integer totalSalary() {
        Integer totalSalary = 0;
        for (CalculateSalary calculateSalary : calculateSalaryList) {
          totalSalary += calculateSalary.totalSalary();
        }
        return totalSalary;
      }

      @Override
      public void printSalary() {
        System.out.println("+ Manager name: " + this.name);
        for (CalculateSalary calculateSalary : calculateSalaryList) {
          calculateSalary.printSalary();
        }
      }
    }
  }
}
