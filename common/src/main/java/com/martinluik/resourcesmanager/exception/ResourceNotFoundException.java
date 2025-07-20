package com.martinluik.resourcesmanager.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class ResourceNotFoundException extends NoSuchElementException {

  public ResourceNotFoundException(UUID id) {
    super("Resource not found with id: " + id);
  }
}
