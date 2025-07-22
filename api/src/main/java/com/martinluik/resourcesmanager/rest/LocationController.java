package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.service.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping(LocationController.API_URL)
@RequiredArgsConstructor
public class LocationController {

  public static final String API_URL = "/api/locations";

  private final LocationService locationService;

  @GetMapping
  public List<LocationDto> getAll() {
    log.info("GET request received to retrieve all locations");
    return locationService.getAllLocations();
  }

  @GetMapping("{id}")
  public ResponseEntity<LocationDto> getById(@PathVariable @NotNull UUID id) {
    log.info("GET request received to retrieve location with ID: {}", id);
    var dto = locationService.getLocation(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<LocationDto> create(@Valid @RequestBody LocationDto dto) {
    log.info("POST request received to create new location");
    var created = locationService.createLocation(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping
  public ResponseEntity<LocationDto> update(@Valid @RequestBody LocationDto dto) {
    log.info("PUT request received to update location with ID: {}", dto.getId());
    var updated = locationService.updateLocation(dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable @NotNull UUID id) {
    log.info("DELETE request received to delete location with ID: {}", id);
    locationService.deleteLocation(id);
    return ResponseEntity.noContent().build();
  }
}
