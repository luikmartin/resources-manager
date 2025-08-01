package com.martinluik.resourcesmanager.service;

import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import java.util.List;
import java.util.UUID;

public interface ResourceService {

  List<ResourceDto> getAllResources();

  ResourceDto getResource(UUID id);

  ResourceDto createResource(ResourceDto dto);

  ResourceDto updateResource(ResourceDto dto);

  void deleteResource(UUID id);

  ResourceDto updateResourceLocation(UUID resourceId, LocationDto locationDto);

  void exportAllResources();
}
