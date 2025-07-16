package com.martinluik.resourcesmanager.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

  public static final String RESOURCES_UPDATES_TOPIC = "resource-updates";
  public static final String BULK_EXPORT_TOPIC = "bulk-export";

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.admin.auto-create:true}")
  private boolean autoCreateTopics;

  @Bean
  public NewTopic resourceUpdatesTopic() {
    return TopicBuilder.name(RESOURCES_UPDATES_TOPIC)
        .partitions(3)
        .replicas(1)
        .configs(
            Map.of(
                "retention.ms", "604800000", // 7 days
                "cleanup.policy", "delete"))
        .build();
  }

  @Bean
  public NewTopic bulkExportTopic() {
    return TopicBuilder.name(BULK_EXPORT_TOPIC)
        .partitions(3)
        .replicas(1)
        .configs(
            Map.of(
                "retention.ms", "86400000", // 1 day
                "cleanup.policy", "delete"))
        .build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    var configProps = new HashMap<String, Object>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put(ProducerConfig.RETRIES_CONFIG, 5);
    configProps.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
    configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
    configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1048576);
    configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
    configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

    // Connection retry settings
    configProps.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
    configProps.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);
    configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);

    // Connection timeout settings
    configProps.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 540000);
    configProps.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, 300000);

    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public KafkaAdmin kafkaAdmin() {
    var configs = new HashMap<String, Object>();
    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configs.put(ProducerConfig.CLIENT_ID_CONFIG, "resources-manager-admin");

    // Connection retry settings
    configs.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
    configs.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);
    configs.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);

    // Connection timeout settings
    configs.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 540000);
    configs.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, 300000);
    configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

    var kafkaAdmin = new KafkaAdmin(configs);
    kafkaAdmin.setAutoCreate(autoCreateTopics);
    kafkaAdmin.setFatalIfBrokerNotAvailable(false);
    kafkaAdmin.setOperationTimeout(30000);

    return kafkaAdmin;
  }
}
