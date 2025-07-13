package com.martinluik.resourcesmanager.common.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

  @Nullable private UUID id;
  @NotNull private String streetAddress;
  @NotNull private String city;
  @NotNull private String postalCode;
  @NotNull private String countryCode;
}
