package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.core.config.CommonMapperConfig;
import com.martinluik.resourcesmanager.core.domain.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    config = CommonMapperConfig.class,
    componentModel = "spring",
    uses = {LocationMapper.class, CharacteristicsMapper.class})
public interface ResourceMapper {

  @Mapping(target = "characteristics", source = "characteristics")
  @Mapping(target = "location", source = "location")
  ResourceDto toDto(Resource resource);

  @Mapping(target = "characteristics", ignore = true)
  Resource toEntity(ResourceDto dto);
}
