package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.core.domain.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

  public LocationDto toDto(Location entity) {
    return LocationDto.builder()
        .id(entity.getId())
        .streetAddress(entity.getStreetAddress())
        .city(entity.getCity())
        .postalCode(entity.getPostalCode())
        .countryCode(entity.getCountryCode())
        .build();
  }

  public Location toEntity(LocationDto dto) {
    return Location.builder()
        .id(dto.getId())
        .streetAddress(dto.getStreetAddress())
        .city(dto.getCity())
        .postalCode(dto.getPostalCode())
        .countryCode(dto.getCountryCode())
        .build();
  }
}
