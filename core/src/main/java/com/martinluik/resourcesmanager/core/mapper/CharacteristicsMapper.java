package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.core.config.CommonMapperConfig;
import com.martinluik.resourcesmanager.core.domain.Characteristic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CommonMapperConfig.class, componentModel = "spring")
public interface CharacteristicsMapper {

  @Mapping(target = "resourceId", source = "resource.id")
  CharacteristicDto toDto(Characteristic entity);

  @Mapping(target = "resource", ignore = true)
  Characteristic toEntity(CharacteristicDto dto);
}
