package com.example.javaLearning.designPatterns.structural.bridge;

import lombok.RequiredArgsConstructor;

public class Bridge {

  public static void main(String[] args) {

    Shape circle = new Circle();
    circle.draw();
    circle.resize();

    Colour red = new Red(circle);
    red.paint();

    Colour blue = new Blue(new Square());
    blue.paint();

    System.out.println();
    System.out.println("Other way of doing........");
    System.out.println();

    ShapeNew redSquare = new SquareNew(new RedNew());
    redSquare.draw();
  }


  public interface Shape {

    void draw();

    void resize();
  }


  public static class Circle implements Shape {

    @Override
    public void draw() {
      System.out.println(this.getClass().getSimpleName() + " is drawing.... ");
    }

    @Override
    public void resize() {
      System.out.println(this.getClass().getSimpleName() + " is resizing...");
    }
  }


  public static class Square implements Shape {

    @Override
    public void draw() {
      System.out.println(this.getClass().getSimpleName() + " is drawing.... ");
    }

    @Override
    public void resize() {
      System.out.println(this.getClass().getSimpleName() + " is resizing...");
    }
  }


  public interface Colour {

    void paint();
  }


  @RequiredArgsConstructor
  public static class Red implements Colour {

    private final Shape shape;

    @Override
    public void paint() {
      System.out.println(
          shape.getClass().getSimpleName() + " with colour " + this.getClass().getSimpleName()
              + " is painted");
    }
  }


  @RequiredArgsConstructor
  public static class Blue implements Colour {

    private final Shape shape;

    @Override
    public void paint() {
      System.out.println(
          shape.getClass().getSimpleName() + " with colour " + this.getClass().getSimpleName()
              + " is painted");
    }
  }


  /**
   * Other way of doing by using abstract class and shape holds colour instead of colour holding
   * shape
   */
  public static abstract class ShapeNew {

    public final ColourNew colourNew;

    protected ShapeNew(ColourNew colourNew) {
      this.colourNew = colourNew;
    }

    public abstract void draw();

    public abstract void resize();
  }


  public static class CircleNew extends ShapeNew {

    protected CircleNew(ColourNew colourNew) {
      super(colourNew);
    }

    @Override
    public void draw() {
      System.out.println(this.getClass().getSimpleName() + " is drawing.... ");
      colourNew.paint(this);
    }

    @Override
    public void resize() {
      System.out.println(this.getClass().getSimpleName() + " is resizing...");
    }
  }


  public static class SquareNew extends ShapeNew {

    protected SquareNew(ColourNew colourNew) {
      super(colourNew);
    }

    @Override
    public void draw() {
      System.out.println(this.getClass().getSimpleName() + " is drawing.... ");
      colourNew.paint(this);
    }

    @Override
    public void resize() {
      System.out.println(this.getClass().getSimpleName() + " is resizing...");
    }
  }


  public interface ColourNew {

    void paint(ShapeNew shapeNew);
  }


  public static class RedNew implements ColourNew {

    @Override
    public void paint(ShapeNew shapeNew) {
      System.out.println(
          shapeNew.getClass().getSimpleName() + " with colour " + this.getClass().getSimpleName()
              + " is painted");
    }
  }


  public static class BlueNew implements ColourNew {

    @Override
    public void paint(ShapeNew shapeNew) {
      System.out.println(
          shapeNew.getClass().getSimpleName() + " with colour " + this.getClass().getSimpleName()
              + " is painted");
    }
  }
}
