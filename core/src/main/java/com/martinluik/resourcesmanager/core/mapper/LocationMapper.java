package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.core.config.CommonMapperConfig;
import com.martinluik.resourcesmanager.core.domain.Location;
import org.mapstruct.Mapper;

@Mapper(config = CommonMapperConfig.class, componentModel = "spring")
public interface LocationMapper {

  LocationDto toDto(Location entity);

  Location toEntity(LocationDto dto);
}
