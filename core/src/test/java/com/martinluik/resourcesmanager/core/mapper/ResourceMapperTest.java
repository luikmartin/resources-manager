package com.martinluik.resourcesmanager.core.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.enums.ResourceType;
import com.martinluik.resourcesmanager.core.domain.Characteristic;
import com.martinluik.resourcesmanager.core.domain.Location;
import com.martinluik.resourcesmanager.core.domain.Resource;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ResourceMapperTest {

  private final ResourceMapper mapper =
      new ResourceMapper(new LocationMapper(), new CharacteristicsMapper());

  @Test
  void toDto_validResource_returnsCorrectlyMappedDto() {
    // Given
    Resource resource =
        Resource.builder()
            .id(UUID.randomUUID())
            .type(ResourceType.METERING_POINT)
            .countryCode("EE")
            .location(Location.builder().id(UUID.randomUUID()).build())
            .characteristics(List.of(Characteristic.builder().id(UUID.randomUUID()).build()))
            .build();

    // When
    ResourceDto dto = mapper.toDto(resource);

    // Then
    assertNotNull(dto);
    assertEquals(resource.getId(), dto.getId());
    assertEquals(resource.getType(), dto.getType());
    assertEquals(resource.getCountryCode(), dto.getCountryCode());
    assertNotNull(dto.getLocation());
    assertNotNull(dto.getCharacteristics());
  }

  @Test
  void toEntity_validResourceDto_returnsCorrectlyMappedEntity() {
    // Given
    ResourceDto dto =
        ResourceDto.builder()
            .id(UUID.randomUUID())
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("LV")
            .location(LocationDto.builder().id(UUID.randomUUID()).build())
            .characteristics(List.of(CharacteristicDto.builder().id(UUID.randomUUID()).build()))
            .build();

    // When
    Resource resource = mapper.toEntity(dto);

    // Then
    assertNotNull(resource);
    assertEquals(dto.getId(), resource.getId());
    assertEquals(dto.getType(), resource.getType());
    assertEquals(dto.getCountryCode(), resource.getCountryCode());
    assertNotNull(resource.getLocation());
    assertNull(
        resource
            .getCharacteristics()); // Should be ignored due to @Mapping(target = "characteristics",
    // ignore = true)
  }
}
