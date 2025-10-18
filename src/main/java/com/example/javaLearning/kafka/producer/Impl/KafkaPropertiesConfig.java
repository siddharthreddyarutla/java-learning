package com.example.javaLearning.kafka.producer.Impl;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaPropertiesConfig(String bootstrapServers, String acks, int retries,
                                    int batchSize, int lingerMs, long bufferMemory,
                                    //key.serializer
                                    String keySerializer, String valueSerializer,
                                    String keyDeserializer, String valueDeserializer,
                                    int sessionTimeoutMs, int heartbeatIntervalMs,
                                    boolean enableAutoCommit, String autoOffsetReset,
                                    boolean enableIdempotence, String testTopic,
                                    String replicatedTopic, String groupId) {

}
