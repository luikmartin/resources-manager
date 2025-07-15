package com.martinluik.resourcesmanager.common.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class ResourceNotFoundException extends NoSuchElementException {

  public ResourceNotFoundException(UUID id) {
    super("Resource not found with id: " + id);
  }
}
