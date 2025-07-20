package com.martinluik.resourcesmanager.service;

import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import com.martinluik.resourcesmanager.exception.ResourceNotFoundException;
import com.martinluik.resourcesmanager.domain.Characteristic;
import com.martinluik.resourcesmanager.domain.Resource;
import com.martinluik.resourcesmanager.mapper.LocationMapper;
import com.martinluik.resourcesmanager.mapper.ResourceMapper;
import com.martinluik.resourcesmanager.repository.CharacteristicRepository;
import com.martinluik.resourcesmanager.repository.LocationRepository;
import com.martinluik.resourcesmanager.repository.ResourceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final LocationRepository locationRepository;
  private final CharacteristicRepository characteristicRepository;
  private final ResourceMapper resourceMapper;
  private final LocationMapper locationMapper;
  private final KafkaService kafkaService;

  @Override
  @Transactional(readOnly = true)
  public List<ResourceDto> getAllResources() {
    return resourceRepository.findAll().stream().map(resourceMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ResourceDto getResource(UUID id) {
    Assert.notNull(id, "Resource ID cannot be null");

    return resourceRepository
        .findById(id)
        .map(resourceMapper::toDto)
        .orElseThrow(() -> new ResourceNotFoundException(id));
  }

  @Override
  @Transactional
  public ResourceDto createResource(ResourceDto dto) {
    Assert.notNull(dto, "Resource DTO cannot be null");

    var resource = resourceMapper.toEntity(dto);

    if (dto.getCharacteristics() != null && !dto.getCharacteristics().isEmpty()) {
      Resource finalResource = resource;
      var characteristics =
          dto.getCharacteristics().stream()
              .map(
                  characteristicDto -> {
                    var characteristic = new Characteristic();
                    characteristic.setCode(characteristicDto.getCode());
                    characteristic.setType(characteristicDto.getType());
                    characteristic.setValue(characteristicDto.getValue());
                    characteristic.setResource(finalResource);
                    return characteristic;
                  })
              .toList();
      resource.setCharacteristics(characteristics);
    }

    resource = resourceRepository.save(resource);
    var createdDto = resourceMapper.toDto(resource);

    kafkaService.sendResourceUpdate(createdDto);

    return createdDto;
  }

  @Override
  @Transactional
  public ResourceDto updateResource(ResourceDto dto) {
    Assert.notNull(dto, "Resource DTO cannot be null");
    Assert.notNull(dto.getId(), "Resource ID cannot be null for update");

    var existingResource =
        resourceRepository
            .findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException(dto.getId()));

    existingResource.setType(dto.getType());
    existingResource.setCountryCode(dto.getCountryCode());

    if (dto.getLocation() != null) {
      var location = locationMapper.toEntity(dto.getLocation());
      location.setId(existingResource.getLocation().getId());
      existingResource.setLocation(location);
    }

    if (dto.getCharacteristics() != null) {
      characteristicRepository.deleteAll(existingResource.getCharacteristics());

      if (!dto.getCharacteristics().isEmpty()) {
        var characteristics =
            dto.getCharacteristics().stream()
                .map(
                    characteristicDto -> {
                      var characteristic = new Characteristic();
                      characteristic.setCode(characteristicDto.getCode());
                      characteristic.setType(characteristicDto.getType());
                      characteristic.setValue(characteristicDto.getValue());
                      characteristic.setResource(existingResource);
                      return characteristic;
                    })
                .toList();

        var savedCharacteristics = characteristicRepository.saveAll(characteristics);
        existingResource.setCharacteristics(savedCharacteristics);
      } else {
        existingResource.setCharacteristics(new ArrayList<>());
      }
    }

    var updatedResource = resourceRepository.save(existingResource);
    var updatedDto = resourceMapper.toDto(updatedResource);

    kafkaService.sendResourceUpdate(updatedDto);

    return updatedDto;
  }

  @Override
  @Transactional
  public void deleteResource(UUID id) {
    Assert.notNull(id, "Resource ID cannot be null");

    if (!resourceRepository.existsById(id)) {
      throw new ResourceNotFoundException(id);
    }

    resourceRepository.deleteById(id);
  }

  @Override
  @Transactional
  public ResourceDto updateResourceLocation(UUID resourceId, LocationDto locationDto) {
    Assert.notNull(resourceId, "Resource ID cannot be null");
    Assert.notNull(locationDto, "Location DTO cannot be null");

    var resource =
        resourceRepository
            .findById(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException(resourceId));

    var location = locationMapper.toEntity(locationDto);
    location.setId(resource.getLocation().getId());
    location = locationRepository.save(location);

    resource.setLocation(location);
    resource = resourceRepository.save(resource);

    return resourceMapper.toDto(resource);
  }

  @Override
  @Transactional
  public void exportAllResources() {
    var resources = resourceRepository.findAll();
    var resourceDtos = resources.stream().map(resourceMapper::toDto).toList();

    kafkaService.sendBulkExport(resourceDtos);
  }
}
