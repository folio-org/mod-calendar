package org.folio.calendar.integration.domain.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.folio.calendar.domain.mapper.ExceptionRangeMapper;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExceptionRangeMapperTest extends BaseApiTest {

  @Autowired
  ExceptionRangeMapper mapper;

  @Test
  void testMapper() {
    assertThat(
      mapper.fromDto(mapper.toDto(ExceptionRanges.CLOSED_ALL_YEAR)),
      is(equalTo(ExceptionRanges.CLOSED_ALL_YEAR))
    );
    assertThat(
      mapper.fromDto(mapper.toDto(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4)),
      is(equalTo(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4))
    );
  }

  @Test
  void testFromNull() {
    assertThat(mapper.fromDto(null), is(nullValue()));
  }
}
