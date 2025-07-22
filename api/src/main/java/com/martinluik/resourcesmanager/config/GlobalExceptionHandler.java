package com.martinluik.resourcesmanager.config;

import com.martinluik.resourcesmanager.exception.CharacteristicNotFoundException;
import com.martinluik.resourcesmanager.exception.LocationNotFoundException;
import com.martinluik.resourcesmanager.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    log.warn("Resource not found: {}", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(LocationNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleLocationNotFoundException(
      LocationNotFoundException ex) {
    log.warn("Location not found: {}", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CharacteristicNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCharacteristicNotFoundException(
      CharacteristicNotFoundException ex) {
    log.warn("Characteristic not found: {}", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    var errors = new HashMap<String, String>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    var message = "Validation failed: " + errors;
    log.warn("Validation error: {}", message);
    return createErrorResponse(message, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex) {
    log.warn("Handler method validation error: {}", ex.getMessage());
    return createErrorResponse("Validation failure", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("Illegal argument: {}", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred", ex);
    return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ErrorResponse> createErrorResponse(String message, HttpStatus status) {
    return ResponseEntity.status(status)
        .body(
            ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build());
  }

  @Getter
  @Builder
  public static class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
  }
}
