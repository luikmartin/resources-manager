package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.core.domain.Characteristic;
import com.martinluik.resourcesmanager.core.domain.Resource;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-16T17:09:24+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Microsoft)"
)
@Component
public class ResourceMapperImpl implements ResourceMapper {

    private final LocationMapper locationMapper;
    private final CharacteristicsMapper characteristicsMapper;

    @Autowired
    public ResourceMapperImpl(LocationMapper locationMapper, CharacteristicsMapper characteristicsMapper) {

        this.locationMapper = locationMapper;
        this.characteristicsMapper = characteristicsMapper;
    }

    @Override
    public ResourceDto toDto(Resource resource) {
        if ( resource == null ) {
            return null;
        }

        ResourceDto resourceDto = new ResourceDto();

        resourceDto.setCharacteristics( characteristicListToCharacteristicDtoList( resource.getCharacteristics() ) );
        resourceDto.setLocation( locationMapper.toDto( resource.getLocation() ) );
        resourceDto.setId( resource.getId() );
        resourceDto.setType( resource.getType() );
        resourceDto.setCountryCode( resource.getCountryCode() );

        return resourceDto;
    }

    @Override
    public Resource toEntity(ResourceDto dto) {
        if ( dto == null ) {
            return null;
        }

        Resource resource = new Resource();

        resource.setId( dto.getId() );
        resource.setType( dto.getType() );
        resource.setCountryCode( dto.getCountryCode() );
        resource.setLocation( locationMapper.toEntity( dto.getLocation() ) );

        return resource;
    }

    protected List<CharacteristicDto> characteristicListToCharacteristicDtoList(List<Characteristic> list) {
        if ( list == null ) {
            return null;
        }

        List<CharacteristicDto> list1 = new ArrayList<CharacteristicDto>( list.size() );
        for ( Characteristic characteristic : list ) {
            list1.add( characteristicsMapper.toDto( characteristic ) );
        }

        return list1;
    }
}
