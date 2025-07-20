package com.martinluik.resourcesmanager.dto;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.martinluik.resourcesmanager.constants.ValidationMessages;
import com.martinluik.resourcesmanager.enums.CharacteristicType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CharacteristicDto {

  @Nullable private UUID id;
  @Nullable private UUID resourceId;

  @NotNull(message = ValidationMessages.CHARACTERISTIC_CODE_REQUIRED)
  @Size(max = 5, message = ValidationMessages.CHARACTERISTIC_CODE_MAX_SIZE)
  private String code;

  @NotNull(message = ValidationMessages.CHARACTERISTIC_TYPE_REQUIRED)
  private CharacteristicType type;

  @NotNull(message = ValidationMessages.CHARACTERISTIC_VALUE_REQUIRED)
  @Size(min = 1, max = 1000, message = ValidationMessages.CHARACTERISTIC_VALUE_SIZE_RANGE)
  private String value;
}
