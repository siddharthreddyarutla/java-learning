package com.example.javaLearning.kafka.consumer.impl;

import com.example.javaLearning.kafka.KafkaPropertiesConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

  private final KafkaPropertiesConfig kafkaPropertiesConfig;

  public KafkaConsumerConfig(KafkaPropertiesConfig kafkaPropertiesConfig) {
    this.kafkaPropertiesConfig = kafkaPropertiesConfig;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaPropertiesConfig.bootstrapServers());
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaPropertiesConfig.groupId());
    consumerProps.put(String.valueOf(ConsumerConfig.DEFAULT_MAX_POLL_RECORDS),
        kafkaPropertiesConfig.maxPollRecords());
    consumerProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,
        kafkaPropertiesConfig.heartbeatIntervalMs());
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        kafkaPropertiesConfig.valueDeserializer());
    consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
        kafkaPropertiesConfig.maxPollIntervalMs());
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        kafkaPropertiesConfig.keyDeserializer());
    consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
        kafkaPropertiesConfig.sessionTimeoutMs());
    return new DefaultKafkaConsumerFactory<>(consumerProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
}
