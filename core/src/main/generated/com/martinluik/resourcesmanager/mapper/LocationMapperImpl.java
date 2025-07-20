package com.martinluik.resourcesmanager.mapper;

import com.martinluik.resourcesmanager.domain.Location;
import com.martinluik.resourcesmanager.dto.LocationDto;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-20T10:54:04+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Microsoft)"
)
@Component
public class LocationMapperImpl implements LocationMapper {

    @Override
    public LocationDto toDto(Location entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        String streetAddress = null;
        String city = null;
        String postalCode = null;
        String countryCode = null;

        id = entity.getId();
        streetAddress = entity.getStreetAddress();
        city = entity.getCity();
        postalCode = entity.getPostalCode();
        countryCode = entity.getCountryCode();

        LocationDto locationDto = new LocationDto( id, streetAddress, city, postalCode, countryCode );

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
