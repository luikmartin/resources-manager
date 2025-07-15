package com.martinluik.resourcesmanager.core.domain;

import com.martinluik.resourcesmanager.common.constants.ValidationMessages;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

  @Id @GeneratedValue private UUID id;

  @Column(nullable = false)
  @NotBlank(message = ValidationMessages.STREET_ADDRESS_REQUIRED)
  @Size(max = 255, message = ValidationMessages.STREET_ADDRESS_MAX_SIZE)
  private String streetAddress;

  @Column(nullable = false)
  @NotBlank(message = ValidationMessages.CITY_REQUIRED)
  @Size(max = 100, message = ValidationMessages.CITY_MAX_SIZE)
  private String city;

  @Column(nullable = false)
  @NotBlank(message = ValidationMessages.POSTAL_CODE_REQUIRED)
  @Size(max = 20, message = ValidationMessages.POSTAL_CODE_MAX_SIZE)
  private String postalCode;

  @Column(nullable = false, length = 2)
  @NotBlank(message = ValidationMessages.COUNTRY_CODE_REQUIRED)
  @Size(min = 2, max = 2, message = ValidationMessages.COUNTRY_CODE_EXACT_SIZE)
  private String countryCode;
}
