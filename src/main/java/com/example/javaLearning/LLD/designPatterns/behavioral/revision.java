package com.example.javaLearning.LLD.designPatterns.behavioral;

import java.util.Stack;

public class revision {

  public static void main(String[] args) {

    System.out.println("---------Strategy-------");

    int clientBankBalance = 1000;
    int withdrawAmount = 10001;
    if (clientBankBalance > withdrawAmount) {
      Strategy.Ram client = new Strategy.Ram(new Strategy.UPI());
      client.doPayment();
    } else {
      Strategy.Ram client = new Strategy.Ram(new Strategy.CARD());
      client.doPayment();
    }

    System.out.println("-----Chain of responsibility-----");

    ChainOfResponsibility.Handler info = new ChainOfResponsibility.InfoHandler();
    ChainOfResponsibility.Handler debug = new ChainOfResponsibility.DebugHandler();
    ChainOfResponsibility.Handler error = new ChainOfResponsibility.ErrorHandler();

    info.setNext(debug).setNext(error);

    info.handle(ChainOfResponsibility.LogType.INFO);
    info.handle(ChainOfResponsibility.LogType.DEBUG);
    info.handle(ChainOfResponsibility.LogType.ERROR);
    info.handle(ChainOfResponsibility.LogType.TRACE);

    System.out.println("--------Memento-------");

    Memento.TextEditor textEditor = new Memento.TextEditor();
    Memento.TextHistory textHistory = new Memento.TextHistory();

    textEditor.add("Hello");
    textHistory.save(textEditor.save());

    textEditor.add("World");
    textHistory.save(textEditor.save());

    textEditor.add("sid");
    textHistory.save(textEditor.save());

    System.out.println("Text before undo: " + textEditor.text);
    textEditor.restore(textHistory.undo());
    System.out.println("Text after 1st undo: " + textEditor.text);

    textEditor.restore(textHistory.undo());
    System.out.println("Text after 2nd undo: " + textEditor.text);
  }

  public static class Strategy {

    // Strategy interface
    public interface PaymentStrategy {
      String pay();
    }


    // Concrete strategies
    public static class UPI implements PaymentStrategy {

      @Override
      public String pay() {
        return "Payment is done via UPI";
      }
    }


    public static class CARD implements PaymentStrategy {
      @Override
      public String pay() {
        return "Payment is doe via CARD";
      }
    }


    // Context
    public static class PaymentGateway {

      private static PaymentStrategy paymentStrategy;

      public PaymentGateway(PaymentStrategy paymentStrategy) {
        this.setPaymentStrategy(paymentStrategy);
      }

      public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
      }

      public void doPayment() {
        System.out.println(
            "Payment is done via strategy: " + paymentStrategy.getClass().getSimpleName());
      }
    }


    // Client
    public static class Ram extends PaymentGateway {

      public Ram(PaymentStrategy paymentStrategy) {
        super(paymentStrategy);
      }
    }
  }


  public static class ChainOfResponsibility {

    public enum LogType {
      INFO,
      DEBUG,
      ERROR,
      WARN,
      TRACE
    }


    public static abstract class Handler {

      protected Handler next;

      public Handler setNext(Handler handler) {
        this.next = handler;
        return next;
      }

      public abstract void handle(LogType logType);

    }


    public static class InfoHandler extends Handler {

      @Override
      public void handle(LogType logType) {
        if (LogType.INFO.equals(logType)) {
          System.out.println("Info log is printed");
        } else if (null != next) {
          next.handle(logType);
        } else {
          System.out.println("No handler found for " + logType);
        }
      }
    }


    public static class ErrorHandler extends Handler {

      @Override
      public void handle(LogType logType) {
        if (LogType.ERROR.equals(logType)) {
          System.out.println("Error log is printed");
        } else if (null != next) {
          next.handle(logType);
        } else {
          System.out.println("No handler found for " + logType);
        }
      }
    }


    public static class DebugHandler extends Handler {

      @Override
      public void handle(LogType logType) {
        if (LogType.DEBUG.equals(logType)) {
          System.out.println("Debug log is printed");
        } else if (null != next) {
          next.handle(logType);
        } else {
          System.out.println("No handler found for " + logType);
        }
      }
    }

  }


  public static class Memento {

    // Memento
    public static class TextMemento {

      private String text;

      public TextMemento(String text) {
        this.text = text;
      }

      public String getTextMemento() {
        return this.text;
      }
    }


    // Organiser
    public static class TextEditor {

      public String text = "";

      public void add(String text) {
        this.text += " " + text;
      }

      public TextMemento save() {
        return new TextMemento(this.text);
      }

      public void restore(TextMemento textMemento) {
        if (null != textMemento) {
          this.text = textMemento.getTextMemento();
        }
      }
    }


    // caretaker
    public static class TextHistory {

      public Stack<TextMemento> textMementoStack = new Stack<>();

      public void save(TextMemento textMemento) {
        textMementoStack.push(textMemento);
      }

      public TextMemento undo() {
        if (textMementoStack.size() <= 1) {
          return textMementoStack.peek();
        }
        textMementoStack.pop();
        return textMementoStack.peek();
      }
    }
  }
}
