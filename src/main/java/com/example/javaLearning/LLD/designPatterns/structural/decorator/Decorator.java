package com.example.javaLearning.LLD.designPatterns.structural.decorator;

import lombok.RequiredArgsConstructor;

public class Decorator {

  public static void main(String[] args) {

    Pizza simplePizza = new SimplePizza();
    System.out.println(simplePizza.description());
    System.out.println(simplePizza.price());

    System.out.println("-------------------");

    Pizza cheesePizza = new CheesePizza(simplePizza);
    System.out.println(cheesePizza.description());
    System.out.println(cheesePizza.price());

    System.out.println("-------------------");

    Pizza cheeseCornPizza = new CheeseCornPizza(cheesePizza);
    System.out.println(cheeseCornPizza.description());
    System.out.println(cheeseCornPizza.price());

    System.out.println("-------------------");

    Pizza cheesePizzaWithExtraSauce = new ExtraSaucePizza(cheesePizza);
    System.out.println(cheesePizzaWithExtraSauce.description());
    System.out.println(cheesePizzaWithExtraSauce.price());
  }

  public interface Pizza {

    String description();

    Long price();
  }


  public static class SimplePizza implements Pizza {

    @Override
    public String description() {
      return "This is simple pizza";
    }

    @Override
    public Long price() {
      return 100L;
    }
  }


  // Decorator base lass
  @RequiredArgsConstructor
  public static abstract class PizzaDecorator implements Pizza {

    public final Pizza pizza;

    @Override
    public String description() {
      return pizza.description();
    }

    @Override
    public Long price() {
      return pizza.price();
    }
  }


  public static class CheesePizza extends PizzaDecorator {


    public CheesePizza(Pizza pizza) {
      super(pizza);
    }

    @Override
    public String description() {
      return (pizza.description() + " plus extra cheese");
    }

    @Override
    public Long price() {
      return pizza.price() + 50L;
    }
  }


  public static class CheeseCornPizza extends PizzaDecorator {

    protected CheeseCornPizza(Pizza pizza) {
      super(pizza);
    }

    @Override
    public String description() {
      return pizza.description() + " plus extra corn";
    }

    @Override
    public Long price() {
      return super.price() + 50L;
    }
  }


  public static class ExtraSaucePizza extends PizzaDecorator {

    public ExtraSaucePizza(Pizza pizza) {
      super(pizza);
    }

    @Override
    public String description() {
      return super.description() + " with extra sauce";
    }

    @Override
    public Long price() {
      return super.price() + 30L;
    }
  }
}
