package com.martinluik.resourcesmanager.core.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.enums.CharacteristicType;
import com.martinluik.resourcesmanager.core.domain.Characteristic;
import com.martinluik.resourcesmanager.core.domain.Resource;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CharacteristicsMapperTest {

  private final CharacteristicsMapper mapper = new CharacteristicsMapper();

  @Test
  void toDto_validCharacteristic_returnsCorrectlyMappedDto() {
    // Given
    Characteristic characteristic =
        Characteristic.builder()
            .id(UUID.randomUUID())
            .code("CONS")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("RESIDENTIAL")
            .resource(Resource.builder().id(UUID.randomUUID()).build())
            .build();

    // When
    CharacteristicDto dto = mapper.toDto(characteristic);

    // Then
    assertNotNull(dto);
    assertEquals(characteristic.getId(), dto.getId());
    assertEquals(characteristic.getResource().getId(), dto.getResourceId());
    assertEquals(characteristic.getCode(), dto.getCode());
    assertEquals(characteristic.getType(), dto.getType());
    assertEquals(characteristic.getValue(), dto.getValue());
  }

  @Test
  void toEntity_validCharacteristicDto_returnsCorrectlyMappedEntity() {
    // Given
    CharacteristicDto dto =
        CharacteristicDto.builder()
            .id(UUID.randomUUID())
            .resourceId(UUID.randomUUID())
            .code("CHRG")
            .type(CharacteristicType.CHARGING_POINT)
            .value("FAST_CHARGING")
            .build();

    // When
    Characteristic characteristic = mapper.toEntity(dto);

    // Then
    assertNotNull(characteristic);
    assertEquals(dto.getId(), characteristic.getId());
    assertNull(
        characteristic
            .getResource()); // Should be ignored due to @Mapping(target = "resource", ignore =
    // true)
    assertEquals(dto.getCode(), characteristic.getCode());
    assertEquals(dto.getType(), characteristic.getType());
    assertEquals(dto.getValue(), characteristic.getValue());
  }
}
