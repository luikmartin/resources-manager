package com.martinluik.resourcesmanager.core.mapper;

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.core.domain.Location;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-16T19:05:35+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Microsoft)"
)
@Component
public class LocationMapperImpl implements LocationMapper {

    @Override
    public LocationDto toDto(Location entity) {
        if ( entity == null ) {
            return null;
        }

        LocationDto locationDto = new LocationDto();

        locationDto.setId( entity.getId() );
        locationDto.setStreetAddress( entity.getStreetAddress() );
        locationDto.setCity( entity.getCity() );
        locationDto.setPostalCode( entity.getPostalCode() );
        locationDto.setCountryCode( entity.getCountryCode() );

        return locationDto;
    }

    @Override
    public Location toEntity(LocationDto dto) {
        if ( dto == null ) {
            return null;
        }

        Location location = new Location();

        location.setId( dto.getId() );
        location.setStreetAddress( dto.getStreetAddress() );
        location.setCity( dto.getCity() );
        location.setPostalCode( dto.getPostalCode() );
        location.setCountryCode( dto.getCountryCode() );

        return location;
    }
}
