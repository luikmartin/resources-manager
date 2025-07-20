package com.martinluik.resourcesmanager.messaging;

import static com.martinluik.resourcesmanager.config.KafkaConfig.BULK_EXPORT_TOPIC;
import static com.martinluik.resourcesmanager.config.KafkaConfig.RESOURCES_UPDATES_TOPIC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl {

  private static final String RESOURCES_GROUP_ID = "resources-group";

  private final ObjectMapper mapper;

  @KafkaListener(topics = RESOURCES_UPDATES_TOPIC, groupId = RESOURCES_GROUP_ID)
  public void listenResourceUpdates(String message) {
    try {
      var resourceDto = mapper.readValue(message, ResourceDto.class);
      log.info("Received resource update for resource ID: {}", resourceDto.getId());
      // TODO - Add business logic here to process the resource update
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize resource update message", e);
    }
  }

  @KafkaListener(topics = BULK_EXPORT_TOPIC, groupId = RESOURCES_GROUP_ID)
  public void listenBulkExport(String message) {
    try {
      var resources = mapper.readValue(message, new TypeReference<List<ResourceDto>>() {});
      log.info("Received bulk export with {} resources", resources.size());
      // TODO - Add business logic here to process the bulk export resources
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize bulk export message", e);
    }
  }
}
