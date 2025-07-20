package com.martinluik.resourcesmanager.mapper;

import com.martinluik.resourcesmanager.domain.Characteristic;
import com.martinluik.resourcesmanager.domain.Resource;
import com.martinluik.resourcesmanager.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import com.martinluik.resourcesmanager.enums.ResourceType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-20T10:54:03+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Microsoft)"
)
@Component
public class ResourceMapperImpl implements ResourceMapper {

    private final LocationMapper locationMapper;
    private final CharacteristicMapper characteristicMapper;

    @Autowired
    public ResourceMapperImpl(LocationMapper locationMapper, CharacteristicMapper characteristicMapper) {

        this.locationMapper = locationMapper;
        this.characteristicMapper = characteristicMapper;
    }

    @Override
    public ResourceDto toDto(Resource resource) {
        if ( resource == null ) {
            return null;
        }

        UUID id = null;
        ResourceType type = null;
        String countryCode = null;
        LocationDto location = null;
        List<CharacteristicDto> characteristics = null;

        id = resource.getId();
        type = resource.getType();
        countryCode = resource.getCountryCode();
        location = locationMapper.toDto( resource.getLocation() );
        characteristics = characteristicListToCharacteristicDtoList( resource.getCharacteristics() );

        ResourceDto resourceDto = new ResourceDto( id, type, countryCode, location, characteristics );

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
        resource.setCharacteristics( characteristicDtoListToCharacteristicList( dto.getCharacteristics() ) );

        return resource;
    }

    protected List<CharacteristicDto> characteristicListToCharacteristicDtoList(List<Characteristic> list) {
        if ( list == null ) {
            return null;
        }

        List<CharacteristicDto> list1 = new ArrayList<CharacteristicDto>( list.size() );
        for ( Characteristic characteristic : list ) {
            list1.add( characteristicMapper.toDto( characteristic ) );
        }

        return list1;
    }

    protected List<Characteristic> characteristicDtoListToCharacteristicList(List<CharacteristicDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Characteristic> list1 = new ArrayList<Characteristic>( list.size() );
        for ( CharacteristicDto characteristicDto : list ) {
            list1.add( characteristicMapper.toEntity( characteristicDto ) );
        }

        return list1;
    }
}
