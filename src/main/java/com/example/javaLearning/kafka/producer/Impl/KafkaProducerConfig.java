package com.example.javaLearning.kafka.producer.Impl;

import com.example.javaLearning.kafka.KafkaPropertiesConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

  private final KafkaPropertiesConfig kafkaPropertiesConfig;

  public KafkaProducerConfig(KafkaPropertiesConfig kafkaPropertiesConfig) {
    this.kafkaPropertiesConfig = kafkaPropertiesConfig;
  }

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> producerProps = new HashMap<>();
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaPropertiesConfig.bootstrapServers());
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        kafkaPropertiesConfig.keySerializer());
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        kafkaPropertiesConfig.valueSerializer());
    producerProps.put(ProducerConfig.ACKS_CONFIG, kafkaPropertiesConfig.acks());
    producerProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
        kafkaPropertiesConfig.enableIdempotence());
    producerProps.put(ProducerConfig.LINGER_MS_CONFIG, kafkaPropertiesConfig.lingerMs());
    producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaPropertiesConfig.batchSize());
    return new DefaultKafkaProducerFactory<>(producerProps);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
