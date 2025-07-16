package com.martinluik.resourcesmanager.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.enums.CharacteristicType;
import com.martinluik.resourcesmanager.common.enums.ResourceType;
import com.martinluik.resourcesmanager.core.domain.Characteristic;
import com.martinluik.resourcesmanager.core.domain.Location;
import com.martinluik.resourcesmanager.core.domain.Resource;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CharacteristicsControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName(
      "Should successfully update characteristic with valid data and persist changes to database")
  void updateCharacteristic_WithValidData_ShouldReturnUpdatedCharacteristic() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
    characteristic = characteristicRepository.save(characteristic);

    var updateDto =
        CharacteristicDto.builder()
            .id(characteristic.getId())
            .resourceId(resource.getId())
            .code("TEST2")
            .type(CharacteristicType.CHARGING_POINT)
            .value("Updated Value")
            .build();

    // When
    mockMvc
        .perform(
            put(CharacteristicsController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(characteristic.getId().toString()))
        .andExpect(jsonPath("$.resourceId").value(resource.getId().toString()))
        .andExpect(jsonPath("$.code").value("TEST2"))
        .andExpect(jsonPath("$.type").value("CHARGING_POINT"))
        .andExpect(jsonPath("$.value").value("Updated Value"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    // Then
    var updatedCharacteristic =
        characteristicRepository.findById(characteristic.getId()).orElse(null);
    assertThat(updatedCharacteristic).isNotNull();
    assertThat(updatedCharacteristic.getCode()).isEqualTo("TEST2");
    assertThat(updatedCharacteristic.getType()).isEqualTo(CharacteristicType.CHARGING_POINT);
    assertThat(updatedCharacteristic.getValue()).isEqualTo("Updated Value");

    assertThat(updatedCharacteristic.getResource().getId()).isEqualTo(resource.getId());
  }

  @Test
  @DisplayName("Should return 404 when trying to update characteristic with non-existent ID")
  void updateCharacteristic_WithNonExistentId_ShouldReturnNotFound() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
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
  @DisplayName(
      "Should return 400 when trying to update characteristic with missing required fields")
  void updateCharacteristic_WithInvalidData_ShouldReturnBadRequest() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
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
  @DisplayName(
      "Should return 400 when trying to update characteristic with code exceeding maximum length")
  void updateCharacteristic_WithInvalidCodeLength_ShouldReturnBadRequest() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
    characteristic = characteristicRepository.save(characteristic);

    var updateDto =
        CharacteristicDto.builder()
            .id(characteristic.getId())
            .resourceId(resource.getId())
            .code("TOOLONG") // 7 characters, max is 5
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
  @DisplayName(
      "Should return 400 when trying to update characteristic with value exceeding maximum length")
  void updateCharacteristic_WithInvalidValueLength_ShouldReturnBadRequest() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
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
  @DisplayName("Should return 400 when trying to update characteristic with empty value")
  void updateCharacteristic_WithEmptyValue_ShouldReturnBadRequest() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
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
  @DisplayName("Should return 400 when trying to update characteristic with null ID")
  void updateCharacteristic_WithNullId_ShouldReturnBadRequest() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
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
  @DisplayName("Should return all characteristics")
  void getAllCharacteristics_ShouldReturnAllCharacteristics() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);
    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);
    var characteristic1 =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
    characteristicRepository.save(characteristic1);
    var characteristic2 =
        Characteristic.builder()
            .code("TEST2")
            .type(CharacteristicType.CHARGING_POINT)
            .value("Slow Charging")
            .resource(resource)
            .build();
    characteristicRepository.save(characteristic2);

    // When & Then
    mockMvc
        .perform(get(CharacteristicsController.API_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].code").exists())
        .andExpect(jsonPath("$[0].type").exists())
        .andExpect(jsonPath("$[0].value").exists());
  }

  @Test
  @DisplayName("Should return characteristic when valid ID is provided")
  void getCharacteristicById_WithValidId_ShouldReturnCharacteristic() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
    characteristic = characteristicRepository.save(characteristic);

    // When & Then
    mockMvc
        .perform(get(CharacteristicsController.API_URL + "/" + characteristic.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(characteristic.getId().toString()))
        .andExpect(jsonPath("$.code").value("TEST1"))
        .andExpect(jsonPath("$.type").value("CONSUMPTION_TYPE"))
        .andExpect(jsonPath("$.value").value("Fast Charging"));
  }

  @Test
  @DisplayName("Should return 404 when characteristic ID does not exist")
  void getCharacteristicById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc
        .perform(get(CharacteristicsController.API_URL + "/" + nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return characteristics for valid resource ID")
  void getCharacteristicsByResourceId_WithValidResourceId_ShouldReturnCharacteristics()
      throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic1 =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
    characteristicRepository.save(characteristic1);

    var characteristic2 =
        Characteristic.builder()
            .code("TEST2")
            .type(CharacteristicType.CHARGING_POINT)
            .value("Slow Charging")
            .resource(resource)
            .build();
    characteristicRepository.save(characteristic2);

    // When & Then
    mockMvc
        .perform(get(CharacteristicsController.API_URL + "/resource/" + resource.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(2)))
        .andExpect(jsonPath("$[0].resourceId").value(resource.getId().toString()))
        .andExpect(jsonPath("$[1].resourceId").value(resource.getId().toString()));
  }

  @Test
  @DisplayName("Should return empty array when resource ID has no characteristics")
  void getCharacteristicsByResourceId_WithResourceIdHavingNoCharacteristics_ShouldReturnEmptyArray()
      throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("456 Another Street")
            .city("Another City")
            .postalCode("54321")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);
    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    // When & Then
    mockMvc
        .perform(get(CharacteristicsController.API_URL + "/resource/" + resource.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(0)));
  }

  @Test
  @DisplayName("Should successfully create characteristic with valid data")
  void createCharacteristic_WithValidData_ShouldReturnCreatedCharacteristic() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var createDto =
        CharacteristicDto.builder()
            .code("NEW1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("New Characteristic")
            .build();

    // When
    mockMvc
        .perform(
            post(CharacteristicsController.API_URL + "/resource/" + resource.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.resourceId").value(resource.getId().toString()))
        .andExpect(jsonPath("$.code").value("NEW1"))
        .andExpect(jsonPath("$.type").value("CONSUMPTION_TYPE"))
        .andExpect(jsonPath("$.value").value("New Characteristic"));

    // Then
    var allCharacteristics = characteristicRepository.findAll();
    Resource finalResource = resource;
    var resourceCharacteristics =
        allCharacteristics.stream()
            .filter(c -> c.getResource().getId().equals(finalResource.getId()))
            .toList();
    assertThat(resourceCharacteristics).hasSize(1);
    assertThat(resourceCharacteristics.stream())
        .anyMatch(c -> c.getCode().equals("NEW1") && c.getValue().equals("New Characteristic"));
  }

  @Test
  @DisplayName("Should return 400 when creating characteristic with invalid data")
  void createCharacteristic_WithInvalidData_ShouldReturnBadRequest() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
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
  @DisplayName("Should return 400 when creating characteristic with non-existent resource ID")
  void createCharacteristic_WithNonExistentResourceId_ShouldReturnBadRequest() throws Exception {
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
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should successfully delete characteristic with valid ID")
  void deleteCharacteristic_WithValidId_ShouldReturnNoContent() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("123 Test Street")
            .city("Test City")
            .postalCode("12345")
            .countryCode("EE")
            .build();
    location = locationRepository.save(location);

    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .build();
    resource = resourceRepository.save(resource);

    var characteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(resource)
            .build();
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
  @DisplayName("Should return 404 when trying to delete characteristic with non-existent ID")
  void deleteCharacteristic_WithNonExistentId_ShouldReturnNotFound() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc
        .perform(delete(CharacteristicsController.API_URL + "/" + nonExistentId))
        .andExpect(status().isNotFound());
  }
}
