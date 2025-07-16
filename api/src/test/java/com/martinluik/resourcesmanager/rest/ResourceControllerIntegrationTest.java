package com.martinluik.resourcesmanager.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.martinluik.resourcesmanager.core.config.KafkaConfig;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;

class ResourceControllerIntegrationTest extends BaseIntegrationTest {

  @SpyBean private KafkaTemplate<String, String> kafkaTemplate;

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

    // When
    var response =
        mockMvc
            .perform(
                post(ResourcesController.API_URL)
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

    // Then
    var createdResource = objectMapper.readValue(response, ResourceDto.class);
    Assertions.assertNotNull(createdResource.getId());
    var savedResource = resourceRepository.findById(createdResource.getId()).orElse(null);
    assertThat(savedResource).isNotNull();
    assertThat(savedResource.getType()).isEqualTo(ResourceType.CONNECTION_POINT);
    assertThat(savedResource.getCountryCode()).isEqualTo("EE");

    var payloadCaptor = ArgumentCaptor.forClass(String.class);
    verify(kafkaTemplate, times(1))
        .send(eq("resource-updates"), anyString(), payloadCaptor.capture());

    var payload = payloadCaptor.getValue();
    assertThat(payload).contains("CONNECTION_POINT");
    assertThat(payload).contains("EE");
  }

  @Test
  @DisplayName("Should update resource and send Kafka message with correct data")
  void updateResource_ShouldSendKafkaMessage() throws Exception {
    // Given
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
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resourceDto)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource = objectMapper.readValue(createResponse, ResourceDto.class);

    var updatedResourceDto =
        ResourceDto.builder()
            .id(createdResource.getId())
            .type(ResourceType.METERING_POINT)
            .countryCode("LV")
            .location(location)
            .characteristics(List.of(characteristic))
            .build();

    // When
    Assertions.assertNotNull(createdResource.getId());
    var updateResponse =
        mockMvc
            .perform(
                put(ResourcesController.API_URL)
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

    // Then
    var updatedResource = objectMapper.readValue(updateResponse, ResourceDto.class);
    Assertions.assertNotNull(updatedResource.getId());
    var savedResource = resourceRepository.findById(updatedResource.getId()).orElse(null);

    assertThat(savedResource).isNotNull();
    assertThat(savedResource.getType()).isEqualTo(ResourceType.METERING_POINT);
    assertThat(savedResource.getCountryCode()).isEqualTo("LV");

    var payloadCaptor = ArgumentCaptor.forClass(String.class);
    verify(kafkaTemplate, times(2))
        .send(eq(KafkaConfig.RESOURCES_UPDATES_TOPIC), anyString(), payloadCaptor.capture());

    var allPayloads = payloadCaptor.getAllValues();
    var updatePayload = allPayloads.getLast();
    assertThat(updatePayload).contains("METERING_POINT");
    assertThat(updatePayload).contains("LV");
  }

  @Test
  @DisplayName("Should send bulk export Kafka message with correct data")
  void exportAllResources_ShouldSendBulkExportKafkaMessage() throws Exception {
    // Given
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

    var response1 =
        mockMvc
            .perform(
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource1)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var response2 =
        mockMvc
            .perform(
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource2)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource1 = objectMapper.readValue(response1, ResourceDto.class);
    var createdResource2 = objectMapper.readValue(response2, ResourceDto.class);

    // When
    mockMvc
        .perform(post(ResourcesController.API_URL + "/bulk-export"))
        .andExpect(status().isAccepted());

    // Then
    Assertions.assertNotNull(createdResource1.getId());
    var savedResource1 = resourceRepository.findById(createdResource1.getId()).orElse(null);
    Assertions.assertNotNull(createdResource2.getId());
    var savedResource2 = resourceRepository.findById(createdResource2.getId()).orElse(null);
    assertThat(savedResource1).isNotNull();
    assertThat(savedResource2).isNotNull();

    verify(kafkaTemplate, times(2))
        .send(eq(KafkaConfig.BULK_EXPORT_TOPIC), anyString(), anyString());
  }
}
