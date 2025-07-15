package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.core.domain.Characteristic;
import org.springframework.stereotype.Component;

@Component
public class CharacteristicsMapper {

  public CharacteristicDto toDto(Characteristic entity) {
    return CharacteristicDto.builder()
        .id(entity.getId())
        .resourceId(entity.getResource() != null ? entity.getResource().getId() : null)
        .code(entity.getCode())
        .type(entity.getType())
        .value(entity.getValue())
        .build();
  }

  public Characteristic toEntity(CharacteristicDto dto) {
    return Characteristic.builder()
        .id(dto.getId())
        .code(dto.getCode())
        .type(dto.getType())
        .value(dto.getValue())
        .build();
  }
}
