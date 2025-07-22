package com.martinluik.resourcesmanager.rest;

import com.martinluik.resourcesmanager.domain.Characteristic;
import com.martinluik.resourcesmanager.domain.Location;
import com.martinluik.resourcesmanager.domain.Resource;
import com.martinluik.resourcesmanager.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import com.martinluik.resourcesmanager.enums.CharacteristicType;
import com.martinluik.resourcesmanager.enums.ResourceType;
import java.util.List;

public final class TestFixtures {

  public static final String LOCATION_STREET = "123 Test Street";
  public static final String LOCATION_CITY = "Test City";
  public static final String LOCATION_POSTAL = "12345";
  public static final String LOCATION_COUNTRY = "EE";
  public static final String LOCATION2_STREET = "456 New Street";
  public static final String LOCATION2_CITY = "New City";
  public static final String LOCATION2_POSTAL = "54321";
  public static final String LOCATION2_COUNTRY = "LV";

  public static final String CHARACTERISTIC_CODE = "TEST1";
  public static final String CHARACTERISTIC_CODE2 = "TEST2";
  public static final String CHARACTERISTIC_VALUE = "Fast Charging";
  public static final String CHARACTERISTIC_VALUE2 = "Updated Value";

  public static final ResourceType RESOURCE_TYPE = ResourceType.CONNECTION_POINT;
  public static final ResourceType RESOURCE_TYPE2 = ResourceType.METERING_POINT;
  public static final String RESOURCE_COUNTRY = "EE";
  public static final String RESOURCE_COUNTRY2 = "LV";

  public static Location constructLocation() {
    return Location.builder()
        .streetAddress(LOCATION_STREET)
        .city(LOCATION_CITY)
        .postalCode(LOCATION_POSTAL)
        .countryCode(LOCATION_COUNTRY)
        .build();
  }

  public static Location constructLocation2() {
    return Location.builder()
        .streetAddress(LOCATION2_STREET)
        .city(LOCATION2_CITY)
        .postalCode(LOCATION2_POSTAL)
        .countryCode(LOCATION2_COUNTRY)
        .build();
  }

  public static Resource constructResource(Location location) {
    return Resource.builder()
        .type(RESOURCE_TYPE)
        .countryCode(RESOURCE_COUNTRY)
        .location(location)
        .build();
  }

  public static Characteristic constructCharacteristic(Resource resource) {
    return Characteristic.builder()
        .code(CHARACTERISTIC_CODE)
        .type(CharacteristicType.CONSUMPTION_TYPE)
        .value(CHARACTERISTIC_VALUE)
        .resource(resource)
        .build();
  }

  public static LocationDto constructLocationDto1() {
    return LocationDto.builder()
        .streetAddress("123 First Street")
        .city("First City")
        .postalCode("11111")
        .countryCode("EE")
        .build();
  }

  public static LocationDto constructLocationDto2() {
    return LocationDto.builder()
        .streetAddress("456 Second Street")
        .city("Second City")
        .postalCode("22222")
        .countryCode("LV")
        .build();
  }

  public static ResourceDto constructResourceDto(
      LocationDto location, List<CharacteristicDto> characteristics) {
    return ResourceDto.builder()
        .type(RESOURCE_TYPE)
        .countryCode(RESOURCE_COUNTRY)
        .location(location)
        .characteristics(characteristics)
        .build();
  }

  public static ResourceDto constructResourceDto2(
      LocationDto location, List<CharacteristicDto> characteristics) {
    return ResourceDto.builder()
        .type(RESOURCE_TYPE2)
        .countryCode(RESOURCE_COUNTRY2)
        .location(location)
        .characteristics(characteristics)
        .build();
  }

  public static CharacteristicDto constructCharacteristicDto() {
    return CharacteristicDto.builder()
        .code(CHARACTERISTIC_CODE)
        .type(CharacteristicType.CONSUMPTION_TYPE)
        .value(CHARACTERISTIC_VALUE)
        .build();
  }
}
