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
import com.martinluik.resourcesmanager.core.domain.Characteristic;
import com.martinluik.resourcesmanager.core.domain.Location;
import com.martinluik.resourcesmanager.core.domain.Resource;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CharacteristicsControllerIntegrationTest extends BaseIntegrationTest {

  private Characteristic testCharacteristic;

  @BeforeEach
  void setUpCharacteristic() {
    // Create test characteristic
    testCharacteristic =
        Characteristic.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .resource(testResource)
            .build();
    testCharacteristic = characteristicRepository.save(testCharacteristic);
  }

  @Test
  @DisplayName(
      "Should successfully update characteristic with valid data and persist changes to database")
  void updateCharacteristic_WithValidData_ShouldReturnUpdatedCharacteristic() throws Exception {
    // Given - Store original values for comparison
    var originalCode = testCharacteristic.getCode();
    var originalType = testCharacteristic.getType();
    var originalValue = testCharacteristic.getValue();

    var updateDto =
        CharacteristicDto.builder()
            .id(testCharacteristic.getId())
            .resourceId(testResource.getId())
            .code("TEST2")
            .type(CharacteristicType.CHARGING_POINT)
            .value("Updated Value")
            .build();

    // When & Then
    var response =
        mockMvc
            .perform(
                put("/api/characteristics")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testCharacteristic.getId().toString()))
            .andExpect(jsonPath("$.resourceId").value(testResource.getId().toString()))
            .andExpect(jsonPath("$.code").value("TEST2"))
            .andExpect(jsonPath("$.type").value("CHARGING_POINT"))
            .andExpect(jsonPath("$.value").value("Updated Value"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Verify the characteristic was actually updated in the database
    var updatedCharacteristic =
        characteristicRepository.findById(testCharacteristic.getId()).orElse(null);
    assertThat(updatedCharacteristic).isNotNull();
    assertThat(updatedCharacteristic.getCode()).isEqualTo("TEST2");
    assertThat(updatedCharacteristic.getType()).isEqualTo(CharacteristicType.CHARGING_POINT);
    assertThat(updatedCharacteristic.getValue()).isEqualTo("Updated Value");

    // Verify that the original values were actually changed
    assertThat(updatedCharacteristic.getCode()).isNotEqualTo(originalCode);
    assertThat(updatedCharacteristic.getType()).isNotEqualTo(originalType);
    assertThat(updatedCharacteristic.getValue()).isNotEqualTo(originalValue);

    // Verify the resource relationship is maintained
    assertThat(updatedCharacteristic.getResource().getId()).isEqualTo(testResource.getId());
  }

  @Test
  @DisplayName("Should return 404 when trying to update characteristic with non-existent ID")
  void updateCharacteristic_WithNonExistentId_ShouldReturnNotFound() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();
    var updateDto =
        CharacteristicDto.builder()
            .id(nonExistentId)
            .resourceId(testResource.getId())
            .code("TEST2")
            .type(CharacteristicType.CHARGING_POINT)
            .value("Updated Value")
            .build();

    // When & Then
    mockMvc
        .perform(
            put("/api/characteristics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Should return 400 when trying to update characteristic with missing required fields")
  void updateCharacteristic_WithInvalidData_ShouldReturnBadRequest() throws Exception {
    // Given - Missing required fields
    var updateDto =
        CharacteristicDto.builder()
            .id(testCharacteristic.getId())
            .resourceId(testResource.getId())
            // Missing code, type, and value
            .build();

    // When & Then
    mockMvc
        .perform(
            put("/api/characteristics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName(
      "Should return 400 when trying to update characteristic with code exceeding maximum length")
  void updateCharacteristic_WithInvalidCodeLength_ShouldReturnBadRequest() throws Exception {
    // Given - Code too long (max 5 characters)
    var updateDto =
        CharacteristicDto.builder()
            .id(testCharacteristic.getId())
            .resourceId(testResource.getId())
            .code("TOOLONG") // 7 characters, max is 5
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Valid Value")
            .build();

    // When & Then
    mockMvc
        .perform(
            put("/api/characteristics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName(
      "Should return 400 when trying to update characteristic with value exceeding maximum length")
  void updateCharacteristic_WithInvalidValueLength_ShouldReturnBadRequest() throws Exception {
    // Given - Value too long (max 1000 characters)
    var longValue = "A".repeat(1001);
    var updateDto =
        CharacteristicDto.builder()
            .id(testCharacteristic.getId())
            .resourceId(testResource.getId())
            .code("TEST2")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value(longValue)
            .build();

    // When & Then
    mockMvc
        .perform(
            put("/api/characteristics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when trying to update characteristic with empty value")
  void updateCharacteristic_WithEmptyValue_ShouldReturnBadRequest() throws Exception {
    // Given - Empty value (min 1 character)
    var updateDto =
        CharacteristicDto.builder()
            .id(testCharacteristic.getId())
            .resourceId(testResource.getId())
            .code("TEST2")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("") // Empty value
            .build();

    // When & Then
    mockMvc
        .perform(
            put("/api/characteristics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when trying to update characteristic with null ID")
  void updateCharacteristic_WithNullId_ShouldReturnBadRequest() throws Exception {
    // Given - Null ID
    var updateDto =
        CharacteristicDto.builder()
            .id(null) // Null ID
            .resourceId(testResource.getId())
            .code("TEST2")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Valid Value")
            .build();

    // When & Then
    mockMvc
        .perform(
            put("/api/characteristics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  // GET /api/characteristics - Get all characteristics
  @Test
  @DisplayName("Should return all characteristics")
  void getAllCharacteristics_ShouldReturnAllCharacteristics() throws Exception {
    // Given - We already have testCharacteristic created in setUpCharacteristic()

    // Create another characteristic for testing
    var secondCharacteristic =
        Characteristic.builder()
            .code("TEST2")
            .type(CharacteristicType.CHARGING_POINT)
            .value("Slow Charging")
            .resource(testResource)
            .build();
    characteristicRepository.save(secondCharacteristic);

    // When & Then
    mockMvc
        .perform(get("/api/characteristics"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].code").exists())
        .andExpect(jsonPath("$[0].type").exists())
        .andExpect(jsonPath("$[0].value").exists());
  }

  // GET /api/characteristics/{id} - Get characteristic by ID
  @Test
  @DisplayName("Should return characteristic when valid ID is provided")
  void getCharacteristicById_WithValidId_ShouldReturnCharacteristic() throws Exception {
    // When & Then
    mockMvc
        .perform(get("/api/characteristics/" + testCharacteristic.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(testCharacteristic.getId().toString()))
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
    mockMvc.perform(get("/api/characteristics/" + nonExistentId)).andExpect(status().isNotFound());
  }

  // GET /api/characteristics/resource/{resourceId} - Get characteristics by resource ID
  @Test
  @DisplayName("Should return characteristics for valid resource ID")
  void getCharacteristicsByResourceId_WithValidResourceId_ShouldReturnCharacteristics()
      throws Exception {
    // Given - We already have testCharacteristic for testResource

    // Create another characteristic for the same resource
    var secondCharacteristic =
        Characteristic.builder()
            .code("TEST2")
            .type(CharacteristicType.CHARGING_POINT)
            .value("Slow Charging")
            .resource(testResource)
            .build();
    characteristicRepository.save(secondCharacteristic);

    // When & Then
    mockMvc
        .perform(get("/api/characteristics/resource/" + testResource.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(2)))
        .andExpect(jsonPath("$[0].resourceId").value(testResource.getId().toString()))
        .andExpect(jsonPath("$[1].resourceId").value(testResource.getId().toString()));
  }

  @Test
  @DisplayName("Should return empty array when resource ID has no characteristics")
  void getCharacteristicsByResourceId_WithResourceIdHavingNoCharacteristics_ShouldReturnEmptyArray()
      throws Exception {
    // Given - Create a new location and resource with no characteristics
    var newLocation =
        Location.builder()
            .streetAddress("456 Another Street")
            .city("Another City")
            .postalCode("54321")
            .countryCode("EE")
            .build();
    newLocation = locationRepository.save(newLocation);

    var newResource =
        Resource.builder()
            .type(testResource.getType())
            .countryCode(testResource.getCountryCode())
            .location(newLocation)
            .build();
    newResource = resourceRepository.save(newResource);

    // When & Then
    mockMvc
        .perform(get("/api/characteristics/resource/" + newResource.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(0)));
  }

  // POST /api/characteristics/resource/{resourceId} - Create new characteristic
  @Test
  @DisplayName("Should successfully create characteristic with valid data")
  void createCharacteristic_WithValidData_ShouldReturnCreatedCharacteristic() throws Exception {
    // Given
    var createDto =
        CharacteristicDto.builder()
            .code("NEW1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("New Characteristic")
            .build();

    // When & Then
    mockMvc
        .perform(
            post("/api/characteristics/resource/" + testResource.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.resourceId").value(testResource.getId().toString()))
        .andExpect(jsonPath("$.code").value("NEW1"))
        .andExpect(jsonPath("$.type").value("CONSUMPTION_TYPE"))
        .andExpect(jsonPath("$.value").value("New Characteristic"));

    // Verify the characteristic was actually created in the database
    var allCharacteristics = characteristicRepository.findAll();
    var resourceCharacteristics =
        allCharacteristics.stream()
            .filter(c -> c.getResource().getId().equals(testResource.getId()))
            .toList();
    assertThat(resourceCharacteristics).hasSize(2); // Original + new one
    assertThat(resourceCharacteristics.stream())
        .anyMatch(c -> c.getCode().equals("NEW1") && c.getValue().equals("New Characteristic"));
  }

  @Test
  @DisplayName("Should return 400 when creating characteristic with invalid data")
  void createCharacteristic_WithInvalidData_ShouldReturnBadRequest() throws Exception {
    // Given - Missing required fields
    var createDto =
        CharacteristicDto.builder()
            .code("NEW1")
            // Missing type and value
            .build();

    // When & Then
    mockMvc
        .perform(
            post("/api/characteristics/resource/" + testResource.getId())
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
            post("/api/characteristics/resource/" + nonExistentResourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  // DELETE /api/characteristics/{id} - Delete characteristic
  @Test
  @DisplayName("Should successfully delete characteristic with valid ID")
  void deleteCharacteristic_WithValidId_ShouldReturnNoContent() throws Exception {
    // When & Then
    mockMvc
        .perform(delete("/api/characteristics/" + testCharacteristic.getId()))
        .andExpect(status().isNoContent());

    // Verify the characteristic was actually deleted from the database
    var deletedCharacteristic = characteristicRepository.findById(testCharacteristic.getId());
    assertThat(deletedCharacteristic).isEmpty();
  }

  @Test
  @DisplayName("Should return 404 when trying to delete characteristic with non-existent ID")
  void deleteCharacteristic_WithNonExistentId_ShouldReturnNotFound() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc
        .perform(delete("/api/characteristics/" + nonExistentId))
        .andExpect(status().isNotFound());
  }
}
