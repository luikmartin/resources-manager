package com.martinluik.resourcesmanager.common.exception;

import java.util.UUID;

public class CharacteristicNotFoundException extends RuntimeException {

  public CharacteristicNotFoundException(UUID id) {
    super("Characteristic not found with id: " + id);
  }
}
