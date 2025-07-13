package com.martinluik.resourcesmanager.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendResourceUpdate(String resourceData) {
        var future = kafkaTemplate.send("resource-updates", resourceData);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Resource update sent to Kafka with offset: {}", result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send resource update to Kafka", ex);
            }
        });
    }

    public void sendBulkExport(List<String> resourcesData) {
        log.info("Sending bulk export of {} resources to Kafka", resourcesData.size());
        
        for (String resourceData : resourcesData) {
            var future = kafkaTemplate.send("bulk-export", resourceData);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Bulk export resource sent to Kafka with offset: {}", result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send bulk export resource to Kafka", ex);
                }
            });
        }
    }
} 
