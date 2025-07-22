package com.martinluik.resourcesmanager.rest;

import static com.martinluik.resourcesmanager.rest.TestFixtures.CHARACTERISTIC_CODE2;
import static com.martinluik.resourcesmanager.rest.TestFixtures.CHARACTERISTIC_VALUE2;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructCharacteristic;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructLocation;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.martinluik.resourcesmanager.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.enums.CharacteristicType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CharacteristicsControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("updateCharacteristic_withValidData_returnsUpdatedCharacteristic")
  void updateCharacteristic_withValidData_returnsUpdatedCharacteristic() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic = constructCharacteristic(resource);
    characteristic = characteristicRepository.save(characteristic);

    var updateDto =
        CharacteristicDto.builder()
            .id(characteristic.getId())
            .resourceId(resource.getId())
            .code(CHARACTERISTIC_CODE2)
            .type(CharacteristicType.CHARGING_POINT)
            .value(CHARACTERISTIC_VALUE2)
            .build();

    // When
    var response =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                put(CharacteristicsController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)),
                200,
                MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var updatedCharacteristic = objectMapper.readValue(response, CharacteristicDto.class);
    assertThat(updatedCharacteristic)
        .extracting(
            CharacteristicDto::getId,
            CharacteristicDto::getResourceId,
            CharacteristicDto::getCode,
            CharacteristicDto::getType,
            CharacteristicDto::getValue)
        .containsExactly(
            characteristic.getId(),
            resource.getId(),
            "TEST2",
            CharacteristicType.CHARGING_POINT,
            "Updated Value");
  }

  @Test
  @DisplayName("updateCharacteristic_withNonExistentId_returns404")
  void updateCharacteristic_withNonExistentId_returns404() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var nonExistentId = UUID.randomUUID();
    var updateDto =
        CharacteristicDto.builder()
            .id(nonExistentId)
            .resourceId(resource.getId())
            .code("TEST2")
            .type(CharacteristicType.CHARGING_POINT)
            .value("Updated Value")
            .build();

    // When & Then
    mockMvc
        .perform(
            put(CharacteristicsController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("updateCharacteristic_withInvalidData_returnsBadRequest")
  void updateCharacteristic_withInvalidData_returnsBadRequest() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic = constructCharacteristic(resource);
    characteristic = characteristicRepository.save(characteristic);

    var updateDto =
        CharacteristicDto.builder().id(characteristic.getId()).resourceId(resource.getId()).build();

    // When & Then
    mockMvc
        .perform(
            put(CharacteristicsController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("updateCharacteristic_withInvalidCodeLength_returnsBadRequest")
  void updateCharacteristic_withInvalidCodeLength_returnsBadRequest() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic = constructCharacteristic(resource);
    characteristic = characteristicRepository.save(characteristic);

    var updateDto =
        CharacteristicDto.builder()
            .id(characteristic.getId())
            .resourceId(resource.getId())
            .code("TOOLONG")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Valid Value")
            .build();

    // When & Then
    mockMvc
        .perform(
            put(CharacteristicsController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("updateCharacteristic_withInvalidValueLength_returnsBadRequest")
  void updateCharacteristic_withInvalidValueLength_returnsBadRequest() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic = constructCharacteristic(resource);
    characteristic = characteristicRepository.save(characteristic);

    var longValue = "A".repeat(1001);
    var updateDto =
        CharacteristicDto.builder()
            .id(characteristic.getId())
            .resourceId(resource.getId())
            .code("TEST2")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value(longValue)
            .build();

    // When & Then
    mockMvc
        .perform(
            put(CharacteristicsController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("updateCharacteristic_withEmptyValue_returnsBadRequest")
  void updateCharacteristic_withEmptyValue_returnsBadRequest() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic = constructCharacteristic(resource);
    characteristic = characteristicRepository.save(characteristic);

    var updateDto =
        CharacteristicDto.builder()
            .id(characteristic.getId())
            .resourceId(resource.getId())
            .code("TEST2")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("")
            .build();

    // When & Then
    mockMvc
        .perform(
            put(CharacteristicsController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("updateCharacteristic_withNullId_returnsBadRequest")
  void updateCharacteristic_withNullId_returnsBadRequest() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic = constructCharacteristic(resource);
    characteristicRepository.save(characteristic);

    var updateDto =
        CharacteristicDto.builder()
            .resourceId(resource.getId())
            .code("TEST2")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Valid Value")
            .build();

    // When & Then
    mockMvc
        .perform(
            put(CharacteristicsController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("getAllCharacteristics_withExistingCharacteristics_returnsAllCharacteristics")
  void getAllCharacteristics_withExistingCharacteristics_returnsAllCharacteristics()
      throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);
    var resource = constructResource(location);
    resource = resourceRepository.save(resource);
    final var resourceId = resource.getId();
    var characteristic1 = constructCharacteristic(resource);
    characteristicRepository.save(characteristic1);
    var characteristic2 = constructCharacteristic(resource);
    characteristicRepository.save(characteristic2);

    // When & Then
    var response =
        mockMvc
            .perform(get(CharacteristicsController.API_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.List<CharacteristicDto> characteristics =
        objectMapper.readValue(
            response,
            objectMapper
                .getTypeFactory()
                .constructCollectionType(java.util.List.class, CharacteristicDto.class));
    assertThat(characteristics)
        .isNotEmpty()
        .allSatisfy(
            c -> {
              assertThat(c.getId()).isNotNull();
              assertThat(c.getCode()).isNotNull();
              assertThat(c.getType()).isNotNull();
              assertThat(c.getValue()).isNotNull();
            });
  }

  @Test
  @DisplayName("getCharacteristicById_withValidId_returnsCharacteristic")
  void getCharacteristicById_withValidId_returnsCharacteristic() throws Exception {
    // Given
    var location = constructLocation();

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic = constructCharacteristic(resource);
    characteristic = characteristicRepository.save(characteristic);

    // When & Then
    var response =
        mockMvc
            .perform(get(CharacteristicsController.API_URL + "/" + characteristic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    var foundCharacteristic = objectMapper.readValue(response, CharacteristicDto.class);
    assertThat(foundCharacteristic)
        .extracting(
            CharacteristicDto::getId,
            CharacteristicDto::getCode,
            CharacteristicDto::getType,
            CharacteristicDto::getValue)
        .containsExactly(
            characteristic.getId(), "TEST1", CharacteristicType.CONSUMPTION_TYPE, "Fast Charging");
  }

  @Test
  @DisplayName("getCharacteristicById_withNonExistentId_returns404")
  void getCharacteristicById_withNonExistentId_returns404() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc
        .perform(get(CharacteristicsController.API_URL + "/" + nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("getCharacteristicsByResourceId_withValidResourceId_returnsCharacteristics")
  void getCharacteristicsByResourceId_withValidResourceId_returnsCharacteristics()
      throws Exception {
    // Given
    var location = constructLocation();

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic1 = constructCharacteristic(resource);
    characteristicRepository.save(characteristic1);

    var characteristic2 = constructCharacteristic(resource);
    characteristicRepository.save(characteristic2);

    // When & Then
    var response =
        mockMvc
            .perform(get(CharacteristicsController.API_URL + "/resource/" + resource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.List<CharacteristicDto> resourceCharacteristics =
        objectMapper.readValue(
            response,
            objectMapper
                .getTypeFactory()
                .constructCollectionType(java.util.List.class, CharacteristicDto.class));
    assertThat(resourceCharacteristics).hasSize(2);
    assertThat(resourceCharacteristics.get(0).getResourceId()).isEqualTo(resource.getId());
    assertThat(resourceCharacteristics.get(1).getResourceId()).isEqualTo(resource.getId());
  }

  @Test
  @DisplayName(
      "getCharacteristicsByResourceId_withResourceIdHavingNoCharacteristics_returnsEmptyArray")
  void getCharacteristicsByResourceId_withResourceIdHavingNoCharacteristics_returnsEmptyArray()
      throws Exception {
    // Given
    var location = constructLocation();
    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    // When & Then
    var response =
        mockMvc
            .perform(get(CharacteristicsController.API_URL + "/resource/" + resource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.List<CharacteristicDto> resourceCharacteristics =
        objectMapper.readValue(
            response,
            objectMapper
                .getTypeFactory()
                .constructCollectionType(java.util.List.class, CharacteristicDto.class));
    assertThat(resourceCharacteristics).isEmpty();
  }

  @Test
  @DisplayName("createCharacteristic_withValidData_returnsCreatedCharacteristic")
  void createCharacteristic_withValidData_returnsCreatedCharacteristic() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);
    final var resourceId = resource.getId();

    var createDto =
        CharacteristicDto.builder()
            .code("NEW1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("New Characteristic")
            .build();

    // When
    var response =
        mockMvc
            .perform(
                post(CharacteristicsController.API_URL + "/resource/" + resourceId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdCharacteristic = objectMapper.readValue(response, CharacteristicDto.class);
    assertThat(createdCharacteristic)
        .extracting(
            CharacteristicDto::getId,
            CharacteristicDto::getResourceId,
            CharacteristicDto::getCode,
            CharacteristicDto::getType,
            CharacteristicDto::getValue)
        .satisfies(
            tuple -> {
              assertThat(tuple.get(0)).isNotNull();
              assertThat(tuple.get(1)).isEqualTo(resourceId);
              assertThat(tuple.get(2)).isEqualTo("NEW1");
              assertThat(tuple.get(3)).isEqualTo(CharacteristicType.CONSUMPTION_TYPE);
              assertThat(tuple.get(4)).isEqualTo("New Characteristic");
            });

    // Then
    var allCharacteristics = characteristicRepository.findAll();
    var resourceCharacteristics =
        allCharacteristics.stream()
            .filter(c -> c.getResource().getId().equals(resourceId))
            .toList();
    assertThat(resourceCharacteristics)
        .hasSize(1)
        .anyMatch(c -> c.getCode().equals("NEW1") && c.getValue().equals("New Characteristic"));
  }

  @Test
  @DisplayName("createCharacteristic_withInvalidData_returnsBadRequest")
  void createCharacteristic_withInvalidData_returnsBadRequest() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var createDto = CharacteristicDto.builder().code("NEW1").build();

    // When & Then
    mockMvc
        .perform(
            post(CharacteristicsController.API_URL + "/resource/" + resource.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("createCharacteristic_withNonExistentResourceId_returns404")
  void createCharacteristic_withNonExistentResourceId_returns404() throws Exception {
    // Given
    var nonExistentResourceId = UUID.randomUUID();
    var createDto =
        CharacteristicDto.builder()
            .code("NEW1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("New Characteristic")
            .build();

    // When & Then
    mockMvc
        .perform(
            post(CharacteristicsController.API_URL + "/resource/" + nonExistentResourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("deleteCharacteristic_withValidId_returnsNoContent")
  void deleteCharacteristic_withValidId_returnsNoContent() throws Exception {
    // Given
    var location = constructLocation();
    location = locationRepository.save(location);

    var resource = constructResource(location);
    resource = resourceRepository.save(resource);

    var characteristic = constructCharacteristic(resource);
    characteristic = characteristicRepository.save(characteristic);

    // When
    mockMvc
        .perform(delete(CharacteristicsController.API_URL + "/" + characteristic.getId()))
        .andExpect(status().isNoContent());

    // Then
    var deletedCharacteristic = characteristicRepository.findById(characteristic.getId());
    assertThat(deletedCharacteristic).isEmpty();
  }

  @Test
  @DisplayName("deleteCharacteristic_withNonExistentId_returns404")
  void deleteCharacteristic_withNonExistentId_returns404() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc
        .perform(delete(CharacteristicsController.API_URL + "/" + nonExistentId))
        .andExpect(status().isNotFound());
  }
}
