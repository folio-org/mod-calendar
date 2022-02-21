package org.folio.calendar.unit.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import org.folio.calendar.exception.ExceptionParameters;
import org.junit.jupiter.api.Test;

public class ExceptionParametersTest {

  @Test
  void testDefaultConstructor() {
    assertThat(
      "Default constructor creates an ExceptionParameters object with an empty map",
      new ExceptionParameters().getMap().isEmpty(),
      is(true)
    );
  }

  @Test
  void testInvalidMultipleOfElements() {
    assertThrows(
      "An odd number of elements to vararg constructor should result in an exception",
      IllegalArgumentException.class,
      () -> new ExceptionParameters(1)
    );
    assertThrows(
      "An odd number of elements to vararg constructor should result in an exception",
      IllegalArgumentException.class,
      () -> new ExceptionParameters(1, 2, 3)
    );
  }
}
