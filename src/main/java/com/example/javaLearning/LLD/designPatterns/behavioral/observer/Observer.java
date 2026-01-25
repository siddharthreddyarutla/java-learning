package com.example.javaLearning.LLD.designPatterns.behavioral.observer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Observer {

  public static void main(String[] args) {
    // Start Spring context so @Component and @EventListenerImpl are active
    ConfigurableApplicationContext ctx = SpringApplication.run(Observer.class, args);

    RunObserverSynchronous.run(ctx);

    System.out.println("----- async impl ----");
    RunObserverAsync.run(ctx);
  }

  public static class RunObserverSynchronous {

    public static void run(ConfigurableApplicationContext ctx) {
      Long startTime = System.currentTimeMillis();

      LogInfo logInfo = new LogInfo("INFO", "file opened");
      LogInfo logError = new LogInfo("ERROR", "file crashed");

      LogPublisher publisher = ctx.getBean(LogPublisher.class);
      publisher.publishEvent(logInfo);
      System.out.println("RunObserverSynchronous: Info event is published.....");
      publisher.publishEvent(logError);
      System.out.println("RunObserverSynchronous: Error event is published....");

      System.out.println(
          "Total time taken for synchronous task: " + (System.currentTimeMillis() - startTime));
    }
  }


  public static class RunObserverAsync {

    public static void run(ConfigurableApplicationContext ctx) {
      Long startTime = System.currentTimeMillis();

      LogInfo logInfo = new LogInfo("INFO", "file opened");
      LogInfo logError = new LogInfo("ERROR", "file crashed");

      LogPublisher publisher = ctx.getBean(LogPublisher.class);

      ExecutorService executorService = Executors.newFixedThreadPool(1);
      executorService.submit(() -> publisher.publishEvent(logInfo));
      executorService.shutdown();
      System.out.println("RunObserverAsync: Info event is published.....");
      ExecutorService executorService1 = Executors.newFixedThreadPool(1);
      executorService1.submit(() -> publisher.publishEvent(logError));
      executorService1.shutdown();
      System.out.println("RunObserverAsync: Error event is published....");

      System.out.println(
          "Total time taken for async task: " + (System.currentTimeMillis() - startTime));
    }
  }


  public record LogInfo(String logType, String info) {
  }


  public interface Logger {
    void Log(LogInfo logInfo);
  }


  @Component
  public static class LogPublisher extends ApplicationEvent {

    private final ApplicationEventPublisher applicationEventPublisher;

    public LogPublisher(ApplicationEventPublisher applicationEventPublisher) {
      super(applicationEventPublisher);
      this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(LogInfo logInfo) {
      applicationEventPublisher.publishEvent(logInfo);
    }
  }


  @Component
  public static class LogEventConsumer {

    private final LoggerService loggerService;

    public LogEventConsumer(LoggerService loggerService) {
      try {
        Thread.sleep(10000);
      } catch (Exception e) {
        Thread.currentThread().interrupt();
        System.out.println(e);
      }
      this.loggerService = loggerService;
    }

    @EventListener
    public void consumeEvent(LogInfo logInfo) {
      loggerService.Log(logInfo);
    }
  }


  @Service
  public static class LoggerService implements Logger {

    @Override
    public void Log(LogInfo logInfo) {
      System.out.println(
          "Log type issue is: " + logInfo.logType + " and log mode is: " + logInfo.info);
    }
  }
}
