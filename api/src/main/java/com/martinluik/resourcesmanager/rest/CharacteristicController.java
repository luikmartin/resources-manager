package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.service.CharacteristicService;
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
@RequestMapping("api/characteristics")
@RequiredArgsConstructor
public class CharacteristicController {

  private final CharacteristicService characteristicService;

  @GetMapping
  public List<CharacteristicDto> getAll() {
    return characteristicService.getAllCharacteristics();
  }

  @GetMapping("{id}")
  public ResponseEntity<CharacteristicDto> getById(@PathVariable UUID id) {
    var dto = characteristicService.getCharacteristic(id);
    return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
  }

  @GetMapping("resource/{resourceId}")
  public List<CharacteristicDto> getByResourceId(@PathVariable UUID resourceId) {
    return characteristicService.getCharacteristicsByResourceId(resourceId);
  }

  @PostMapping("resource/{resourceId}")
  public ResponseEntity<CharacteristicDto> create(
      @PathVariable UUID resourceId, @RequestBody CharacteristicDto dto) {
    var created = characteristicService.createCharacteristic(dto, resourceId);
    return created != null ? ResponseEntity.ok(created) : ResponseEntity.badRequest().build();
  }

  @PutMapping("{id}")
  public ResponseEntity<CharacteristicDto> update(
      @PathVariable UUID id, @RequestBody CharacteristicDto dto) {
    var updated = characteristicService.updateCharacteristic(id, dto);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    characteristicService.deleteCharacteristic(id);
    return ResponseEntity.noContent().build();
  }
}
