package com.martinluik.resourcesmanager.messaging;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.martinluik.resourcesmanager.config.KafkaConfig.BULK_EXPORT_TOPIC;
import static com.martinluik.resourcesmanager.config.KafkaConfig.RESOURCES_UPDATES_TOPIC;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import com.martinluik.resourcesmanager.service.KafkaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void sendResourceUpdate(ResourceDto resourceDto) {
    try {
      var resourceData = objectMapper.writeValueAsString(resourceDto);
      var future =
          kafkaTemplate.send(
              RESOURCES_UPDATES_TOPIC, String.valueOf(resourceDto.getId()), resourceData);

      future.whenComplete(
          (result, ex) -> {
            if (ex == null) {
              log.info(
                  "Resource update sent to Kafka - Topic: {}, Partition: {}, Offset: {}, Key: {}",
                  result.getRecordMetadata().topic(),
                  result.getRecordMetadata().partition(),
                  result.getRecordMetadata().offset(),
                  result.getProducerRecord().key());
            } else {
              log.error(
                  "Failed to send resource update to Kafka for resource ID: {}",
                  resourceDto.getId(),
                  ex);
            }
          });
    } catch (JsonProcessingException e) {
      log.error(
          "Failed to serialize resource data for Kafka, resource ID: {}", resourceDto.getId(), e);
    } catch (Exception e) {
      log.error(
          "Unexpected error sending resource update to Kafka, resource ID: {}",
          resourceDto.getId(),
          e);
    }
  }

  @Override
  public void sendBulkExport(List<ResourceDto> resources) {
    log.info("Sending bulk export of {} resources to Kafka", resources.size());

    try {
      var resourcesData = objectMapper.writeValueAsString(resources);
      var future = kafkaTemplate.send(BULK_EXPORT_TOPIC, "bulk-export", resourcesData);

      future.whenComplete(
          (result, ex) -> {
            if (ex == null) {
              log.info(
                  "Bulk export sent to Kafka - Topic: {}, Partition: {}, Offset: {}, Resources count: {}",
                  result.getRecordMetadata().topic(),
                  result.getRecordMetadata().partition(),
                  result.getRecordMetadata().offset(),
                  resources.size());
            } else {
              log.error("Failed to send bulk export to Kafka", ex);
            }
          });
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize resources data for bulk export", e);
    } catch (Exception e) {
      log.error("Unexpected error sending bulk export to Kafka", e);
    }
  }
}
