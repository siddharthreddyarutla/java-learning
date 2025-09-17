package com.example.javaLearning.oops.abstraction;

public class Interface {

  public static void main(String[] args) {

    Playable musicPlayer = new MusicPlayer();

    musicPlayer.play();
    musicPlayer.pause();
    musicPlayer.stop();
    musicPlayer.changeBass();
    System.out.println("Volume is: " + musicPlayer.volume);
    musicPlayer.changePixel();

    System.out.println("---------------------");

    Playable videoPlayer = new VideoPlayer();

    videoPlayer.play();
    videoPlayer.pause();
    videoPlayer.stop();
    videoPlayer.changeBass();
    videoPlayer.changePixel();
  }

  public interface Playable {

    // Can add fields as well
    public Long volume = 2L;

    // By default these are
    public abstract void play();

    void pause();

    void stop();

    // private methods can have own body in interface post java 8/9
    private void changeBass() {
      System.out.println("Playable: Changing bass");
    }

    default void changePixel() {
      System.out.println("Playable: Changing pixel");
    }
  }


  public static class MusicPlayer implements Playable {

    @Override
    public void play() {
      System.out.println("MusicPlayer: playing....");
    }

    @Override
    public void pause() {
      System.out.println("MusicPlayer: paused.....");
    }

    @Override
    public void stop() {
      System.out.println("MusicPlayer: stopped.....");
    }
  }


  public static class VideoPlayer implements Playable {

    @Override
    public void play() {
      System.out.println("VideoPlayer: playing....");
    }

    @Override
    public void pause() {
      System.out.println("VideoPlayer: paused.....");
    }

    @Override
    public void stop() {
      System.out.println("VideoPlayer: stopped.....");
    }

    @Override
    public void changePixel() {
      System.out.println("VideoPlayer: Changing pixel");
    }
  }
}

