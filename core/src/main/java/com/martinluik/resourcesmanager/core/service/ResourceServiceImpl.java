package com.martinluik.resourcesmanager.core.service;

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.exception.ResourceNotFoundException;
import com.martinluik.resourcesmanager.common.service.KafkaService;
import com.martinluik.resourcesmanager.common.service.ResourceService;
import com.martinluik.resourcesmanager.core.mapper.LocationMapper;
import com.martinluik.resourcesmanager.core.mapper.ResourceMapper;
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
    var resource =
        resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    return resourceMapper.toDto(resource);
  }

  @Override
  @Transactional
  public ResourceDto createResource(ResourceDto dto) {
    var resource = resourceMapper.toEntity(dto);
    resource = resourceRepository.save(resource);
    var createdDto = resourceMapper.toDto(resource);

    kafkaService.sendResourceUpdate(createdDto);

    return createdDto;
  }

  @Override
  @Transactional
  public ResourceDto updateResource(ResourceDto dto) {
    if (dto.getId() == null) {
      throw new IllegalArgumentException("Resource ID cannot be null for update");
    }

    if (!resourceRepository.existsById(dto.getId())) {
      throw new ResourceNotFoundException(dto.getId());
    }

    var resource = resourceMapper.toEntity(dto);
    resource = resourceRepository.save(resource);
    var updatedDto = resourceMapper.toDto(resource);

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
