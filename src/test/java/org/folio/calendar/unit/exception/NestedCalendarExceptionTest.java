package org.folio.calendar.unit.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.exception.DataNotFoundException;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.exception.NestedCalendarException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class NestedCalendarExceptionTest {

  @Test
  void testInvalidConstruction() {
    List<AbstractCalendarException> list = new ArrayList<>();
    assertThrows(
      "A nested calendar exception must have at least one sub-exception",
      IllegalArgumentException.class,
      () -> new NestedCalendarException(HttpStatus.BAD_REQUEST, list)
    );
  }

  @Test
  void testErrorConversion() {
    assertThat(
      new NestedCalendarException(
        HttpStatus.BAD_REQUEST,
        Arrays.asList(
          new InvalidDataException(new ExceptionParameters(), "test"),
          new DataNotFoundException(new ExceptionParameters(), "test")
        )
      )
        .getErrorResponseDtoEntity()
        .getBody()
        .getErrors(),
      hasSize(2)
    );
  }
}
