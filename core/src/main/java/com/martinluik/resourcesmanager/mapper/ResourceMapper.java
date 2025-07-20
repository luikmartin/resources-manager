package com.martinluik.resourcesmanager.mapper;

import com.martinluik.resourcesmanager.config.CommonMapperConfig;
import com.martinluik.resourcesmanager.domain.Resource;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import org.mapstruct.Mapper;

@Mapper(
    config = CommonMapperConfig.class,
    uses = {LocationMapper.class, CharacteristicMapper.class})
public interface ResourceMapper {

  ResourceDto toDto(Resource resource);

  Resource toEntity(ResourceDto dto);
}
