package com.example.javaLearning.LLD.designPatterns.behavioral.chainOfResponsibility;

public class ChainOfResponsibility {

  public static void main(String[] args) {
    Handler info = new InfoLogger();
    Handler debug = new DebugLogger();
    Handler error = new ErrorLogger();

    info.setNext(debug).setNext(error);

    info.handle("INFO");
    info.handle("DEBUG");
    info.handle("ERROR");
    info.handle("TRACE");
  }

  public static abstract class Handler {
    protected Handler next;

    public Handler setNext(Handler next) {
      this.next = next;
      return next; // for chaining
    }

    public abstract void handle(String request);
  }


  // Concrete handlers
  public static class InfoLogger extends Handler {
    public void handle(String request) {
      if (request.equals("INFO")) {
        System.out.println("INFO: Logging mode message");
      } else if (next != null) {
        next.handle(request);
      }
    }
  }


  public static class DebugLogger extends Handler {
    public void handle(String request) {
      if (request.equals("DEBUG")) {
        System.out.println("DEBUG: Logging debug message");
      } else if (next != null) {
        next.handle(request);
      }
    }
  }


  public static class ErrorLogger extends Handler {
    public void handle(String request) {
      if (request.equals("ERROR")) {
        System.out.println("ERROR: Logging error message");
      } else if (next != null) {
        next.handle(request);
      } else {
        System.out.println("No handler found for: " + request);
      }
    }
  }
}
