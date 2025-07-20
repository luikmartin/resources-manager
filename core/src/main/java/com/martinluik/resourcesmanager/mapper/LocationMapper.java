package com.martinluik.resourcesmanager.mapper;

import com.martinluik.resourcesmanager.config.CommonMapperConfig;
import com.martinluik.resourcesmanager.domain.Location;
import com.martinluik.resourcesmanager.dto.LocationDto;
import org.mapstruct.Mapper;

@Mapper(config = CommonMapperConfig.class)
public interface LocationMapper {

  LocationDto toDto(Location entity);

  Location toEntity(LocationDto dto);
}
