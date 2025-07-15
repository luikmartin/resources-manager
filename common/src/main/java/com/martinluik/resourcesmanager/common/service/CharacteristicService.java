package com.martinluik.resourcesmanager.common.service;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import java.util.List;
import java.util.UUID;

public interface CharacteristicService {

  List<CharacteristicDto> getAllCharacteristics();

  CharacteristicDto getCharacteristic(UUID id);

  CharacteristicDto createCharacteristic(CharacteristicDto dto, UUID resourceId);

  CharacteristicDto updateCharacteristic(CharacteristicDto dto);

  void deleteCharacteristic(UUID id);

  List<CharacteristicDto> getCharacteristicsByResourceId(UUID resourceId);
}
