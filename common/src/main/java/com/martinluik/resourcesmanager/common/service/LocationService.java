package com.martinluik.resourcesmanager.common.service;

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import java.util.List;
import java.util.UUID;

public interface LocationService {

  List<LocationDto> getAllLocations();

  LocationDto getLocation(UUID id);

  LocationDto createLocation(LocationDto dto);

  LocationDto updateLocation(UUID id, LocationDto dto);

  void deleteLocation(UUID id);
}
