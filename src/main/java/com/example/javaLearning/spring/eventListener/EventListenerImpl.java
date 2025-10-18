package com.example.javaLearning.spring.eventListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@SpringBootApplication
public class EventListenerImpl {

  public static void main(String[] args) {
    // Start Spring context so @Component and @EventListenerImpl are active
    ConfigurableApplicationContext ctx = SpringApplication.run(EventListenerImpl.class, args);

    PaymentInfoRecord paymentInfoRecord =
        new PaymentInfoRecord("lvjnsd45_35rw4", "CASH", "SUCCESS", new BigDecimal(1_00_000));

    PaymentEventPublisher publisher = ctx.getBean(PaymentEventPublisher.class);
    publisher.publishEvent(paymentInfoRecord);
  }

  public record PaymentInfoRecord(String id, String mode, String status, BigDecimal amount) {
  }


  public interface Logger {
    void Log(PaymentInfoRecord paymentInfoRecord);
  }


  public interface Notification {
    void send(PaymentInfoRecord paymentInfoRecord);
  }


  // publisher
  @Component
  public static class PaymentEventPublisher extends ApplicationEvent {

    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
      super(applicationEventPublisher);
      this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(PaymentInfoRecord paymentInfoRecord) {
      applicationEventPublisher.publishEvent(paymentInfoRecord);
    }
  }


  // One of the consumer for logging
  @Component
  public static class LogEventConsumer {

    private final LoggerService loggerService;

    public LogEventConsumer(LoggerService loggerService) {
      this.loggerService = loggerService;
    }

    @EventListener
    public void consumeEvent(PaymentInfoRecord paymentInfoRecord) {
      loggerService.Log(paymentInfoRecord);
    }
  }


  @Component
  public static class EmailNotificationConsumer {

    private final Notification notification;

    public EmailNotificationConsumer(Notification notification) {
      this.notification = notification;
    }

    @EventListener
    public void sendNotification(PaymentInfoRecord paymentInfoRecord) {
      notification.send(paymentInfoRecord);
    }
  }


  @Service
  public static class EmailNotification implements Notification {

    @Override
    public void send(PaymentInfoRecord paymentInfoRecord) {
      System.out.println(
          "Email notification was sent successfully for payment id: " + paymentInfoRecord.id
              + " as mode of payment used: " + paymentInfoRecord.mode + " of amount: "
              + paymentInfoRecord.amount + " and status: " + paymentInfoRecord.status);
    }
  }


  @Service
  public static class LoggerService implements Logger {

    @Override
    public void Log(PaymentInfoRecord paymentInfoRecord) {
      System.out.println(
          "Payment was successful with id: " + paymentInfoRecord.id + " as mode of payment used: "
              + paymentInfoRecord.mode + " of amount: " + paymentInfoRecord.amount + " and status: "
              + paymentInfoRecord.status);
    }
  }
}
