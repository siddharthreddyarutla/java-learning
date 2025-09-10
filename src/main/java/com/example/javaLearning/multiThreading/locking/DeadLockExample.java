package com.example.javaLearning.multiThreading.locking;

public class DeadLockExample {

  public static void main(String[] args) {

    Pen pen = new Pen();
    Paper paper = new Paper();

    Thread thread1 = new Thread(new Task1(pen, paper), "Thread 1");

    Thread thread2 = new Thread(new Task2(pen, paper), "Thread 2");

    thread1.start();
    thread2.start();
  }

  public static class Task1 implements Runnable {

    private Pen pen;

    private Paper paper;

    public Task1(Pen pen, Paper paper) {
      this.pen = pen;
      this.paper = paper;
    }

    @Override
    public void run() {
      paper.writeWithPaperAndPen(pen); // Thread 1 locks paper and tries to lock pen
    }
  }


  public static class Task2 implements Runnable {

    private Pen pen;

    private Paper paper;

    public Task2(Pen pen, Paper paper) {
      this.pen = pen;
      this.paper = paper;
    }

    @Override
    public void run() {
      /* This clears the deadlock - as paper object has lock so once paper lock releases we can use
       paper in pen  */
      synchronized (paper) {
        pen.writeWithPenAndPaper(paper); // Thread 2 locks pen and try to lock paper
      }
    }
  }


  public static class Pen {

    public synchronized void writeWithPenAndPaper(Paper paper) {
      System.out.println(
          Thread.currentThread().getName() + " is using pen " + this + " and trying to use paper "
              + paper);
      paper.finishWritingWithPaper();
    }

    public synchronized void finishWritingWithPen() {
      System.out.println(Thread.currentThread().getName() + " finished using pen " + this);
    }
  }


  public static class Paper {

    public synchronized void writeWithPaperAndPen(Pen pen) {
      System.out.println(
          Thread.currentThread().getName() + " is using paper " + this + " and trying to use pen "
              + pen);
      pen.finishWritingWithPen();
    }

    public synchronized void finishWritingWithPaper() {
      System.out.println(Thread.currentThread().getName() + " finished using paper " + this);
    }
  }

}
