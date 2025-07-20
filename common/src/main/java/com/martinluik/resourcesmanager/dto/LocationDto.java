package com.martinluik.resourcesmanager.dto;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.martinluik.resourcesmanager.constants.ValidationMessages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

  @Nullable private UUID id;

  @NotNull(message = ValidationMessages.STREET_ADDRESS_REQUIRED)
  @Size(min = 1, max = 255, message = ValidationMessages.STREET_ADDRESS_SIZE_RANGE)
  private String streetAddress;

  @NotNull(message = ValidationMessages.CITY_REQUIRED)
  @Size(min = 1, max = 100, message = ValidationMessages.CITY_SIZE_RANGE)
  private String city;

  @NotNull(message = ValidationMessages.POSTAL_CODE_REQUIRED)
  @Pattern(regexp = "^[A-Z0-9\\s-]{3,10}$", message = ValidationMessages.POSTAL_CODE_PATTERN)
  private String postalCode;

  @NotNull(message = ValidationMessages.COUNTRY_CODE_REQUIRED)
  @Pattern(regexp = "^[A-Z]{2}$", message = ValidationMessages.COUNTRY_CODE_ISO_PATTERN)
  private String countryCode;
}
