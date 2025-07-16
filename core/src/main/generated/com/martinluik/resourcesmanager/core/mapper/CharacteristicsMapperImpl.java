package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.core.domain.Characteristic;
import com.martinluik.resourcesmanager.core.domain.Resource;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-16T17:09:24+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Microsoft)"
)
@Component
public class CharacteristicsMapperImpl implements CharacteristicsMapper {

    @Override
    public CharacteristicDto toDto(Characteristic entity) {
        if ( entity == null ) {
            return null;
        }

        CharacteristicDto characteristicDto = new CharacteristicDto();

        characteristicDto.setResourceId( entityResourceId( entity ) );
        characteristicDto.setId( entity.getId() );
        characteristicDto.setCode( entity.getCode() );
        characteristicDto.setType( entity.getType() );
        characteristicDto.setValue( entity.getValue() );

        return characteristicDto;
    }

    @Override
    public Characteristic toEntity(CharacteristicDto dto) {
        if ( dto == null ) {
            return null;
        }

        Characteristic characteristic = new Characteristic();

        characteristic.setId( dto.getId() );
        characteristic.setCode( dto.getCode() );
        characteristic.setType( dto.getType() );
        characteristic.setValue( dto.getValue() );

        return characteristic;
    }

    private UUID entityResourceId(Characteristic characteristic) {
        if ( characteristic == null ) {
            return null;
        }
        Resource resource = characteristic.getResource();
        if ( resource == null ) {
            return null;
        }
        UUID id = resource.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
