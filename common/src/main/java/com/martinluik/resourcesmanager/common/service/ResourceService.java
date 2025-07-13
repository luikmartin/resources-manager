package com.martinluik.resourcesmanager.common.service;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import java.util.List;
import java.util.UUID;

public interface ResourceService {

  List<ResourceDto> getAllResources();

  ResourceDto getResource(UUID id);

  ResourceDto createResource(ResourceDto dto);

  ResourceDto updateResource(UUID id, ResourceDto dto);

  void deleteResource(UUID id);

  ResourceDto updateResourceLocation(UUID resourceId, LocationDto locationDto);

  CharacteristicDto addCharacteristic(UUID resourceId, CharacteristicDto characteristicDto);

  CharacteristicDto updateCharacteristic(
      UUID characteristicId, CharacteristicDto characteristicDto);

  void deleteCharacteristic(UUID characteristicId);

  void exportAllResources();
}
