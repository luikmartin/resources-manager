package com.martinluik.resourcesmanager.core.service;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.service.ResourceService;
import com.martinluik.resourcesmanager.core.mapper.CharacteristicsMapper;
import com.martinluik.resourcesmanager.core.mapper.LocationMapper;
import com.martinluik.resourcesmanager.core.mapper.ResourceMapper;
import com.martinluik.resourcesmanager.core.repository.CharacteristicRepository;
import com.martinluik.resourcesmanager.core.repository.LocationRepository;
import com.martinluik.resourcesmanager.core.repository.ResourceRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final LocationRepository locationRepository;
  private final CharacteristicRepository characteristicRepository;
  private final ResourceMapper resourceMapper;
  private final LocationMapper locationMapper;
  private final CharacteristicsMapper characteristicsMapper;

  @Transactional(readOnly = true)
  public List<ResourceDto> getAllResources() {
    return resourceRepository.findAll().stream().map(resourceMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public ResourceDto getResource(UUID id) {
    return resourceRepository.findById(id).map(resourceMapper::toDto).orElse(null);
  }

  @Transactional
  public ResourceDto createResource(ResourceDto dto) {
    var resource = resourceMapper.toEntity(dto);
    resource = resourceRepository.save(resource);
    var createdDto = resourceMapper.toDto(resource);
    return createdDto;
  }

  @Transactional
  public ResourceDto updateResource(UUID id, ResourceDto dto) {
    if (!resourceRepository.existsById(id)) return null;
    var resource = resourceMapper.toEntity(dto);
    resource.setId(id);
    resource = resourceRepository.save(resource);
    return resourceMapper.toDto(resource);
  }

  @Transactional
  public void deleteResource(UUID id) {
    if (resourceRepository.existsById(id)) {
      resourceRepository.deleteById(id);
    }
  }

  @Transactional
  public ResourceDto updateResourceLocation(UUID resourceId, LocationDto locationDto) {
    var resource = resourceRepository.findById(resourceId).orElse(null);
    if (resource == null) return null;

    var location = locationMapper.toEntity(locationDto);
    location.setId(resource.getLocation().getId()); // Preserve existing location ID
    location = locationRepository.save(location);

    resource.setLocation(location);
    resource = resourceRepository.save(resource);

    return resourceMapper.toDto(resource);
  }

  @Transactional
  public CharacteristicDto addCharacteristic(UUID resourceId, CharacteristicDto characteristicDto) {
    var resource = resourceRepository.findById(resourceId).orElse(null);
    if (resource == null) return null;

    var characteristic = characteristicsMapper.toEntity(characteristicDto);
    characteristic.setResource(resource);
    characteristic = characteristicRepository.save(characteristic);

    return characteristicsMapper.toDto(characteristic);
  }

  @Transactional
  public CharacteristicDto updateCharacteristic(
      UUID characteristicId, CharacteristicDto characteristicDto) {
    if (!characteristicRepository.existsById(characteristicId)) return null;

    var characteristic = characteristicsMapper.toEntity(characteristicDto);
    characteristic.setId(characteristicId);
    characteristic = characteristicRepository.save(characteristic);

    return characteristicsMapper.toDto(characteristic);
  }

  @Transactional
  public void deleteCharacteristic(UUID characteristicId) {
    var characteristic = characteristicRepository.findById(characteristicId).orElse(null);
    if (characteristic != null) {
      characteristicRepository.deleteById(characteristicId);
    }
  }

  @Transactional
  public void exportAllResources() {
    var resources = resourceRepository.findAll();
    // Bulk export logic can be implemented here
  }
}
