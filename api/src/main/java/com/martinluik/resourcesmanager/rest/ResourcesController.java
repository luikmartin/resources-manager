package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@RequestMapping(ResourcesController.API_URL)
@RequiredArgsConstructor
@Tag(name = "Resources", description = "Resource management operations")
public class ResourcesController {

  public static final String API_URL = "api/resources";

  private final ResourceService resourceService;

  @Operation(summary = "Get all resources", description = "Retrieves a list of all available resources")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all resources"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping
  public ResponseEntity<List<ResourceDto>> getAll() {
    log.info("GET request received to retrieve all resources");
    var resources = resourceService.getAllResources();
    return ResponseEntity.ok(resources);
  }

  @GetMapping("{id}")
  public ResponseEntity<ResourceDto> getById(@PathVariable UUID id) {
    log.info("GET request received to retrieve resource with ID: {}", id);
    var resource = resourceService.getResource(id);
    return ResponseEntity.ok(resource);
  }

  @PostMapping
  public ResponseEntity<ResourceDto> create(@Valid @RequestBody ResourceDto dto) {
    log.info("POST request received to create new resource");
    var created = resourceService.createResource(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("{id}")
  public ResponseEntity<ResourceDto> update(
      @PathVariable UUID id, @Valid @RequestBody ResourceDto dto) {
    log.info("PUT request received to update resource with ID: {}", id);
    var updated = resourceService.updateResource(id, dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    log.info("DELETE request received to delete resource with ID: {}", id);
    resourceService.deleteResource(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("{id}/location")
  public ResponseEntity<ResourceDto> updateLocation(
      @PathVariable UUID id, @Valid @RequestBody ResourceDto dto) {
    log.info("POST request received to update location for resource with ID: {}", id);
    var updated = resourceService.updateResourceLocation(id, dto.getLocation());
    return ResponseEntity.ok(updated);
  }

  @PostMapping("{id}/characteristics")
  public ResponseEntity<ResourceDto> addCharacteristic(
      @PathVariable UUID id, @Valid @RequestBody ResourceDto dto) {
    log.info("POST request received to add characteristic to resource with ID: {}", id);
    if (dto.getCharacteristics() == null || dto.getCharacteristics().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    
    var characteristic = dto.getCharacteristics().get(0);
    var added = resourceService.addCharacteristic(id, characteristic);
    if (added != null) {
      var resource = resourceService.getResource(id);
      return ResponseEntity.ok(resource);
    }
    return ResponseEntity.badRequest().build();
  }

  @PutMapping("characteristics/{characteristicId}")
  public ResponseEntity<ResourceDto> updateCharacteristic(
      @PathVariable UUID characteristicId, @Valid @RequestBody ResourceDto dto) {
    log.info("PUT request received to update characteristic with ID: {}", characteristicId);
    if (dto.getCharacteristics() == null || dto.getCharacteristics().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    
    var characteristic = dto.getCharacteristics().get(0);
    var updated = resourceService.updateCharacteristic(characteristicId, characteristic);
    if (updated != null) {
      var resource = resourceService.getResource(updated.getResourceId());
      return ResponseEntity.ok(resource);
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("characteristics/{characteristicId}")
  public ResponseEntity<Void> deleteCharacteristic(@PathVariable UUID characteristicId) {
    log.info("DELETE request received to delete characteristic with ID: {}", characteristicId);
    resourceService.deleteCharacteristic(characteristicId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("bulk-export")
  public ResponseEntity<String> exportAllResources() {
    log.info("POST request received to initiate bulk export of all resources");
    resourceService.exportAllResources();
    return ResponseEntity.accepted().body("Bulk export initiated");
  }
}
