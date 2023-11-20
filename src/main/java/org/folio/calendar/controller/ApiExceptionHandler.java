package org.folio.calendar.controller;

import jakarta.servlet.ServletException;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.domain.request.TranslationKey;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.exception.NonspecificCalendarException;
import org.folio.spring.i18n.service.TranslationService;
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
    log.info(exception);
    return exception.getErrorResponseDtoEntity();
  }

  /**
   * Handles requests to endpoints that do not exist
   *
   * @param exception exception indicating that no handler exists for an endpoint
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponseDTO ErrorResponseDTO} body.
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotFound(NoHandlerFoundException exception) {
    log.info(exception);
    return new NonspecificCalendarException(
      exception,
      HttpStatus.NOT_FOUND,
      ErrorCodeDTO.INVALID_REQUEST,
      translationService.format(
        TranslationKey.ERROR_ENDPOINT_NOT_FOUND,
        TranslationKey.ERROR_ENDPOINT_NOT_FOUND_P.METHOD,
        exception.getHttpMethod(),
        TranslationKey.ERROR_ENDPOINT_NOT_FOUND_P.URL,
        exception.getRequestURL()
      )
    )
      .getErrorResponseDtoEntity();
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
    log.info(exception);
    return new NonspecificCalendarException(
      exception,
      HttpStatus.METHOD_NOT_ALLOWED,
      ErrorCodeDTO.INVALID_REQUEST,
      translationService.format(
        TranslationKey.ERROR_ENDPOINT_METHOD_INVALID,
        TranslationKey.ERROR_ENDPOINT_METHOD_INVALID_P.METHOD,
        exception.getMethod(),
        TranslationKey.ERROR_ENDPOINT_METHOD_INVALID_P.METHOD_LIST,
        Arrays.toString(exception.getSupportedMethods())
      )
    )
      .getErrorResponseDtoEntity();
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
    log.info(exception);
    return new NonspecificCalendarException(
      exception,
      ErrorCodeDTO.INVALID_PARAMETER,
      translationService.format(
        TranslationKey.ERROR_UNPARSABLE_DATA,
        TranslationKey.ERROR_UNPARSABLE_DATA_P.UNLOCALIZED_ERROR_MESSAGE,
        exception.getMessage()
      )
    )
      .getErrorResponseDtoEntity();
  }

  /**
   * Handles all uncaught exceptions.
   *
   * @param exception exceptions not otherwise caught
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponseDTO ErrorResponseDTO} body.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleAllOtherExceptions(Exception exception) {
    log.error(exception);
    log.error(exception.getMessage());

    // As a note, NullPointerException can be thrown deep in the servlet code if parsing invalid JSON.
    // However, NPE is far too generic to catch and always attribute to bad input.
    return new NonspecificCalendarException(
      exception,
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCodeDTO.INTERNAL_SERVER_ERROR,
      translationService.format(
        TranslationKey.ERROR_INTERNAL_SERVER_ERROR,
        TranslationKey.ERROR_INTERNAL_SERVER_ERROR_P.CLASS_NAME,
        exception.getClass().getSimpleName(),
        TranslationKey.ERROR_INTERNAL_SERVER_ERROR_P.ERROR_MESSAGE,
        exception.getMessage()
      )
    )
      .getErrorResponseDtoEntity();
  }
}
