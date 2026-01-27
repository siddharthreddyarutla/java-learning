package com.example.javaLearning.LLD.designPatterns.structural;


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
}
