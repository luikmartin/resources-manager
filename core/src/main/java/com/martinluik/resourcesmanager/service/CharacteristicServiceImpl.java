package com.martinluik.resourcesmanager.service;

import com.martinluik.resourcesmanager.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.exception.CharacteristicNotFoundException;
import com.martinluik.resourcesmanager.exception.ResourceNotFoundException;
import com.martinluik.resourcesmanager.mapper.CharacteristicMapper;
import com.martinluik.resourcesmanager.repository.CharacteristicRepository;
import com.martinluik.resourcesmanager.repository.ResourceRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class CharacteristicServiceImpl implements CharacteristicService {

  private final CharacteristicRepository characteristicRepository;
  private final ResourceRepository resourceRepository;
  private final CharacteristicMapper characteristicMapper;

  @Override
  @Transactional(readOnly = true)
  public List<CharacteristicDto> getAllCharacteristics() {
    return characteristicRepository.findAll().stream().map(characteristicMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CharacteristicDto getCharacteristic(UUID id) {
    Assert.notNull(id, "Characteristic ID cannot be null");

    return characteristicRepository
        .findById(id)
        .map(characteristicMapper::toDto)
        .orElseThrow(() -> new CharacteristicNotFoundException(id));
  }

  @Override
  @Transactional
  public CharacteristicDto createCharacteristic(CharacteristicDto dto, UUID resourceId) {
    Assert.notNull(dto, "Characteristic DTO cannot be null");
    Assert.notNull(resourceId, "Resource ID cannot be null");

    var resource =
        resourceRepository
            .findById(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException(resourceId));

    var characteristic = characteristicMapper.toEntity(dto);
    characteristic.setResource(resource);

    var savedCharacteristic = characteristicRepository.save(characteristic);

    return characteristicMapper.toDto(savedCharacteristic);
  }

  @Override
  @Transactional
  public CharacteristicDto updateCharacteristic(CharacteristicDto dto) {
    Assert.notNull(dto, "Characteristic DTO cannot be null");
    Assert.notNull(dto.getId(), "Characteristic ID cannot be null for update");

    var existingCharacteristic =
        characteristicRepository
            .findById(dto.getId())
            .orElseThrow(() -> new CharacteristicNotFoundException(dto.getId()));

    var characteristic = characteristicMapper.toEntity(dto);
    characteristic.setResource(existingCharacteristic.getResource());

    var updatedCharacteristic = characteristicRepository.save(characteristic);

    return characteristicMapper.toDto(updatedCharacteristic);
  }

  @Override
  @Transactional
  public void deleteCharacteristic(UUID id) {
    Assert.notNull(id, "Characteristic ID cannot be null");

    if (!characteristicRepository.existsById(id)) {
      throw new CharacteristicNotFoundException(id);
    }

    characteristicRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CharacteristicDto> getCharacteristicsByResourceId(UUID resourceId) {
    Assert.notNull(resourceId, "Resource ID cannot be null");

    if (!resourceRepository.existsById(resourceId)) {
      throw new ResourceNotFoundException(resourceId);
    }

    return characteristicRepository.findByResourceId(resourceId).stream()
        .map(characteristicMapper::toDto)
        .toList();
  }
}
