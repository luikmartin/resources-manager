package com.martinluik.resourcesmanager.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class CharacteristicNotFoundException extends NoSuchElementException {

  public CharacteristicNotFoundException(UUID id) {
    super("Characteristic not found with id: " + id);
  }
}
