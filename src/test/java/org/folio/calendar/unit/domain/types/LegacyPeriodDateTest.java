package org.folio.calendar.unit.domain.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.folio.calendar.domain.types.LegacyPeriodDate;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testutils.MapperUtils;
import org.junit.jupiter.api.Test;

public class LegacyPeriodDateTest {

  @Test
  void testParseNormalDate() throws JsonMappingException, JsonProcessingException {
    assertThat(
      "A legacy date can be properly parsed",
      MapperUtils.MAPPER.readValue("\"2021-01-01T00:00:00.000+00:00\"", LegacyPeriodDate.class),
      is(Dates.LDATE_2021_01_01)
    );
  }

  @Test
  void testParseNonGmtDate() throws JsonMappingException, JsonProcessingException {
    assertThat(
      "A legacy date can be properly parsed regardless of time component",
      MapperUtils.MAPPER.readValue("\"2021-01-01T00:00:00.000+05:00\"", LegacyPeriodDate.class),
      is(Dates.LDATE_2021_01_01)
    );
  }

  @Test
  void testParseNonMidnightDate() throws JsonMappingException, JsonProcessingException {
    assertThat(
      "A legacy date can be properly parsed regardless of time component",
      MapperUtils.MAPPER.readValue("\"2021-01-01T23:59:59.000+00:00\"", LegacyPeriodDate.class),
      is(Dates.LDATE_2021_01_01)
    );
  }

  @Test
  void testParseInvalidDate() throws JsonMappingException, JsonProcessingException {
    assertThrows(
      "An invalid small string cannot be parsed as a legacy date",
      IllegalArgumentException.class,
      () -> MapperUtils.MAPPER.readValue("\"invalid\"", LegacyPeriodDate.class)
    );
  }

  @Test
  void testParseInvalidLongDate() throws JsonMappingException, JsonProcessingException {
    assertThrows(
      "An invalid long string cannot be parsed as a legacy date",
      IllegalArgumentException.class,
      () -> MapperUtils.MAPPER.readValue("\"invalid long string\"", LegacyPeriodDate.class)
    );
  }
}
