package com.martinluik.resourcesmanager.core.service;

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.service.LocationService;
import com.martinluik.resourcesmanager.core.mapper.LocationMapper;
import com.martinluik.resourcesmanager.core.repository.LocationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

  private final LocationRepository locationRepository;
  private final LocationMapper locationMapper;

  @Transactional(readOnly = true)
  public List<LocationDto> getAllLocations() {
    return locationRepository.findAll().stream().map(locationMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public LocationDto getLocation(UUID id) {
    return locationRepository.findById(id).map(locationMapper::toDto).orElse(null);
  }

  @Transactional
  public LocationDto createLocation(LocationDto dto) {
    var location = locationMapper.toEntity(dto);
    location = locationRepository.save(location);
    return locationMapper.toDto(location);
  }

  @Transactional
  public LocationDto updateLocation(UUID id, LocationDto dto) {
    if (!locationRepository.existsById(id)) {
      return null;
    }

    var location = locationMapper.toEntity(dto);
    location.setId(id);
    location = locationRepository.save(location);
    return locationMapper.toDto(location);
  }

  @Transactional
  public void deleteLocation(UUID id) {
    if (locationRepository.existsById(id)) {
      locationRepository.deleteById(id);
    }
  }
}
