package com.martinluik.resourcesmanager.mapper;

import com.martinluik.resourcesmanager.config.CommonMapperConfig;
import com.martinluik.resourcesmanager.domain.Characteristic;
import com.martinluik.resourcesmanager.dto.CharacteristicDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CommonMapperConfig.class)
public interface CharacteristicMapper {

  @Mapping(target = "resourceId", source = "resource.id")
  CharacteristicDto toDto(Characteristic entity);

  @Mapping(target = "resource", ignore = true)
  Characteristic toEntity(CharacteristicDto dto);
}
