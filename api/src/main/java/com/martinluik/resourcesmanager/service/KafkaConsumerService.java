package com.martinluik.resourcesmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

  @KafkaListener(topics = "resource-updates", groupId = "resources-group")
  public void listenResourceUpdates(String message) {
    log.info("Received resource update: {}", message);
  }

  @KafkaListener(topics = "bulk-export", groupId = "resources-group")
  public void listenBulkExport(String message) {
    log.info("Received bulk export resource: {}", message);
  }
}
