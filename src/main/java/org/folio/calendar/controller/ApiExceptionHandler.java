package org.folio.calendar.controller;

import static org.apache.logging.log4j.Level.ERROR;
import static org.apache.logging.log4j.Level.INFO;

import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.folio.calendar.domain.dto.ErrorResponse.ErrorCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Log4j2
@RestControllerAdvice
public class ApiExceptionHandler {

  /**
   * Handles improperly typed parameters
   *
   * @param exception {@link Exception} object
   * @return {@link ResponseEntity} with {@link ErrorResponse} body.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleGenericInvalidParameter(
    MethodArgumentTypeMismatchException exception
  ) {
    log.log(INFO, exception);
    return buildErrorResponse(
      HttpStatus.BAD_REQUEST,
      ErrorCodeEnum.INVALID_PARAMETER,
      "One of the parameters was of the incorrect type (%s)",
      exception.getMessage()
    );
  }

  /**
   * Handles entirely missing parameters
   *
   * @param exception {@link Exception} object
   * @return {@link ResponseEntity} with {@link ErrorResponse} body.
   */
  @ExceptionHandler(MissingRequestValueException.class)
  public ResponseEntity<ErrorResponse> handleGenericMissingParameter(
    MissingRequestValueException exception
  ) {
    log.log(INFO, exception);
    return buildErrorResponse(
      HttpStatus.BAD_REQUEST,
      ErrorCodeEnum.INVALID_PARAMETER,
      "One of the parameters was of the missing or null (%s)",
      exception.getMessage()
    );
  }

  /**
   * Handles all uncaught exceptions.
   *
   * @param exception {@link Exception} object
   * @return {@link ResponseEntity} with {@link ErrorResponse} body.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception exception) {
    log.log(ERROR, exception);
    return buildErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCodeEnum.INTERNAL_SERVER_ERROR,
      "Internal server error (%s): %s",
      exception.getClass().getName(),
      exception.getMessage()
    );
  }

  /**
   * Create a standardized error response from the
   *
   * @param status
   * @param errorCode
   * @throws Exception
   * @return
   */
  protected ResponseEntity<ErrorResponse> buildErrorResponse(
    HttpStatus status,
    ErrorCodeEnum errorCode,
    String errorMessage,
    Object... format
  ) {
    ErrorResponse error = new ErrorResponse();
    error.setTimestamp(new Date());
    error.setErrorCode(errorCode);
    error.setErrorMessage(String.format(errorMessage, format));
    error.setStatus(status.value());
    // error.set
    return new ResponseEntity<>(error, status);
  }
}
