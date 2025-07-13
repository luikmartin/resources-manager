package com.martinluik.resourcesmanager.common.dto;

import com.martinluik.resourcesmanager.common.enums.ResourceType;
import java.util.List;
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
public class ResourceDto {

  @Nullable private UUID id;
  @NotNull private ResourceType type;
  @NotNull private String countryCode;
  @NotNull private LocationDto location;
  @NotNull private List<CharacteristicDto> characteristics;
}
