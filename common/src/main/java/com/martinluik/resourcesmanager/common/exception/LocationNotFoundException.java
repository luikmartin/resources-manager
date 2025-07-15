package com.martinluik.resourcesmanager.common.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class LocationNotFoundException extends NoSuchElementException {

  public LocationNotFoundException(UUID id) {
    super("Location not found with id: " + id);
  }
}
