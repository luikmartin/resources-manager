package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.core.domain.Resource;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ResourceMapper {

  private final LocationMapper locationMapper;
  private final CharacteristicsMapper characteristicsMapper;

  public ResourceDto toDto(Resource resource) {
    if (resource == null) {
      return null;
    }

    List<CharacteristicDto> characteristics = null;
    if (resource.getCharacteristics() != null) {
      characteristics =
          resource.getCharacteristics().stream().map(characteristicsMapper::toDto).toList();
    }

    return ResourceDto.builder()
        .id(resource.getId())
        .type(resource.getType())
        .countryCode(resource.getCountryCode())
        .location(locationMapper.toDto(resource.getLocation()))
        .characteristics(characteristics)
        .build();
  }

  public Resource toEntity(ResourceDto dto) {
    return dto == null
        ? null
        : Resource.builder()
            .id(dto.getId())
            .type(dto.getType())
            .countryCode(dto.getCountryCode())
            .location(locationMapper.toEntity(dto.getLocation()))
            .build();
  }
}
