package org.folio.calendar.controller;

import static org.apache.logging.log4j.Level.ERROR;
import static org.apache.logging.log4j.Level.INFO;

import java.util.Arrays;
import javax.servlet.ServletException;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.exception.NonspecificCalendarException;
import org.folio.calendar.i18n.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Overall controller to handle exceptions and return proper API responses
 */
@Log4j2
@RestControllerAdvice
public class ApiExceptionHandler {

  @Autowired
  private TranslationService translationService;

  /**
   * Handles exceptions from our application
   *
   * @param exception an {@link org.folio.calendar.exception.AbstractCalendarException AbstractCalendarException}
   * @see AbstractCalendarException
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponseDTO ErrorResponseDTO} body.
   */
  @ExceptionHandler(AbstractCalendarException.class)
  public ResponseEntity<ErrorResponseDTO> handleCalendarException(
    AbstractCalendarException exception
  ) {
    log.log(INFO, exception);
    return exception.getErrorResponseDTOEntity();
  }

  /**
   * Handles requests to endpoints that do not exist
   *
   * @param exception exception indicating that no handler exists for an endpoint
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponseDTO ErrorResponseDTO} body.
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotFound(NoHandlerFoundException exception) {
    log.log(INFO, exception);
    return new NonspecificCalendarException(
      exception,
      HttpStatus.NOT_FOUND,
      ErrorCodeDTO.INVALID_REQUEST,
      translationService.format(
        "error.endpointNotFound",
        "method",
        exception.getHttpMethod(),
        "url",
        exception.getRequestURL()
      )
    )
      .getErrorResponseDTOEntity();
  }

  /**
   * Handles requests to endpoints with unknown methods
   *
   * @param exception exception indicating that no handler exists for an endpoint with this method
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponseDTO ErrorResponseDTO} body.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponseDTO> handleBadMethod(
    HttpRequestMethodNotSupportedException exception
  ) {
    log.log(INFO, exception);
    return new NonspecificCalendarException(
      exception,
      HttpStatus.METHOD_NOT_ALLOWED,
      ErrorCodeDTO.INVALID_REQUEST,
      translationService.format(
        "error.endpointMethodInvalid",
        "method",
        exception.getMethod(),
        "urlList",
        Arrays.toString(exception.getSupportedMethods())
      )
    )
      .getErrorResponseDTOEntity();
  }

  /**
   * Handles improperly typed parameters/requests
   *
   * @param exception exception indicating that the request could not be parsed
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponseDTO ErrorResponseDTO} body.
   */
  @ExceptionHandler(
    {
      ServletException.class,
      MethodArgumentTypeMismatchException.class,
      MissingRequestValueException.class,
      HttpMessageNotReadableException.class,
    }
  )
  public ResponseEntity<ErrorResponseDTO> handleBadRequest(Exception exception) {
    log.log(INFO, exception);
    return new NonspecificCalendarException(
      exception,
      ErrorCodeDTO.INVALID_PARAMETER,
      translationService.format(
        "error.unparsableData",
        "unLocalizedErrorMessage",
        exception.getMessage()
      )
    )
      .getErrorResponseDTOEntity();
  }

  /**
   * Handles all uncaught exceptions.
   *
   * @param exception exceptions not otherwise caught
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponseDTO ErrorResponseDTO} body.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleAllOtherExceptions(Exception exception) {
    log.log(ERROR, exception);

    // As a note, NullPointerException can be thrown deep in the servlet code if parsing invalid JSON.
    // However, NPE is far too generic to catch and always attribute to bad input.
    return new NonspecificCalendarException(
      exception,
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCodeDTO.INTERNAL_SERVER_ERROR,
      translationService.format(
        "error.internalServerError",
        "className",
        exception.getClass().getSimpleName(),
        "errorMessage",
        exception.getMessage()
      )
    )
      .getErrorResponseDTOEntity();
  }
}
