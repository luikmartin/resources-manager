package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.service.LocationService;
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
@RequestMapping("api/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  @GetMapping
  public List<LocationDto> getAll() {
    return locationService.getAllLocations();
  }

  @GetMapping("{id}")
  public ResponseEntity<LocationDto> getById(@PathVariable UUID id) {
    var dto = locationService.getLocation(id);
    return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<LocationDto> create(@RequestBody LocationDto dto) {
    var created = locationService.createLocation(dto);
    return ResponseEntity.ok(created);
  }

  @PutMapping("{id}")
  public ResponseEntity<LocationDto> update(@PathVariable UUID id, @RequestBody LocationDto dto) {
    var updated = locationService.updateLocation(id, dto);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    locationService.deleteLocation(id);
    return ResponseEntity.noContent().build();
  }
}
