package com.martinluik.resourcesmanager.core.service;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.service.CharacteristicService;
import com.martinluik.resourcesmanager.core.mapper.CharacteristicsMapper;
import com.martinluik.resourcesmanager.core.repository.CharacteristicRepository;
import com.martinluik.resourcesmanager.core.repository.ResourceRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CharacteristicServiceImpl implements CharacteristicService {

  private final CharacteristicRepository characteristicRepository;
  private final ResourceRepository resourceRepository;
  private final CharacteristicsMapper characteristicsMapper;

  @Transactional(readOnly = true)
  public List<CharacteristicDto> getAllCharacteristics() {
    return characteristicRepository.findAll().stream().map(characteristicsMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public CharacteristicDto getCharacteristic(UUID id) {
    return characteristicRepository.findById(id).map(characteristicsMapper::toDto).orElse(null);
  }

  @Transactional
  public CharacteristicDto createCharacteristic(CharacteristicDto dto, UUID resourceId) {
    var resource = resourceRepository.findById(resourceId).orElse(null);
    if (resource == null) {
      return null;
    }

    var characteristic = characteristicsMapper.toEntity(dto);
    characteristic.setResource(resource);
    characteristic = characteristicRepository.save(characteristic);
    return characteristicsMapper.toDto(characteristic);
  }

  @Transactional
  public CharacteristicDto updateCharacteristic(UUID id, CharacteristicDto dto) {
    var existingCharacteristic = characteristicRepository.findById(id).orElse(null);
    if (existingCharacteristic == null) {
      return null;
    }

    var characteristic = characteristicsMapper.toEntity(dto);
    characteristic.setId(id);
    characteristic.setResource(existingCharacteristic.getResource());
    characteristic = characteristicRepository.save(characteristic);
    return characteristicsMapper.toDto(characteristic);
  }

  @Transactional
  public void deleteCharacteristic(UUID id) {
    if (characteristicRepository.existsById(id)) {
      characteristicRepository.deleteById(id);
    }
  }

  @Transactional(readOnly = true)
  public List<CharacteristicDto> getCharacteristicsByResourceId(UUID resourceId) {
    return characteristicRepository.findAll().stream()
        .filter(c -> c.getResource().getId().equals(resourceId))
        .map(characteristicsMapper::toDto)
        .toList();
  }
}
