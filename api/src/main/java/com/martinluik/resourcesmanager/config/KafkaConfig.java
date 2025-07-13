package com.martinluik.resourcesmanager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic resourceUpdatesTopic() {
        return TopicBuilder.name("resource-updates")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bulkExportTopic() {
        return TopicBuilder.name("bulk-export")
                .partitions(3)
                .replicas(1)
                .build();
    }
} 