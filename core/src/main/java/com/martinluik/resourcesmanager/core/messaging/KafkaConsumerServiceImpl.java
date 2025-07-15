package com.martinluik.resourcesmanager.core.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl {

  private final ObjectMapper mapper;

  @KafkaListener(topics = "resource-updates", groupId = "resources-group")
  public void listenResourceUpdates(String message) {
    try {
      var resourceDto = mapper.readValue(message, ResourceDto.class);
      log.info("Received resource update for resource ID: {}", resourceDto.getId());
      // TODO - Add business logic here to process the resource update
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize resource update message", e);
    }
  }

  @KafkaListener(topics = "bulk-export", groupId = "resources-group")
  public void listenBulkExport(String message) {
    try {
      var resourceDto = mapper.readValue(message, ResourceDto.class);
      log.info("Received bulk export resource ID: {}", resourceDto.getId());
      // TODO - Add business logic here to process the bulk export resource
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize bulk export message", e);
    }
  }
}
