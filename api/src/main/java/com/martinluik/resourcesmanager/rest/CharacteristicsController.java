package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.exception.CharacteristicNotFoundException;
import com.martinluik.resourcesmanager.common.service.CharacteristicService;
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
@RequestMapping(CharacteristicsController.API_URL)
@RequiredArgsConstructor
public class CharacteristicsController {

  public static final String API_URL = "/api/characteristics";

  private final CharacteristicService characteristicService;

  @GetMapping
  public List<CharacteristicDto> getAll() {
    log.info("GET request received to retrieve all characteristics");
    return characteristicService.getAllCharacteristics();
  }

  @GetMapping("{id}")
  public ResponseEntity<CharacteristicDto> getById(@PathVariable UUID id) {
    log.info("GET request received to retrieve characteristic with ID: {}", id);
    var dto = characteristicService.getCharacteristic(id);
    return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
  }

  @GetMapping("resource/{resourceId}")
  public List<CharacteristicDto> getByResourceId(@PathVariable UUID resourceId) {
    log.info(
        "GET request received to retrieve characteristics for resource with ID: {}", resourceId);
    return characteristicService.getCharacteristicsByResourceId(resourceId);
  }

  @PostMapping("resource/{resourceId}")
  public ResponseEntity<CharacteristicDto> create(
      @PathVariable UUID resourceId, @Valid @RequestBody CharacteristicDto dto) {
    log.info("POST request received to create characteristic for resource with ID: {}", resourceId);
    var created = characteristicService.createCharacteristic(dto, resourceId);
    return created != null
        ? ResponseEntity.created(null).body(created)
        : ResponseEntity.badRequest().build();
  }

  @PutMapping
  public ResponseEntity<CharacteristicDto> update(@Valid @RequestBody CharacteristicDto dto) {
    log.info("PUT request received to update characteristic with ID: {}", dto.getId());
    try {
      var updated = characteristicService.updateCharacteristic(dto);
      return ResponseEntity.ok(updated);
    } catch (IllegalArgumentException e) {
      log.warn("Invalid argument for characteristic update: {}", e.getMessage());
      return ResponseEntity.badRequest().build();
    } catch (CharacteristicNotFoundException e) {
      log.warn("Characteristic not found for update: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    log.info("DELETE request received to delete characteristic with ID: {}", id);
    characteristicService.deleteCharacteristic(id);
    return ResponseEntity.noContent().build();
  }
}
