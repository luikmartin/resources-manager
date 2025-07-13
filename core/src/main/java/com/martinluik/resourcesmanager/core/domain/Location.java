package com.martinluik.resourcesmanager.core.domain;

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
  @NotBlank(message = "Street address is required")
  @Size(max = 255, message = "Street address must be less than 255 characters")
  private String streetAddress;

  @Column(nullable = false)
  @NotBlank(message = "City is required")
  @Size(max = 100, message = "City must be less than 100 characters")
  private String city;

  @Column(nullable = false)
  @NotBlank(message = "Postal code is required")
  @Size(max = 20, message = "Postal code must be less than 20 characters")
  private String postalCode;

  @Column(nullable = false, length = 2)
  @NotBlank(message = "Country code is required")
  @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
  private String countryCode;
}
