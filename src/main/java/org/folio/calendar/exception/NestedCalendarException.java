package org.folio.calendar.exception;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;

/**
 * A calendar exception containing multiple others.  Used when multiple errors can occur
 * (for example, on creation, multiple overlaps existing)
 */
@Getter
@ToString(callSuper = true)
public class NestedCalendarException extends AbstractCalendarException {

  private final List<? extends AbstractCalendarException> subExceptions;

  /**
   * Create a nested calendar exception with a status code and list of child exceptions
   *
   * @param statusCode The Spring HTTP status code ({@link org.springframework.http.HttpStatus HttpStatus})
   * @param subExceptions a list of all child exceptions
   */
  public NestedCalendarException(
    HttpStatus statusCode,
    List<? extends AbstractCalendarException> subExceptions
  ) {
    super(null, null, statusCode, null, "", null);
    if (subExceptions.isEmpty()) {
      throw new IllegalArgumentException("A nested exception must contain at least one exception");
    }
    this.subExceptions = subExceptions;
  }

  /**
   * Create a standardized error response for the rest API.  This will contain all nested exceptions
   *
   * @return An ErrorResponse for API return
   */
  @Override
  protected ErrorResponseDTO getErrorResponseDto() {
    ErrorResponseDTO.ErrorResponseDTOBuilder responseBuilder = ErrorResponseDTO.builder();
    responseBuilder = responseBuilder.timestamp(Instant.now());
    responseBuilder = responseBuilder.status(this.getStatusCode().value());

    for (AbstractCalendarException subException : subExceptions) {
      responseBuilder.error(subException.getErrorDto());
    }

    return responseBuilder.build();
  }
}
