package org.folio.calendar.integration.types;

import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.folio.calendar.domain.types.LegacyPeriodDate;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.integration.BaseApiTest;
import org.junit.jupiter.api.Test;

public class LegacyPeriodDateTest extends BaseApiTest {

  @Test
  void testParseInvalidDate() throws JsonMappingException, JsonProcessingException {
    assertThrows(
      "An invalid small string cannot be parsed as a legacy date",
      InvalidDataException.class,
      () -> new LegacyPeriodDate(this.translationService, "invalid")
    );
  }

  @Test
  void testParseInvalidLongDate() throws JsonMappingException, JsonProcessingException {
    assertThrows(
      "An invalid long string cannot be parsed as a legacy date",
      InvalidDataException.class,
      () -> new LegacyPeriodDate(this.translationService, "invalid long string")
    );
  }
}
