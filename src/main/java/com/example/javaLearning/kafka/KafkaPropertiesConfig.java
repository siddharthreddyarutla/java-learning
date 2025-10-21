package com.example.javaLearning.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaPropertiesConfig(String bootstrapServers, String acks, int retries,
                                    int batchSize, int lingerMs, long bufferMemory,
                                    String keySerializer, String valueSerializer,
                                    String keyDeserializer, String valueDeserializer,
                                    int sessionTimeoutMs, int heartbeatIntervalMs,
                                    boolean enableAutoCommit, String autoOffsetReset,
                                    boolean enableIdempotence, String testTopic,
                                    String replicatedTopic, String groupId, int maxPollRecords,
                                    int maxPollIntervalMs) {

}
