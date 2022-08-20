package org.folio.calendar.integration.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.folio.calendar.domain.legacy.dto.LegacyPeriodDate;
import org.folio.calendar.domain.legacy.dto.LegacyPeriodDateConverter;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.integration.BaseApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LegacyPeriodDateTest extends BaseApiTest {

  @Autowired
  LegacyPeriodDateConverter converter;

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

  @Test
  void testConverter() {
    assertThat(converter.convert("2022-01-01").toString(), is("2022-01-01"));
    assertThat(converter.convert("2022-01-01").serialize(), is("2022-01-01T00:00:00.000+00:00"));
  }
}
