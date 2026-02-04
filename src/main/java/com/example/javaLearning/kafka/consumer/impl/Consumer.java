package com.example.javaLearning.kafka.consumer.impl;

import com.example.javaLearning.kafka.KafkaPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableConfigurationProperties(KafkaPropertiesConfig.class)
public class Consumer {

  public static void main(String[] args) {

    ConfigurableApplicationContext ctx = SpringApplication.run(Consumer.class, args);
  }

  @Component
  public class ConsumeEvent {

    private final KafkaPropertiesConfig propertiesConfig;

    public ConsumeEvent(KafkaPropertiesConfig propertiesConfig) {
      this.propertiesConfig = propertiesConfig;
    }

//    @KafkaListener(topics = "test_topic", groupId = "java-learning-app")
    public void consume1(@Payload String message,
        @Header(KafkaHeaders.RECEIVED_PARTITION) String partition) {
      System.out.println(
          "consume1: Event was consumed and the msg is: " + message + " from partition: "
              + partition);
    }

    /* Depicting multi consumer instances where consumers in a single group partitions will be
    segregated accordingly*/
//    @KafkaListener(topics = "test_topic", groupId = "java-learning-app")
    public void consume2(@Payload String message,
        @Header(KafkaHeaders.RECEIVED_PARTITION) String partition) {
      System.out.println(
          "consume2: Event was consumed and the msg is: " + message + " from partition: "
              + partition);
    }

    /* Depicting multiple consumer groups */
//    @KafkaListener(topics = "test_topic", groupId = "java-learning-app-duplicate")
    public void consume3(@Payload String message,
        @Header(KafkaHeaders.RECEIVED_PARTITION) String partition) {
      System.out.println(
          "consume3: Event was consumed and the msg is: " + message + " from partition: "
              + partition);
    }
  }
}
