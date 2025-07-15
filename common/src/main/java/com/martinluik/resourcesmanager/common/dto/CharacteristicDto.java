package com.martinluik.resourcesmanager.common.dto;

import com.martinluik.resourcesmanager.common.constants.ValidationMessages;
import com.martinluik.resourcesmanager.common.enums.CharacteristicType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicDto {

  private UUID id;
  private UUID resourceId;

  @NotNull(message = ValidationMessages.CHARACTERISTIC_CODE_REQUIRED)
  @Size(max = 5, message = ValidationMessages.CHARACTERISTIC_CODE_MAX_SIZE)
  private String code;

  @NotNull(message = ValidationMessages.CHARACTERISTIC_TYPE_REQUIRED)
  private CharacteristicType type;
  
  @NotNull(message = ValidationMessages.CHARACTERISTIC_VALUE_REQUIRED)
  @Size(min = 1, max = 1000, message = ValidationMessages.CHARACTERISTIC_VALUE_SIZE_RANGE)
  private String value;
}
