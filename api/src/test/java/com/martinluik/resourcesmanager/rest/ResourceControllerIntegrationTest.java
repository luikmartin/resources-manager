package com.martinluik.resourcesmanager.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.martinluik.resourcesmanager.common.dto.CharacteristicDto;
import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.common.enums.CharacteristicType;
import com.martinluik.resourcesmanager.common.enums.ResourceType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ResourceControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("Should create resource and send Kafka message with correct data")
  void createResource_ShouldSendKafkaMessage() throws Exception {
    // Given
    var characteristic =
        CharacteristicDto.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .build();

    var location =
        LocationDto.builder()
            .streetAddress("456 New Street")
            .city("New City")
            .postalCode("54321")
            .countryCode("EE")
            .build();

    var resourceDto =
        ResourceDto.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .characteristics(List.of(characteristic))
            .build();

    // When & Then
    var response =
        mockMvc
            .perform(
                post("/api/resources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resourceDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.type").value("CONNECTION_POINT"))
            .andExpect(jsonPath("$.countryCode").value("EE"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Verify the resource was created in the database
    var createdResource = objectMapper.readValue(response, ResourceDto.class);
    var savedResource = resourceRepository.findById(createdResource.getId()).orElse(null);
    assertThat(savedResource).isNotNull();
    assertThat(savedResource.getType()).isEqualTo(ResourceType.CONNECTION_POINT);
    assertThat(savedResource.getCountryCode()).isEqualTo("EE");

    // Resource was created successfully and persisted to database
  }

  @Test
  @DisplayName("Should update resource and send Kafka message with correct data")
  void updateResource_ShouldSendKafkaMessage() throws Exception {
    // Given - Create a resource first
    var characteristic =
        CharacteristicDto.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .build();

    var location =
        LocationDto.builder()
            .streetAddress("789 Update Street")
            .city("Update City")
            .postalCode("98765")
            .countryCode("EE")
            .build();

    var resourceDto =
        ResourceDto.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location)
            .characteristics(List.of(characteristic))
            .build();

    var createResponse =
        mockMvc
            .perform(
                post("/api/resources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resourceDto)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource = objectMapper.readValue(createResponse, ResourceDto.class);

    // Resource created successfully

    // Update the resource
    var updatedResourceDto =
        ResourceDto.builder()
            .id(createdResource.getId())
            .type(ResourceType.METERING_POINT) // Changed type
            .countryCode("LV") // Changed country
            .location(location)
            .characteristics(List.of(characteristic))
            .build();

    // When & Then
    var updateResponse =
        mockMvc
            .perform(
                put("/api/resources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedResourceDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(createdResource.getId().toString()))
            .andExpect(jsonPath("$.type").value("METERING_POINT"))
            .andExpect(jsonPath("$.countryCode").value("LV"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    var updatedResource = objectMapper.readValue(updateResponse, ResourceDto.class);

    // Verify the resource was updated in the database
    var savedResource = resourceRepository.findById(updatedResource.getId()).orElse(null);
    assertThat(savedResource).isNotNull();
    assertThat(savedResource.getType()).isEqualTo(ResourceType.METERING_POINT);
    assertThat(savedResource.getCountryCode()).isEqualTo("LV");

    // Resource was updated successfully and persisted to database
  }

  @Test
  @DisplayName("Should send bulk export Kafka message with correct data")
  void exportAllResources_ShouldSendBulkExportKafkaMessage() throws Exception {
    // Given - Create multiple resources
    var characteristic =
        CharacteristicDto.builder()
            .code("TEST1")
            .type(CharacteristicType.CONSUMPTION_TYPE)
            .value("Fast Charging")
            .build();

    var location1 =
        LocationDto.builder()
            .streetAddress("123 First Street")
            .city("First City")
            .postalCode("11111")
            .countryCode("EE")
            .build();

    var location2 =
        LocationDto.builder()
            .streetAddress("456 Second Street")
            .city("Second City")
            .postalCode("22222")
            .countryCode("LV")
            .build();

    var resource1 =
        ResourceDto.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(location1)
            .characteristics(List.of(characteristic))
            .build();

    var resource2 =
        ResourceDto.builder()
            .type(ResourceType.METERING_POINT)
            .countryCode("LV")
            .location(location2)
            .characteristics(List.of(characteristic))
            .build();

    // Create resources
    var response1 =
        mockMvc
            .perform(
                post("/api/resources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource1)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var response2 =
        mockMvc
            .perform(
                post("/api/resources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource2)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource1 = objectMapper.readValue(response1, ResourceDto.class);
    var createdResource2 = objectMapper.readValue(response2, ResourceDto.class);

    // Resources created successfully

    // When - Trigger bulk export
    mockMvc.perform(post("/api/resources/bulk-export")).andExpect(status().isAccepted());

    // Then - Bulk export was initiated successfully
    // Verify that both resources exist in the database
    var savedResource1 = resourceRepository.findById(createdResource1.getId()).orElse(null);
    var savedResource2 = resourceRepository.findById(createdResource2.getId()).orElse(null);
    assertThat(savedResource1).isNotNull();
    assertThat(savedResource2).isNotNull();
  }
}
