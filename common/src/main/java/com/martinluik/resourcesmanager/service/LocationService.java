package com.martinluik.resourcesmanager.service;

import com.martinluik.resourcesmanager.dto.LocationDto;
import java.util.List;
import java.util.UUID;

public interface LocationService {

  List<LocationDto> getAllLocations();

  LocationDto getLocation(UUID id);

  LocationDto createLocation(LocationDto dto);

  LocationDto updateLocation(LocationDto dto);

  void deleteLocation(UUID id);
}
