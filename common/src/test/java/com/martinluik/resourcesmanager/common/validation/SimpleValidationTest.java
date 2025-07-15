package com.martinluik.resourcesmanager.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.martinluik.resourcesmanager.common.constants.ValidationMessages;
import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.enums.CharacteristicType;
import com.martinluik.resourcesmanager.common.enums.ResourceType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

class SimpleValidationTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validateResource_nullType_returnsValidationError() {
    // Given
    var invalidResource = ResourceDto.builder()
        .type(null)
        .countryCode("US")
        .location(createValidLocation())
        .characteristics(List.of(createValidCharacteristic()))
        .build();

    // When
    var violations = validator.validate(invalidResource);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.stream())
        .anyMatch(v -> v.getMessage().equals(ValidationMessages.RESOURCE_TYPE_REQUIRED));
  }

  @Test
  void validateResource_multipleInvalidFields_returnsMultipleValidationErrors() {
    // Given
    var invalidResource =
        ResourceDto.builder()
            .type(null)
            .countryCode("INVALID")
            .location(createValidLocation())
            .characteristics(List.of(createValidCharacteristic()))
            .build();

    // When
    var violations = validator.validate(invalidResource);

    // Then
    assertThat(violations).hasSizeGreaterThan(1);
    assertThat(violations.stream())
        .anyMatch(v -> v.getMessage().equals(ValidationMessages.RESOURCE_TYPE_REQUIRED));
    assertThat(violations.stream())
        .anyMatch(v -> v.getPropertyPath().toString().equals("countryCode"));
  }

  @Test
  void validateResource_validResource_returnsNoValidationErrors() {
    // Given
    var validResource =
        ResourceDto.builder()
            .type(ResourceType.METERING_POINT)
            .countryCode("US")
            .location(createValidLocation())
            .characteristics(List.of(createValidCharacteristic()))
            .build();

    // When
    var violations = validator.validate(validResource);

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  void validateResource_nullLocation_returnsValidationError() {
    // Given
    var invalidResource =
        ResourceDto.builder()
            .type(ResourceType.METERING_POINT)
            .countryCode("US")
            .location(null)
            .characteristics(List.of(createValidCharacteristic()))
            .build();

    // When
    var violations = validator.validate(invalidResource);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.stream())
        .anyMatch(v -> v.getMessage().equals(ValidationMessages.LOCATION_REQUIRED));
  }

  @Test
  void validateResource_nullCharacteristics_returnsValidationError() {
    // Given
    var invalidResource =
        ResourceDto.builder()
            .type(ResourceType.METERING_POINT)
            .countryCode("US")
            .location(createValidLocation())
            .characteristics(null)
            .build();

    // When
    var violations = validator.validate(invalidResource);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.stream())
        .anyMatch(v -> v.getMessage().equals(ValidationMessages.CHARACTERISTICS_REQUIRED));
  }

  @Test
  void validateResource_emptyCharacteristics_returnsValidationError() {
    // Given
    var invalidResource =
        ResourceDto.builder()
            .type(ResourceType.METERING_POINT)
            .countryCode("US")
            .location(createValidLocation())
            .characteristics(List.of())
            .build();

    // When
    var violations = validator.validate(invalidResource);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.stream())
        .anyMatch(v -> v.getMessage().equals(ValidationMessages.CHARACTERISTICS_MIN_SIZE));
  }

  private LocationDto createValidLocation() {
    return LocationDto.builder()
        .streetAddress("123 Main St")
        .city("New York")
        .postalCode("10001")
        .countryCode("US")
        .build();
  }

  private CharacteristicDto createValidCharacteristic() {
    return CharacteristicDto.builder()
        .code("CONS")
        .type(CharacteristicType.CONSUMPTION_TYPE)
        .value("ELECTRICITY")
        .build();
  }
}
