package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.service.ResourceService;
import com.martinluik.resourcesmanager.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/resources")
@RequiredArgsConstructor
public class ResourcesController {

  private final ResourceService resourceService;
  private final KafkaProducerService kafkaProducerService;
  private final ObjectMapper objectMapper;

  @GetMapping
  public List<ResourceDto> getAll() {
    return resourceService.getAllResources();
  }

  @GetMapping("{id}")
  public ResponseEntity<ResourceDto> getById(@PathVariable UUID id) {
    var dto = resourceService.getResource(id);
    return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<ResourceDto> create(@RequestBody ResourceDto dto) {
    try {
      var created = resourceService.createResource(dto);
      var resourceJson = objectMapper.writeValueAsString(created);
      kafkaProducerService.sendResourceUpdate(resourceJson);
      return ResponseEntity.ok(created);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping("{id}")
  public ResponseEntity<ResourceDto> update(@PathVariable UUID id, @RequestBody ResourceDto dto) {
    try {
      var updated = resourceService.updateResource(id, dto);
      if (updated != null) {
        var resourceJson = objectMapper.writeValueAsString(updated);
        kafkaProducerService.sendResourceUpdate(resourceJson);
        return ResponseEntity.ok(updated);
      }
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    resourceService.deleteResource(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("{id}/location")
  public ResponseEntity<ResourceDto> updateLocation(
      @PathVariable UUID id, @RequestBody ResourceDto dto) {
    var updated = resourceService.updateResourceLocation(id, dto.getLocation());
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @PostMapping("{id}/characteristics")
  public ResponseEntity<ResourceDto> addCharacteristic(
      @PathVariable UUID id, @RequestBody ResourceDto dto) {
    if (dto.getCharacteristics() != null && !dto.getCharacteristics().isEmpty()) {
      var characteristic = dto.getCharacteristics().get(0);
      var added = resourceService.addCharacteristic(id, characteristic);
      if (added != null) {
        return ResponseEntity.ok(resourceService.getResource(id));
      }
    }
    return ResponseEntity.badRequest().build();
  }

  @PutMapping("characteristics/{characteristicId}")
  public ResponseEntity<ResourceDto> updateCharacteristic(
      @PathVariable UUID characteristicId, @RequestBody ResourceDto dto) {
    if (dto.getCharacteristics() != null && !dto.getCharacteristics().isEmpty()) {
      var characteristic = dto.getCharacteristics().get(0);
      var updated = resourceService.updateCharacteristic(characteristicId, characteristic);
      if (updated != null) {
        return ResponseEntity.ok(resourceService.getResource(updated.getResourceId()));
      }
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("characteristics/{characteristicId}")
  public ResponseEntity<Void> deleteCharacteristic(@PathVariable UUID characteristicId) {
    resourceService.deleteCharacteristic(characteristicId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("bulk-export")
  public ResponseEntity<String> exportAllResources() {
    try {
      var resources = resourceService.getAllResources();
      var resourcesJson =
          resources.stream()
              .map(
                  resource -> {
                    try {
                      return objectMapper.writeValueAsString(resource);
                    } catch (Exception e) {
                      return "{}";
                    }
                  })
              .toList();
      kafkaProducerService.sendBulkExport(resourcesJson);
      return ResponseEntity.ok("Bulk export initiated and sent to Kafka");
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Failed to export resources: " + e.getMessage());
    }
  }
}
