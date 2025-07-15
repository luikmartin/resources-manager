package com.martinluik.resourcesmanager.core.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.core.domain.Location;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LocationMapperTest {

  private final LocationMapper mapper = new LocationMapper();

  @Test
  void toDto_validLocation_returnsCorrectlyMappedDto() {
    // Given
    Location location =
        Location.builder()
            .id(UUID.randomUUID())
            .streetAddress("123 Main St")
            .city("Tallinn")
            .postalCode("10115")
            .countryCode("EE")
            .build();

    // When
    LocationDto dto = mapper.toDto(location);

    // Then
    assertNotNull(dto);
    assertEquals(location.getId(), dto.getId());
    assertEquals(location.getStreetAddress(), dto.getStreetAddress());
    assertEquals(location.getCity(), dto.getCity());
    assertEquals(location.getPostalCode(), dto.getPostalCode());
    assertEquals(location.getCountryCode(), dto.getCountryCode());
  }

  @Test
  void toEntity_validLocationDto_returnsCorrectlyMappedEntity() {
    // Given
    LocationDto dto =
        LocationDto.builder()
            .id(UUID.randomUUID())
            .streetAddress("456 Oak Ave")
            .city("Riga")
            .postalCode("1050")
            .countryCode("LV")
            .build();

    // When
    Location location = mapper.toEntity(dto);

    // Then
    assertNotNull(location);
    assertEquals(dto.getId(), location.getId());
    assertEquals(dto.getStreetAddress(), location.getStreetAddress());
    assertEquals(dto.getCity(), location.getCity());
    assertEquals(dto.getPostalCode(), location.getPostalCode());
    assertEquals(dto.getCountryCode(), location.getCountryCode());
  }
}
