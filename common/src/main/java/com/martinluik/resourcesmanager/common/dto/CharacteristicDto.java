package com.martinluik.resourcesmanager.common.dto;

import com.martinluik.resourcesmanager.common.enums.CharacteristicType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicDto {

  @Nullable private UUID id;
  @Nullable private UUID resourceId;

  @NotNull
  @Length(max = 5)
  private String code;

  @NotNull private CharacteristicType type;
  @NotNull private String value;
}
