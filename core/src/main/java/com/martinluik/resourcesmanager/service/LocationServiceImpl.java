package com.martinluik.resourcesmanager.service;

import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.exception.LocationNotFoundException;
import com.martinluik.resourcesmanager.mapper.LocationMapper;
import com.martinluik.resourcesmanager.repository.LocationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

  private final LocationRepository locationRepository;
  private final LocationMapper locationMapper;

  @Override
  @Transactional(readOnly = true)
  public List<LocationDto> getAllLocations() {
    return locationRepository.findAll().stream().map(locationMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public LocationDto getLocation(UUID id) {
    Assert.notNull(id, "Location ID cannot be null");

    return locationRepository
        .findById(id)
        .map(locationMapper::toDto)
        .orElseThrow(() -> new LocationNotFoundException(id));
  }

  @Override
  @Transactional
  public LocationDto createLocation(LocationDto dto) {
    Assert.notNull(dto, "Location DTO cannot be null");

    var location = locationMapper.toEntity(dto);
    var savedLocation = locationRepository.save(location);

    return locationMapper.toDto(savedLocation);
  }

  @Override
  @Transactional
  public LocationDto updateLocation(LocationDto dto) {
    Assert.notNull(dto, "Location DTO cannot be null");
    Assert.notNull(dto.getId(), "Location ID cannot be null for update");

    if (!locationRepository.existsById(dto.getId())) {
      throw new LocationNotFoundException(dto.getId());
    }

    var location = locationMapper.toEntity(dto);
    var updatedLocation = locationRepository.save(location);

    return locationMapper.toDto(updatedLocation);
  }

  @Override
  @Transactional
  public void deleteLocation(UUID id) {
    Assert.notNull(id, "Location ID cannot be null");

    if (!locationRepository.existsById(id)) {
      throw new LocationNotFoundException(id);
    }

    locationRepository.deleteById(id);
  }
}
