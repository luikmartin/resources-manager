package com.martinluik.resourcesmanager.rest;

import static com.martinluik.resourcesmanager.rest.TestFixtures.LOCATION2_CITY;
import static com.martinluik.resourcesmanager.rest.TestFixtures.LOCATION2_COUNTRY;
import static com.martinluik.resourcesmanager.rest.TestFixtures.LOCATION2_POSTAL;
import static com.martinluik.resourcesmanager.rest.TestFixtures.LOCATION2_STREET;
import static com.martinluik.resourcesmanager.rest.TestFixtures.constructLocation2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.martinluik.resourcesmanager.dto.LocationDto;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class LocationControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("getAllLocations_withExistingLocations_returnsAllLocations")
  void getAllLocations_withExistingLocations_returnsAllLocations() throws Exception {
    // Given
    var location = constructLocation2();
    locationRepository.save(location);

    // When
    var response =
        MockMvcTestUtils.performAndExpect(
                mockMvc, get(LocationController.API_URL), 200, MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Then
    java.util.List<LocationDto> locations =
        objectMapper.readValue(
            response,
            objectMapper
                .getTypeFactory()
                .constructCollectionType(java.util.List.class, LocationDto.class));
    assertThat(locations).isNotEmpty();
    assertThat(locations.get(0))
        .extracting(
            LocationDto::getId,
            LocationDto::getStreetAddress,
            LocationDto::getCity,
            LocationDto::getPostalCode,
            LocationDto::getCountryCode)
        .allSatisfy(x -> assertThat(x).isNotNull());
  }

  @Test
  @DisplayName("getLocationById_withValidId_returnsLocation")
  void getLocationById_withValidId_returnsLocation() throws Exception {
    // Given
    var location = constructLocation2();
    location = locationRepository.save(location);

    // When & Then
    var foundLocation =
        objectMapper.readValue(
            MockMvcTestUtils.performAndExpect(
                    mockMvc,
                    get(LocationController.API_URL + "/" + location.getId()),
                    200,
                    MediaType.APPLICATION_JSON)
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LocationDto.class);
    assertThat(foundLocation)
        .extracting(
            LocationDto::getId,
            LocationDto::getStreetAddress,
            LocationDto::getCity,
            LocationDto::getPostalCode,
            LocationDto::getCountryCode)
        .containsExactly(
            location.getId(),
            LOCATION2_STREET,
            LOCATION2_CITY,
            LOCATION2_POSTAL,
            LOCATION2_COUNTRY);
  }

  @Test
  @DisplayName("getLocationById_withNonExistentId_returns404")
  void getLocationById_withNonExistentId_returns404() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    var response =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                get(LocationController.API_URL + "/" + nonExistentId),
                404,
                MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var errorMap = objectMapper.readValue(response, java.util.Map.class);
    assertThat(errorMap).containsEntry("status", 404).containsEntry("error", "Not Found");
    assertThat(errorMap.get("message").toString()).contains(nonExistentId.toString());
    assertThat(errorMap.get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("createLocation_withValidInput_createsLocation")
  void createLocation_withValidInput_createsLocation() throws Exception {
    // Given
    var locationDto =
        LocationDto.builder()
            .streetAddress("123 New Street")
            .city("New City")
            .postalCode("12345")
            .countryCode("EE")
            .build();

    // When
    var response =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                post(LocationController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(locationDto)),
                201,
                MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Then
    var createdLocation = objectMapper.readValue(response, LocationDto.class);
    Assertions.assertNotNull(createdLocation.getId());
    assertThat(createdLocation)
        .extracting(
            LocationDto::getStreetAddress,
            LocationDto::getCity,
            LocationDto::getPostalCode,
            LocationDto::getCountryCode)
        .containsExactly("123 New Street", "New City", "12345", "EE");
  }

  @Test
  @DisplayName("createLocation_withInvalidData_returnsBadRequest")
  void createLocation_withInvalidData_returnsBadRequest() throws Exception {
    // Given
    var locationDto =
        LocationDto.builder()
            .streetAddress("")
            .city("")
            .postalCode("")
            .countryCode("INVALID")
            .build();

    // When & Then
    MockMvcTestUtils.performAndExpect(
        mockMvc,
        post(LocationController.API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(locationDto)),
        400);
  }

  @Test
  @DisplayName("updateLocation_withValidInput_updatesLocation")
  void updateLocation_withValidInput_updatesLocation() throws Exception {
    // Given
    var location = constructLocation2();
    location = locationRepository.save(location);

    var updateDto =
        LocationDto.builder()
            .id(location.getId())
            .streetAddress("456 Updated Street")
            .city("Updated City")
            .postalCode("54321")
            .countryCode("LT")
            .build();

    // When
    var response =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                put(LocationController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)),
                200,
                MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Then
    var updatedLocation = objectMapper.readValue(response, LocationDto.class);
    Assertions.assertNotNull(updatedLocation.getId());
    assertThat(updatedLocation)
        .extracting(
            LocationDto::getStreetAddress,
            LocationDto::getCity,
            LocationDto::getPostalCode,
            LocationDto::getCountryCode)
        .containsExactly("456 Updated Street", "Updated City", "54321", "LT");
  }

  @Test
  @DisplayName("updateLocation_withNonExistentId_returns404")
  void updateLocation_withNonExistentId_returns404() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();
    var updateDto =
        LocationDto.builder()
            .id(nonExistentId)
            .streetAddress("456 Updated Street")
            .city("Updated City")
            .postalCode("54321")
            .countryCode("LT")
            .build();

    // When & Then
    var response =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                put(LocationController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)),
                404,
                MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var errorMap = objectMapper.readValue(response, java.util.Map.class);
    assertThat(errorMap).containsEntry("status", 404).containsEntry("error", "Not Found");
    assertThat(errorMap.get("message").toString()).contains(nonExistentId.toString());
    assertThat(errorMap.get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("deleteLocation_withValidId_deletesLocation")
  void deleteLocation_withValidId_deletesLocation() throws Exception {
    // Given
    var locationToDelete = constructLocation2();
    locationToDelete = locationRepository.save(locationToDelete);

    // When
    MockMvcTestUtils.performAndExpect(
        mockMvc, delete(LocationController.API_URL + "/" + locationToDelete.getId()), 204);

    // Then
    var deletedLocation = locationRepository.findById(locationToDelete.getId()).orElse(null);
    assertThat(deletedLocation).isNull();
  }

  @Test
  @DisplayName("deleteLocation_withNonExistentId_returns404")
  void deleteLocation_withNonExistentId_returns404() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    var response =
        MockMvcTestUtils.performAndExpect(
                mockMvc,
                delete(LocationController.API_URL + "/" + nonExistentId),
                404,
                MediaType.APPLICATION_JSON)
            .andReturn()
            .getResponse()
            .getContentAsString();

    var errorMap = objectMapper.readValue(response, java.util.Map.class);
    assertThat(errorMap).containsEntry("status", 404).containsEntry("error", "Not Found");
    assertThat(errorMap.get("message").toString()).contains(nonExistentId.toString());
    assertThat(errorMap.get("timestamp")).isNotNull();
  }
}
