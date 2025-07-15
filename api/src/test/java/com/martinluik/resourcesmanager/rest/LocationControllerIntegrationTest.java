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

import com.martinluik.resourcesmanager.common.dto.LocationDto;
import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import com.martinluik.resourcesmanager.core.domain.Location;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class LocationControllerIntegrationTest extends BaseIntegrationTest {

  private Location testLocationForController;

  @BeforeEach
  void setUpLocation() {
    // Create a separate test location for controller tests
    testLocationForController =
        Location.builder()
            .streetAddress("789 Controller Street")
            .city("Controller City")
            .postalCode("67890")
            .countryCode("LV")
            .build();
    testLocationForController = locationRepository.save(testLocationForController);
  }

  @Test
  @DisplayName("Should retrieve all locations")
  void getAllLocations_ShouldReturnAllLocations() throws Exception {
    // When & Then
    mockMvc
        .perform(get("/api/locations"))
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
    // When & Then
    mockMvc
        .perform(get("/api/locations/" + testLocationForController.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(testLocationForController.getId().toString()))
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
        .perform(get("/api/locations/" + nonExistentId))
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
                post("/api/locations")
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

    // Verify the location was actually created in the database
    var createdLocation = objectMapper.readValue(response, LocationDto.class);
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
    // Given - Missing required fields
    var locationDto =
        LocationDto.builder()
            .streetAddress("") // Empty street address
            .city("") // Empty city
            .postalCode("") // Empty postal code
            .countryCode("INVALID") // Invalid country code (not 2 letters)
            .build();

    // When & Then
    mockMvc
        .perform(
            post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should update location and persist changes to database")
  void updateLocation_ShouldUpdateAndPersistLocation() throws Exception {
    // Given
    var updateDto =
        LocationDto.builder()
            .id(testLocationForController.getId())
            .streetAddress("456 Updated Street")
            .city("Updated City")
            .postalCode("54321")
            .countryCode("LT")
            .build();

    // When & Then
    var response =
        mockMvc
            .perform(
                put("/api/locations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testLocationForController.getId().toString()))
            .andExpect(jsonPath("$.streetAddress").value("456 Updated Street"))
            .andExpect(jsonPath("$.city").value("Updated City"))
            .andExpect(jsonPath("$.postalCode").value("54321"))
            .andExpect(jsonPath("$.countryCode").value("LT"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Verify the location was actually updated in the database
    var updatedLocation = objectMapper.readValue(response, LocationDto.class);
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
            put("/api/locations")
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
    // Given - Create a location to delete
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
        .perform(delete("/api/locations/" + locationToDelete.getId()))
        .andExpect(status().isNoContent());

    // Verify the location was actually deleted from the database
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
        .perform(delete("/api/locations/" + nonExistentId))
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
                put("/api/locations/resource/" + testResource.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newLocationDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testResource.getId().toString()))
            .andExpect(jsonPath("$.location.streetAddress").value("777 Resource Street"))
            .andExpect(jsonPath("$.location.city").value("Resource City"))
            .andExpect(jsonPath("$.location.postalCode").value("77777"))
            .andExpect(jsonPath("$.location.countryCode").value("FI"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Verify the resource location was actually updated in the database
    var updatedResource = objectMapper.readValue(response, ResourceDto.class);
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
            put("/api/locations/resource/" + nonExistentResourceId)
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
