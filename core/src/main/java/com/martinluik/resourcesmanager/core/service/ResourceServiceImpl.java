package com.martinluik.resourcesmanager.core.service;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.exception.ResourceNotFoundException;
import com.martinluik.resourcesmanager.common.exception.CharacteristicNotFoundException;
import com.martinluik.resourcesmanager.common.service.ResourceService;
import com.martinluik.resourcesmanager.common.service.KafkaService;

import com.martinluik.resourcesmanager.core.mapper.CharacteristicsMapper;
import com.martinluik.resourcesmanager.core.mapper.LocationMapper;
import com.martinluik.resourcesmanager.core.mapper.ResourceMapper;
import com.martinluik.resourcesmanager.core.repository.CharacteristicRepository;
import com.martinluik.resourcesmanager.core.repository.LocationRepository;
import com.martinluik.resourcesmanager.core.repository.ResourceRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final LocationRepository locationRepository;
  private final CharacteristicRepository characteristicRepository;
  private final ResourceMapper resourceMapper;
  private final LocationMapper locationMapper;
  private final CharacteristicsMapper characteristicsMapper;
  private final KafkaService kafkaService;

  @Override
  @Transactional(readOnly = true)
  public List<ResourceDto> getAllResources() {
    return resourceRepository.findAll().stream().map(resourceMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ResourceDto getResource(UUID id) {
    var resource = resourceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(id));
    return resourceMapper.toDto(resource);
  }

  @Override
  @Transactional
  public ResourceDto createResource(ResourceDto dto) {
    var resource = resourceMapper.toEntity(dto);
    resource = resourceRepository.save(resource);
    var createdDto = resourceMapper.toDto(resource);
    
    // Send Kafka message for resource creation
    kafkaService.sendResourceUpdate(createdDto);
    
    return createdDto;
  }

  @Override
  @Transactional
  public ResourceDto updateResource(UUID id, ResourceDto dto) {
    if (!resourceRepository.existsById(id)) {
      throw new ResourceNotFoundException(id);
    }

    var resource = resourceMapper.toEntity(dto);
    resource.setId(id);
    resource = resourceRepository.save(resource);
    var updatedDto = resourceMapper.toDto(resource);
    
    // Send Kafka message for resource update
    kafkaService.sendResourceUpdate(updatedDto);
    
    return updatedDto;
  }

  @Override
  @Transactional
  public void deleteResource(UUID id) {
    if (!resourceRepository.existsById(id)) {
      throw new ResourceNotFoundException(id);
    }
    resourceRepository.deleteById(id);
  }

  @Override
  @Transactional
  public ResourceDto updateResourceLocation(UUID resourceId, LocationDto locationDto) {
    var resource = resourceRepository.findById(resourceId)
        .orElseThrow(() -> new ResourceNotFoundException(resourceId));

    var location = locationMapper.toEntity(locationDto);
    location.setId(resource.getLocation().getId()); // Preserve existing location ID
    location = locationRepository.save(location);

    resource.setLocation(location);
    resource = resourceRepository.save(resource);

    return resourceMapper.toDto(resource);
  }

  @Override
  @Transactional
  public CharacteristicDto addCharacteristic(UUID resourceId, CharacteristicDto characteristicDto) {
    var resource = resourceRepository.findById(resourceId)
        .orElseThrow(() -> new ResourceNotFoundException(resourceId));

    var characteristic = characteristicsMapper.toEntity(characteristicDto);
    characteristic.setResource(resource);
    characteristic = characteristicRepository.save(characteristic);

    return characteristicsMapper.toDto(characteristic);
  }

  @Override
  @Transactional
  public CharacteristicDto updateCharacteristic(UUID characteristicId, CharacteristicDto characteristicDto) {
    if (!characteristicRepository.existsById(characteristicId)) {
      throw new CharacteristicNotFoundException(characteristicId);
    }

    var characteristic = characteristicsMapper.toEntity(characteristicDto);
    characteristic.setId(characteristicId);
    characteristic = characteristicRepository.save(characteristic);

    return characteristicsMapper.toDto(characteristic);
  }

  @Override
  @Transactional
  public void deleteCharacteristic(UUID characteristicId) {
    if (!characteristicRepository.existsById(characteristicId)) {
      throw new CharacteristicNotFoundException(characteristicId);
    }
    characteristicRepository.deleteById(characteristicId);
  }

  @Override
  @Transactional
  public void exportAllResources() {
    var resources = resourceRepository.findAll();
    var resourceDtos = resources.stream().map(resourceMapper::toDto).toList();
    
    // Send bulk export to Kafka
    kafkaService.sendBulkExport(resourceDtos);
    
    log.info("Exported {} resources to Kafka", resources.size());
  }
}
