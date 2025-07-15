package com.martinluik.resourcesmanager.common.constants;


public final class ValidationMessages {

  // Resource validation messages
  public static final String RESOURCE_TYPE_REQUIRED = "Resource type is required";
  public static final String COUNTRY_CODE_REQUIRED = "Country code is required";
  public static final String COUNTRY_CODE_ISO_PATTERN = "Country code must be a 2-letter ISO code";
  public static final String LOCATION_REQUIRED = "Location is required";
  public static final String CHARACTERISTICS_REQUIRED = "Characteristics list is required";
  public static final String CHARACTERISTICS_MIN_SIZE = "At least one characteristic is required";
  // Location validation messages
  public static final String STREET_ADDRESS_REQUIRED = "Street address is required";
  public static final String STREET_ADDRESS_SIZE_RANGE =
      "Street address must be between 1 and 255 characters";
  public static final String STREET_ADDRESS_MAX_SIZE =
      "Street address must be less than 255 characters";
  public static final String CITY_REQUIRED = "City is required";
  public static final String CITY_SIZE_RANGE = "City must be between 1 and 100 characters";
  public static final String CITY_MAX_SIZE = "City must be less than 100 characters";
  public static final String POSTAL_CODE_REQUIRED = "Postal code is required";
  public static final String POSTAL_CODE_PATTERN =
      "Postal code must be between 3 and 10 characters";
  public static final String POSTAL_CODE_MAX_SIZE = "Postal code must be less than 20 characters";
  public static final String COUNTRY_CODE_EXACT_SIZE = "Country code must be exactly 2 characters";
  // Characteristic validation messages
  public static final String CHARACTERISTIC_CODE_REQUIRED = "Characteristic code is required";
  public static final String CHARACTERISTIC_CODE_MAX_SIZE =
      "Characteristic code must not exceed 5 characters";
  public static final String CHARACTERISTIC_TYPE_REQUIRED = "Characteristic type is required";
  public static final String CHARACTERISTIC_VALUE_REQUIRED = "Characteristic value is required";
  public static final String CHARACTERISTIC_VALUE_SIZE_RANGE =
      "Characteristic value must be between 1 and 1000 characters";
  // Service layer validation messages
  public static final String RESOURCE_DATA_NULL = "Resource data cannot be null";
  public static final String RESOURCE_NAME_REQUIRED = "Resource name is required";
  public static final String RESOURCE_NOT_FOUND = "Resource not found with id: ";
  public static final String CHARACTERISTIC_DATA_NULL = "Characteristic data cannot be null";
  public static final String CHARACTERISTIC_NAME_REQUIRED = "Characteristic name is required";
  public static final String CHARACTERISTIC_NOT_FOUND = "Characteristic not found with id: ";
  public static final String LOCATION_DATA_NULL = "Location data cannot be null";
  // Error messages
  public static final String FAILED_TO_RETRIEVE_RESOURCE = "Failed to retrieve resource: ";
  public static final String FAILED_TO_CREATE_RESOURCE = "Failed to create resource: ";
  public static final String FAILED_TO_UPDATE_RESOURCE = "Failed to update resource: ";
  public static final String FAILED_TO_DELETE_RESOURCE = "Failed to delete resource: ";
  public static final String FAILED_TO_UPDATE_RESOURCE_LOCATION =
      "Failed to update resource location: ";
  public static final String FAILED_TO_ADD_CHARACTERISTIC = "Failed to add characteristic: ";
  public static final String FAILED_TO_UPDATE_CHARACTERISTIC = "Failed to update characteristic: ";
  public static final String FAILED_TO_DELETE_CHARACTERISTIC = "Failed to delete characteristic: ";
  public static final String FAILED_TO_EXPORT_RESOURCES = "Failed to export resources: ";

  private ValidationMessages() {
    // Utility class - prevent instantiation
  }
}
