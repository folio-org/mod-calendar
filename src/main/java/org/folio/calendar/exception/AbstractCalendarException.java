package org.folio.calendar.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.domain.error.ErrorData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Abstract calendar exception, to be implemented by more concrete exceptions thrown from our application.
 * {@link org.folio.calendar.exception.NonspecificCalendarException NonspecificCalendarException} should be used for otherwise unknown errors (e.g. generic Exception or Spring-related exceptions).
 */
@Getter
@ToString(callSuper = true)
public abstract class AbstractCalendarException extends RuntimeException {

  /** By default, send a 400 */
  public static final HttpStatus DEFAULT_STATUS_CODE = HttpStatus.BAD_REQUEST;

  protected final ErrorCodeDTO errorCode;

  protected final HttpStatus statusCode;

  @NonNull
  protected final ExceptionParameters parameters;

  protected final ErrorData data;

  /**
   * Create an AbstractCalendarException with the given HTTP status code, error
   * code, message, and format.
   *
   * @param cause      The exception which caused this (may be null)
   * @param parameters The parameters causing the exception
   * @param statusCode The Spring HTTP status code ({@link org.springframework.http.HttpStatus HttpStatus})
   * @param errorCode  An error code as described in the ErrorResponse API type
   * @param message    A string for the error message
   * @param data       Nullable additional data to include in the response for richer errors
   */
  protected AbstractCalendarException(
    Throwable cause,
    ExceptionParameters parameters,
    HttpStatus statusCode,
    ErrorCodeDTO errorCode,
    String message,
    ErrorData data
  ) {
    super(message, cause);
    if (parameters == null) {
      parameters = new ExceptionParameters();
    }
    this.parameters = parameters;
    this.errorCode = errorCode;
    this.statusCode = statusCode;
    this.data = data;
  }

  /**
   * Get the standardized error response for this exception
   */
  protected ErrorDTO getErrorDto() {
    // Can only have one exception at a time
    ErrorDTO.ErrorDTOBuilder errorBuilder = ErrorDTO.builder();
    errorBuilder = errorBuilder.code(this.getErrorCode());
    errorBuilder = errorBuilder.message(this.getMessage());
    errorBuilder = errorBuilder.data(this.getData());
    for (StackTraceElement frame : this.getStackTrace()) {
      errorBuilder = errorBuilder.traceItem(frame.toString());
    }
    if (this.getCause() != null) {
      errorBuilder = errorBuilder.traceItem("----------------- CAUSED BY -----------------");
      errorBuilder = errorBuilder.traceItem(this.getCause().getMessage());
      for (StackTraceElement frame : this.getCause().getStackTrace()) {
        errorBuilder = errorBuilder.traceItem(frame.toString());
      }
    }
    Map<String, Object> errorParameters = new HashMap<>();
    for (Entry<String, Object> parameter : this.getParameters().getMap().entrySet()) {
      errorParameters.put(parameter.getKey(), parameter.getValue());
    }
    errorBuilder = errorBuilder.parameters(errorParameters);
    return errorBuilder.build();
  }

  /**
   * Create a standardized error response for the rest API
   *
   * @return An ErrorResponse for API return
   */
  protected ErrorResponseDTO getErrorResponseDto() {
    ErrorResponseDTO.ErrorResponseDTOBuilder responseBuilder = ErrorResponseDTO.builder();
    responseBuilder = responseBuilder.timestamp(Instant.now());
    responseBuilder = responseBuilder.status(this.getStatusCode().value());

    responseBuilder.error(getErrorDto());

    return responseBuilder.build();
  }

  /**
   * Get a ResponseEntity to be returned to the API
   *
   * @return {@link org.springframework.http.ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponse} body.
   */
  public ResponseEntity<ErrorResponseDTO> getErrorResponseDtoEntity() {
    return new ResponseEntity<>(this.getErrorResponseDto(), this.getStatusCode());
  }
}
