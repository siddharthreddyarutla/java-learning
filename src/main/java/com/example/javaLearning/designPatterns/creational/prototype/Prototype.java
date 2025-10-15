package com.example.javaLearning.designPatterns.creational.prototype;

public class Prototype {

  public static void main(String[] args) {

    Rectangle rectangle = new Rectangle(10, 20, "Red", 3, 7);
    System.out.println(rectangle.toString());

    // Clone
    Rectangle rectangleClone = (Rectangle) rectangle.clone();
    System.out.println(rectangleClone.toString());

    rectangle.height = 20;
    System.out.println(rectangle.toString());
    System.out.println(rectangleClone.toString());

    Circle circle = new Circle(10, 20, "Blue", 8);
    System.out.println(circle.toString());

    // Clone
    Circle circleClone = (Circle) circle.clone();
    System.out.println(circleClone.toString());
  }

  public static abstract class Shape {

    public int x;
    public int y;
    public String colour;

    public Shape() {

    }

    public Shape(int x, int y, String colour) {
      this.x = x;
      this.y = y;
      this.colour = colour;
    }

    public Shape(Shape shape) {
      this.x = shape.x;
      this.y = shape.y;
      this.colour = shape.colour;
    }

    public abstract Shape clone();
  }


  public static class Rectangle extends Shape {

    public int width;
    public int height;

    public Rectangle() {

    }

    public Rectangle(int x, int y, String colour, int width, int height) {
      super(x, y, colour);
      this.width = width;
      this.height = height;
    }


    private Rectangle(Rectangle rectangle) {
      super(rectangle);
      this.height = rectangle.height;
      this.width = rectangle.width;
    }

    @Override
    public Shape clone() {
      return new Rectangle(this);
    }

    @Override
    public String toString() {
      return "Rectangle [x = " + this.x + ", y = " + this.y + ", colour = " + this.colour
          + ", width = " + this.width + ", height = " + this.height + "]";
    }
  }


  public static class Circle extends Shape {

    public int radius;

    public Circle() {

    }

    public Circle(int x, int y, String colour, int radius) {
      super(x, y, colour);
      this.radius = radius;
    }

    private Circle(Circle circle) {
      super(circle);
      this.radius = circle.radius;
    }

    @Override
    public Shape clone() {
      return new Circle(this);
    }

    @Override
    public String toString() {
      return "Circle [x = " + this.x + ", y = " + this.y + ", colour = " + this.colour
          + ", radius = " + this.radius + "]";
    }
  }
}
