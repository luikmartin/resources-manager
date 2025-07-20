package com.martinluik.resourcesmanager.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.martinluik.resourcesmanager.dto.LocationDto;
import com.martinluik.resourcesmanager.dto.ResourceDto;
import com.martinluik.resourcesmanager.enums.ResourceType;
import com.martinluik.resourcesmanager.domain.Location;
import com.martinluik.resourcesmanager.domain.Resource;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class LocationControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("Should retrieve all locations")
  void getAllLocations_ShouldReturnAllLocations() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("789 Controller Street")
            .city("Controller City")
            .postalCode("67890")
            .countryCode("LV")
            .build();
    locationRepository.save(location);

    // When & Then
    mockMvc
        .perform(get(LocationController.API_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].streetAddress").exists())
        .andExpect(jsonPath("$[0].city").exists())
        .andExpect(jsonPath("$[0].postalCode").exists())
        .andExpect(jsonPath("$[0].countryCode").exists());
  }

  @Test
  @DisplayName("Should retrieve location by ID")
  void getLocationById_ShouldReturnLocation() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("789 Controller Street")
            .city("Controller City")
            .postalCode("67890")
            .countryCode("LV")
            .build();
    location = locationRepository.save(location);

    // When & Then
    mockMvc
        .perform(get(LocationController.API_URL + "/" + location.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(location.getId().toString()))
        .andExpect(jsonPath("$.streetAddress").value("789 Controller Street"))
        .andExpect(jsonPath("$.city").value("Controller City"))
        .andExpect(jsonPath("$.postalCode").value("67890"))
        .andExpect(jsonPath("$.countryCode").value("LV"));
  }

  @Test
  @DisplayName("Should return 404 when location ID does not exist")
  void getLocationById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc
        .perform(get(LocationController.API_URL + "/" + nonExistentId))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value(containsString(nonExistentId.toString())))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  @DisplayName("Should create new location and persist to database")
  void createLocation_ShouldCreateAndPersistLocation() throws Exception {
    // Given
    var locationDto =
        LocationDto.builder()
            .streetAddress("123 New Street")
            .city("New City")
            .postalCode("12345")
            .countryCode("EE")
            .build();

    // When & Then
    var response =
        mockMvc
            .perform(
                post(LocationController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(locationDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.streetAddress").value("123 New Street"))
            .andExpect(jsonPath("$.city").value("New City"))
            .andExpect(jsonPath("$.postalCode").value("12345"))
            .andExpect(jsonPath("$.countryCode").value("EE"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    var createdLocation = objectMapper.readValue(response, LocationDto.class);
    Assertions.assertNotNull(createdLocation.getId());
    var savedLocation = locationRepository.findById(createdLocation.getId()).orElse(null);
    assertThat(savedLocation).isNotNull();
    assertThat(savedLocation.getStreetAddress()).isEqualTo("123 New Street");
    assertThat(savedLocation.getCity()).isEqualTo("New City");
    assertThat(savedLocation.getPostalCode()).isEqualTo("12345");
    assertThat(savedLocation.getCountryCode()).isEqualTo("EE");
  }

  @Test
  @DisplayName("Should return 400 when creating location with invalid data")
  void createLocation_WithInvalidData_ShouldReturnBadRequest() throws Exception {
    // Given
    var locationDto =
        LocationDto.builder()
            .streetAddress("")
            .city("")
            .postalCode("")
            .countryCode("INVALID")
            .build();

    // When & Then
    mockMvc
        .perform(
            post(LocationController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should update location and persist changes to database")
  void updateLocation_ShouldUpdateAndPersistLocation() throws Exception {
    // Given
    var location =
        Location.builder()
            .streetAddress("789 Controller Street")
            .city("Controller City")
            .postalCode("67890")
            .countryCode("LV")
            .build();
    location = locationRepository.save(location);

    var updateDto =
        LocationDto.builder()
            .id(location.getId())
            .streetAddress("456 Updated Street")
            .city("Updated City")
            .postalCode("54321")
            .countryCode("LT")
            .build();

    // When & Then
    var response =
        mockMvc
            .perform(
                put(LocationController.API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(location.getId().toString()))
            .andExpect(jsonPath("$.streetAddress").value("456 Updated Street"))
            .andExpect(jsonPath("$.city").value("Updated City"))
            .andExpect(jsonPath("$.postalCode").value("54321"))
            .andExpect(jsonPath("$.countryCode").value("LT"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    var updatedLocation = objectMapper.readValue(response, LocationDto.class);
    Assertions.assertNotNull(updatedLocation.getId());
    var savedLocation = locationRepository.findById(updatedLocation.getId()).orElse(null);
    assertThat(savedLocation).isNotNull();
    assertThat(savedLocation.getStreetAddress()).isEqualTo("456 Updated Street");
    assertThat(savedLocation.getCity()).isEqualTo("Updated City");
    assertThat(savedLocation.getPostalCode()).isEqualTo("54321");
    assertThat(savedLocation.getCountryCode()).isEqualTo("LT");
  }

  @Test
  @DisplayName("Should return 404 when updating location with non-existent ID")
  void updateLocation_WithNonExistentId_ShouldReturnNotFound() throws Exception {
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
    mockMvc
        .perform(
            put(LocationController.API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value(containsString(nonExistentId.toString())))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  @DisplayName("Should delete location")
  void deleteLocation_ShouldDeleteLocation() throws Exception {
    // Given
    var locationToDelete =
        Location.builder()
            .streetAddress("999 Delete Street")
            .city("Delete City")
            .postalCode("99999")
            .countryCode("EE")
            .build();
    locationToDelete = locationRepository.save(locationToDelete);

    // When & Then
    mockMvc
        .perform(delete(LocationController.API_URL + "/" + locationToDelete.getId()))
        .andExpect(status().isNoContent());

    var deletedLocation = locationRepository.findById(locationToDelete.getId()).orElse(null);
    assertThat(deletedLocation).isNull();
  }

  @Test
  @DisplayName("Should return 404 when deleting location with non-existent ID")
  void deleteLocation_WithNonExistentId_ShouldReturnNotFound() throws Exception {
    // Given
    var nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc
        .perform(delete(LocationController.API_URL + "/" + nonExistentId))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value(containsString(nonExistentId.toString())))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  @DisplayName("Should update resource location and persist changes")
  void updateResourceLocation_ShouldUpdateResourceLocation() throws Exception {
    // Given
    var resource =
        Resource.builder()
            .type(ResourceType.CONNECTION_POINT)
            .countryCode("EE")
            .location(
                Location.builder()
                    .streetAddress("789 Controller Street")
                    .city("Controller City")
                    .postalCode("67890")
                    .countryCode("LV")
                    .build())
            .build();
    resource = resourceRepository.save(resource);

    var newLocationDto =
        LocationDto.builder()
            .streetAddress("777 Resource Street")
            .city("Resource City")
            .postalCode("77777")
            .countryCode("FI")
            .build();

    // When & Then
    var response =
        mockMvc
            .perform(
                put(LocationController.API_URL + "/resource/" + resource.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newLocationDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(resource.getId().toString()))
            .andExpect(jsonPath("$.location.streetAddress").value("777 Resource Street"))
            .andExpect(jsonPath("$.location.city").value("Resource City"))
            .andExpect(jsonPath("$.location.postalCode").value("77777"))
            .andExpect(jsonPath("$.location.countryCode").value("FI"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    var updatedResource = objectMapper.readValue(response, ResourceDto.class);
    Assertions.assertNotNull(updatedResource.getId());
    var savedResource = resourceRepository.findById(updatedResource.getId()).orElse(null);
    assertThat(savedResource).isNotNull();
    assertThat(savedResource.getLocation().getStreetAddress()).isEqualTo("777 Resource Street");
    assertThat(savedResource.getLocation().getCity()).isEqualTo("Resource City");
    assertThat(savedResource.getLocation().getPostalCode()).isEqualTo("77777");
    assertThat(savedResource.getLocation().getCountryCode()).isEqualTo("FI");
  }

  @Test
  @DisplayName("Should return 404 when updating location for non-existent resource")
  void updateResourceLocation_WithNonExistentResourceId_ShouldReturnNotFound() throws Exception {
    // Given
    var nonExistentResourceId = UUID.randomUUID();
    var newLocationDto =
        LocationDto.builder()
            .streetAddress("777 Resource Street")
            .city("Resource City")
            .postalCode("77777")
            .countryCode("FI")
            .build();

    // When & Then
    mockMvc
        .perform(
            put(LocationController.API_URL + "/resource/" + nonExistentResourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newLocationDto)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value(containsString(nonExistentResourceId.toString())))
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
