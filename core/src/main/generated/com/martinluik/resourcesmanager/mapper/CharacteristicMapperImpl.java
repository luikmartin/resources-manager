package com.martinluik.resourcesmanager.mapper;

import com.martinluik.resourcesmanager.domain.Characteristic;
import com.martinluik.resourcesmanager.domain.Resource;
import com.martinluik.resourcesmanager.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.enums.CharacteristicType;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-20T10:54:04+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Microsoft)"
)
@Component
public class CharacteristicMapperImpl implements CharacteristicMapper {

    @Override
    public CharacteristicDto toDto(Characteristic entity) {
        if ( entity == null ) {
            return null;
        }

        UUID resourceId = null;
        UUID id = null;
        String code = null;
        CharacteristicType type = null;
        String value = null;

        resourceId = entityResourceId( entity );
        id = entity.getId();
        code = entity.getCode();
        type = entity.getType();
        value = entity.getValue();

        CharacteristicDto characteristicDto = new CharacteristicDto( id, resourceId, code, type, value );

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
