package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.service.ResourceService;
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
public class ResourcesController {

  public static final String API_URL = "/api/resources";

  private final ResourceService resourceService;

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

  @PutMapping
  public ResponseEntity<ResourceDto> update(@Valid @RequestBody ResourceDto dto) {
    log.info("PUT request received to update resource with ID: {}", dto.getId());
    var updated = resourceService.updateResource(dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    log.info("DELETE request received to delete resource with ID: {}", id);
    resourceService.deleteResource(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("bulk-export")
  public ResponseEntity<String> exportAllResources() {
    log.info("POST request received to initiate bulk export of all resources");
    resourceService.exportAllResources();
    return ResponseEntity.accepted().body("Bulk export initiated");
  }
}
