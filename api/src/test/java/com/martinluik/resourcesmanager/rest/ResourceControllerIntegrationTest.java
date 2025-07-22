package com.martinluik.resourcesmanager.rest;

import static com.martinluik.resourcesmanager.config.KafkaConfig.BULK_EXPORT_TOPIC;
import static com.martinluik.resourcesmanager.config.KafkaConfig.RESOURCES_UPDATES_TOPIC;
import static com.martinluik.resourcesmanager.rest.TestFixtures.CHARACTERISTIC_CODE;
import static com.martinluik.resourcesmanager.rest.TestFixtures.RESOURCE_COUNTRY;
import static com.martinluik.resourcesmanager.rest.TestFixtures.RESOURCE_COUNTRY2;
import static com.martinluik.resourcesmanager.rest.TestFixtures.RESOURCE_TYPE;
import static com.martinluik.resourcesmanager.rest.TestFixtures.RESOURCE_TYPE2;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructCharacteristicDto;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructLocationDto1;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructLocationDto2;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructResourceDto;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructResourceDto2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;

class ResourceControllerIntegrationTest extends BaseIntegrationTest {

  @SpyBean private KafkaTemplate<String, String> kafkaTemplate;

  @Test
  @DisplayName("createResource_withValidInput_sendsKafkaMessage")
  void createResource_withValidInput_sendsKafkaMessage() throws Exception {
    // Given
    var characteristic = constructCharacteristicDto();
    var location = constructLocationDto2();
    var resourceDto = constructResourceDto(location, List.of(characteristic));

    // When
    var response =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resourceDto)),
                201,
                MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Then
    var createdResource = objectMapper.readValue(response, ResourceDto.class);
    assertThat(createdResource)
        .extracting(ResourceDto::getId, ResourceDto::getType, ResourceDto::getCountryCode)
        .satisfies(
            tuple -> {
              assertThat(tuple.get(0)).isNotNull();
              assertThat(tuple.get(1)).isEqualTo(RESOURCE_TYPE);
              assertThat(tuple.get(2)).isEqualTo(RESOURCE_COUNTRY);
            });

    var payloadCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
    verify(kafkaTemplate, times(1))
        .send(eq(RESOURCES_UPDATES_TOPIC), anyString(), payloadCaptor.capture());

    var payload = payloadCaptor.getValue();
    assertThat(payload).contains(RESOURCE_TYPE.name()).contains(RESOURCE_COUNTRY);
  }

  @Test
  @DisplayName("updateResource_withValidInput_sendsKafkaMessage")
  void updateResource_withValidInput_sendsKafkaMessage() throws Exception {
    // Given
    var characteristic = constructCharacteristicDto();
    var location = constructLocationDto2();
    var resourceDto = constructResourceDto(location, List.of(characteristic));

    var createResponse =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resourceDto)),
                201)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource = objectMapper.readValue(createResponse, ResourceDto.class);

    var updatedResourceDto =
        ResourceDto.builder()
            .id(createdResource.getId())
            .type(RESOURCE_TYPE2)
            .countryCode(RESOURCE_COUNTRY2)
            .location(location)
            .characteristics(List.of(characteristic))
            .build();

    // When
    Assertions.assertNotNull(createdResource.getId());
    var updateResponse =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                put(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedResourceDto)),
                200,
                MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Then
    var updatedResource = objectMapper.readValue(updateResponse, ResourceDto.class);
    assertThat(updatedResource)
        .extracting(ResourceDto::getId, ResourceDto::getType, ResourceDto::getCountryCode)
        .satisfies(
            tuple -> {
              assertThat(tuple.get(0)).isNotNull();
              assertThat(tuple.get(1)).isEqualTo(RESOURCE_TYPE2);
              assertThat(tuple.get(2)).isEqualTo(RESOURCE_COUNTRY2);
            });

    var payloadCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
    verify(kafkaTemplate, times(2))
        .send(eq(RESOURCES_UPDATES_TOPIC), anyString(), payloadCaptor.capture());

    var allPayloads = payloadCaptor.getAllValues();
    var updatePayload = allPayloads.getLast();
    assertThat(updatePayload).contains(RESOURCE_TYPE2.name()).contains(RESOURCE_COUNTRY2);
  }

  @Test
  @DisplayName("exportAllResources_withValidInput_sendsBulkExportKafkaMessage")
  void exportAllResources_withValidInput_sendsBulkExportKafkaMessage() throws Exception {
    // Given
    var characteristic = constructCharacteristicDto();
    var location1 = constructLocationDto1();
    var location2 = constructLocationDto2();
    var resource1 = constructResourceDto(location1, List.of(characteristic));
    var resource2 = constructResourceDto2(location2, List.of(characteristic));

    var response1 =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource1)),
                201)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var response2 =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource2)),
                201)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource1 = objectMapper.readValue(response1, ResourceDto.class);
    var createdResource2 = objectMapper.readValue(response2, ResourceDto.class);

    // When
    MockMvcTestUtils.performAndExpect(
        mockMvc, post(ResourcesController.API_URL + "/bulk-export"), 202);

    // Then
    Assertions.assertNotNull(createdResource1.getId());
    var savedResource1 = resourceRepository.findById(createdResource1.getId()).orElse(null);
    Assertions.assertNotNull(createdResource2.getId());
    var savedResource2 = resourceRepository.findById(createdResource2.getId()).orElse(null);
    assertThat(savedResource1).isNotNull();
    assertThat(savedResource2).isNotNull();

    var payloadCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
    verify(kafkaTemplate, times(1))
        .send(eq(BULK_EXPORT_TOPIC), eq(BULK_EXPORT_TOPIC), payloadCaptor.capture());

    var payload = payloadCaptor.getValue();
    assertThat(payload)
        .contains(RESOURCE_TYPE.name())
        .contains(RESOURCE_TYPE2.name())
        .contains(RESOURCE_COUNTRY)
        .contains(RESOURCE_COUNTRY2);

    // Verify the message is a valid JSON array containing the expected resources
    java.util.List<ResourceDto> exportedResources =
        objectMapper.readValue(
            payload,
            objectMapper
                .getTypeFactory()
                .constructCollectionType(java.util.List.class, ResourceDto.class));
    assertThat(exportedResources)
        .hasSize(2)
        .anyMatch(r -> r.getType() == RESOURCE_TYPE && RESOURCE_COUNTRY.equals(r.getCountryCode()))
        .anyMatch(
            r -> r.getType() == RESOURCE_TYPE2 && RESOURCE_COUNTRY2.equals(r.getCountryCode()));
  }

  @Test
  @DisplayName("getAllResources_withExistingResources_returnsAllResources")
  void getAllResources_withExistingResources_returnsAllResources() throws Exception {
    // Given
    var characteristic = constructCharacteristicDto();
    var location1 = constructLocationDto1();
    var location2 = constructLocationDto2();
    var resource1 = constructResourceDto(location1, List.of(characteristic));
    var resource2 = constructResourceDto2(location2, List.of(characteristic));

    var response1 =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource1)),
                201)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var response2 =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource2)),
                201)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource1 = objectMapper.readValue(response1, ResourceDto.class);
    var createdResource2 = objectMapper.readValue(response2, ResourceDto.class);

    // When & Then
    MockMvcTestUtils.performAndExpect(
            mockMvc, get(ResourcesController.API_URL), 200, MediaType.APPLICATION_JSON)
        .andReturn()
        .getResponse()
        .getContentAsString();

    // Then
    java.util.List<ResourceDto> allResources =
        objectMapper.readValue(
            MockMvcTestUtils.performAndExpect(
                    mockMvc, get(ResourcesController.API_URL), 200, MediaType.APPLICATION_JSON)
                .andReturn()
                .getResponse()
                .getContentAsString(),
            objectMapper
                .getTypeFactory()
                .constructCollectionType(java.util.List.class, ResourceDto.class));
    assertThat(allResources).hasSize(2);
    assertThat(allResources.get(0))
        .extracting(ResourceDto::getId, ResourceDto::getType, ResourceDto::getCountryCode)
        .containsExactly(createdResource1.getId(), RESOURCE_TYPE, RESOURCE_COUNTRY);
    assertThat(allResources.get(1))
        .extracting(ResourceDto::getId, ResourceDto::getType, ResourceDto::getCountryCode)
        .containsExactly(createdResource2.getId(), RESOURCE_TYPE2, RESOURCE_COUNTRY2);
  }

  @Test
  @DisplayName("getResourceById_withValidId_returnsResource")
  void getResourceById_withValidId_returnsResource() throws Exception {
    // Given
    var characteristic = constructCharacteristicDto();
    var location = constructLocationDto1();
    var resourceDto = constructResourceDto(location, List.of(characteristic));

    var createResponse =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resourceDto)),
                201)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource = objectMapper.readValue(createResponse, ResourceDto.class);

    // When & Then
    Assertions.assertNotNull(createdResource.getId());
    MockMvcTestUtils.performAndExpect(
            mockMvc,
            get(ResourcesController.API_URL + "/" + createdResource.getId()),
            200,
            MediaType.APPLICATION_JSON)
        .andReturn()
        .getResponse()
        .getContentAsString();

    // Then
    var foundResource =
        objectMapper.readValue(
            MockMvcTestUtils.performAndExpect(
                    mockMvc,
                    get(ResourcesController.API_URL + "/" + createdResource.getId()),
                    200,
                    MediaType.APPLICATION_JSON)
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ResourceDto.class);
    assertThat(foundResource)
        .extracting(ResourceDto::getId, ResourceDto::getType, ResourceDto::getCountryCode)
        .containsExactly(createdResource.getId(), RESOURCE_TYPE, RESOURCE_COUNTRY);
    assertThat(foundResource.getLocation())
        .extracting(LocationDto::getStreetAddress, LocationDto::getCity)
        .containsExactly(
            constructLocationDto1().getStreetAddress(), constructLocationDto1().getCity());
    assertThat(foundResource.getCharacteristics().getFirst().getCode())
        .isEqualTo(CHARACTERISTIC_CODE);
  }

  @Test
  @DisplayName("getResourceById_withNonExistentId_returns404")
  void getResourceById_withNonExistentId_returns404() throws Exception {
    // Given
    var nonExistentId = "550e8400-e29b-41d4-a716-446655440000";

    // When & Then
    MockMvcTestUtils.performAndExpect(
        mockMvc, get(ResourcesController.API_URL + "/" + nonExistentId), 404);
  }

  @Test
  @DisplayName("deleteResourceById_withValidId_deletesResource")
  void deleteResourceById_withValidId_deletesResource() throws Exception {
    // Given
    var characteristic = constructCharacteristicDto();
    var location = constructLocationDto1();
    var resourceDto = constructResourceDto(location, List.of(characteristic));

    var createResponse =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(ResourcesController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resourceDto)),
                201)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdResource = objectMapper.readValue(createResponse, ResourceDto.class);

    Assertions.assertNotNull(createdResource.getId());
    var savedResource = resourceRepository.findById(createdResource.getId()).orElse(null);
    assertThat(savedResource).isNotNull();

    // When
    MockMvcTestUtils.performAndExpect(
        mockMvc, delete(ResourcesController.API_URL + "/" + createdResource.getId()), 204);

    // Then
    var deletedResource = resourceRepository.findById(createdResource.getId()).orElse(null);
    assertThat(deletedResource).isNull();
  }

  @Test
  @DisplayName("deleteResourceById_withNonExistentId_returns404")
  void deleteResourceById_withNonExistentId_returns404() throws Exception {
    // Given
    var nonExistentId = "550e8400-e29b-41d4-a716-446655440000";

    // When & Then
    MockMvcTestUtils.performAndExpect(
        mockMvc, delete(ResourcesController.API_URL + "/" + nonExistentId), 404);
  }
}
