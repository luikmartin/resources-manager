package com.martinluik.resourcesmanager.common.dto;

import com.martinluik.resourcesmanager.common.constants.ValidationMessages;
import com.martinluik.resourcesmanager.common.enums.ResourceType;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {

  private UUID id;
  
  @NotNull(message = ValidationMessages.RESOURCE_TYPE_REQUIRED)
  private ResourceType type;
  
  @NotNull(message = ValidationMessages.COUNTRY_CODE_REQUIRED)
  @Pattern(regexp = "^[A-Z]{2}$", message = ValidationMessages.COUNTRY_CODE_ISO_PATTERN)
  private String countryCode;
  
  @NotNull(message = ValidationMessages.LOCATION_REQUIRED)
  @Valid
  private LocationDto location;
  
  @NotNull(message = ValidationMessages.CHARACTERISTICS_REQUIRED)
  @Size(min = 1, message = ValidationMessages.CHARACTERISTICS_MIN_SIZE)
  @Valid
  private List<CharacteristicDto> characteristics;
}
