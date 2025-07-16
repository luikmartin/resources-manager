package com.martinluik.resourcesmanager.core.service;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.exception.CharacteristicNotFoundException;
import com.martinluik.resourcesmanager.common.exception.ResourceNotFoundException;
import com.martinluik.resourcesmanager.common.service.CharacteristicService;
import com.martinluik.resourcesmanager.core.mapper.CharacteristicsMapper;
import com.martinluik.resourcesmanager.core.repository.CharacteristicRepository;
import com.martinluik.resourcesmanager.core.repository.ResourceRepository;
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
  private final CharacteristicsMapper characteristicsMapper;

  @Override
  @Transactional(readOnly = true)
  public List<CharacteristicDto> getAllCharacteristics() {
    return characteristicRepository.findAll().stream().map(characteristicsMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CharacteristicDto getCharacteristic(UUID id) {
    Assert.notNull(id, "Characteristic ID cannot be null");

    return characteristicRepository
        .findById(id)
        .map(characteristicsMapper::toDto)
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

    var characteristic = characteristicsMapper.toEntity(dto);
    characteristic.setResource(resource);

    var savedCharacteristic = characteristicRepository.save(characteristic);

    return characteristicsMapper.toDto(savedCharacteristic);
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

    var characteristic = characteristicsMapper.toEntity(dto);
    characteristic.setResource(existingCharacteristic.getResource());

    var updatedCharacteristic = characteristicRepository.save(characteristic);

    return characteristicsMapper.toDto(updatedCharacteristic);
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
        .map(characteristicsMapper::toDto)
        .toList();
  }
}
