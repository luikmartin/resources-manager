package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import com.martinluik.resourcesmanager.service.LocationService;
import com.martinluik.resourcesmanager.service.ResourceService;
import jakarta.validation.Valid;
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
@RequestMapping(LocationController.API_URL)
@RequiredArgsConstructor
public class LocationController {

  public static final String API_URL = "/api/locations";

  private final LocationService locationService;
  private final ResourceService resourceService;

  @GetMapping
  public List<LocationDto> getAll() {
    log.info("GET request received to retrieve all locations");
    return locationService.getAllLocations();
  }

  @GetMapping("{id}")
  public ResponseEntity<LocationDto> getById(@PathVariable UUID id) {
    log.info("GET request received to retrieve location with ID: {}", id);
    var dto = locationService.getLocation(id);
    return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<LocationDto> create(@Valid @RequestBody LocationDto dto) {
    log.info("POST request received to create new location");
    var created = locationService.createLocation(dto);
    return ResponseEntity.created(null).body(created);
  }

  @PutMapping
  public ResponseEntity<LocationDto> update(@Valid @RequestBody LocationDto dto) {
    log.info("PUT request received to update location with ID: {}", dto.getId());
    var updated = locationService.updateLocation(dto);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    log.info("DELETE request received to delete location with ID: {}", id);
    locationService.deleteLocation(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("resource/{resourceId}")
  public ResponseEntity<ResourceDto> updateResourceLocation(
      @PathVariable UUID resourceId, @Valid @RequestBody LocationDto locationDto) {
    log.info("PUT request received to update location for resource with ID: {}", resourceId);
    var updated = resourceService.updateResourceLocation(resourceId, locationDto);
    return ResponseEntity.ok(updated);
  }
}
