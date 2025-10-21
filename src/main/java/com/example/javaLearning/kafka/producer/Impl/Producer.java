package com.example.javaLearning.kafka.producer.Impl;

import com.example.javaLearning.kafka.KafkaPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@SpringBootApplication
@EnableConfigurationProperties(KafkaPropertiesConfig.class)
public class Producer {

  public static void main(String[] args) {

    ConfigurableApplicationContext ctx = SpringApplication.run(Producer.class, args);
    ProduceEvent produceEvent = ctx.getBean(ProduceEvent.class);
    produceEvent.produce("replicated_topic", "Hi again from java application");
  }

  @Service
  public class ProduceEvent {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProduceEvent(KafkaTemplate<String, String> kafkaTemplate) {
      this.kafkaTemplate = kafkaTemplate;
    }

    public void produce(String topic, String msg) {
      kafkaTemplate.send(topic, msg);
      System.out.println("Successfully published msg to the topic: " + topic + " with msg: " + msg);
    }
  }
}
