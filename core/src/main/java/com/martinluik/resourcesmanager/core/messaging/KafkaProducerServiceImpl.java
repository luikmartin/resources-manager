package com.martinluik.resourcesmanager.core.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.service.KafkaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void sendResourceUpdate(ResourceDto resourceDto) {
    try {
      var resourceData = objectMapper.writeValueAsString(resourceDto);
      var future = kafkaTemplate.send("resource-updates", String.valueOf(resourceDto.getId()), resourceData);

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
              log.error("Failed to send resource update to Kafka for resource ID: {}", resourceDto.getId(), ex);
            }
          });
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize resource data for Kafka, resource ID: {}", resourceDto.getId(), e);
    } catch (Exception e) {
      log.error("Unexpected error sending resource update to Kafka, resource ID: {}", resourceDto.getId(), e);
    }
  }

  @Override
  public void sendBulkExport(List<ResourceDto> resources) {
    log.info("Sending bulk export of {} resources to Kafka", resources.size());

    for (ResourceDto resource : resources) {
      try {
        var resourceData = objectMapper.writeValueAsString(resource);
        var future = kafkaTemplate.send("bulk-export", String.valueOf(resource.getId()), resourceData);

        future.whenComplete(
            (result, ex) -> {
              if (ex == null) {
                log.debug(
                    "Bulk export resource sent to Kafka - Topic: {}, Partition: {}, Offset: {}, Key: {}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    result.getProducerRecord().key());
              } else {
                log.error("Failed to send bulk export resource to Kafka for resource ID: {}", resource.getId(), ex);
              }
            });
      } catch (JsonProcessingException e) {
        log.error("Failed to serialize resource data for bulk export, resource ID: {}", resource.getId(), e);
      } catch (Exception e) {
        log.error("Unexpected error sending bulk export resource to Kafka, resource ID: {}", resource.getId(), e);
      }
    }
  }
}
