package com.martinluik.resourcesmanager.constants;

public final class ValidationMessages {

  // Resource validation messages
  public static final String RESOURCE_TYPE_REQUIRED = "resource_type_required";
  public static final String COUNTRY_CODE_REQUIRED = "country_code_required";
  public static final String COUNTRY_CODE_ISO_PATTERN = "country_code_iso_pattern";
  public static final String LOCATION_REQUIRED = "location_required";
  public static final String CHARACTERISTICS_REQUIRED = "characteristics_required";
  public static final String CHARACTERISTICS_MIN_SIZE = "characteristics_min_size";

  // Location validation messages
  public static final String STREET_ADDRESS_REQUIRED = "street_address_required";
  public static final String STREET_ADDRESS_SIZE_RANGE = "street_address_size_range";
  public static final String STREET_ADDRESS_MAX_SIZE = "street_address_max_size";
  public static final String CITY_REQUIRED = "city_required";
  public static final String CITY_SIZE_RANGE = "city_size_range";
  public static final String CITY_MAX_SIZE = "city_max_size";
  public static final String POSTAL_CODE_REQUIRED = "postal_code_required";
  public static final String POSTAL_CODE_PATTERN = "postal_code_pattern";
  public static final String POSTAL_CODE_MAX_SIZE = "postal_code_max_size";
  public static final String COUNTRY_CODE_EXACT_SIZE = "country_code_exact_size";

  // Characteristic validation messages
  public static final String CHARACTERISTIC_CODE_REQUIRED = "characteristic_code_required";
  public static final String CHARACTERISTIC_CODE_MAX_SIZE = "characteristic_code_max_size";
  public static final String CHARACTERISTIC_TYPE_REQUIRED = "characteristic_type_required";
  public static final String CHARACTERISTIC_VALUE_REQUIRED = "characteristic_value_required";
  public static final String CHARACTERISTIC_VALUE_SIZE_RANGE = "characteristic_value_size_range";

  private ValidationMessages() {
    // Utility class - prevent instantiation
  }
}
