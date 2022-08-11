package org.folio.calendar.integration.domain.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.mapper.ExceptionHourMapper;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.testconstants.ExceptionHours;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExceptionHourMapperTest extends BaseApiTest {

  @Autowired
  ExceptionHourMapper mapper;

  @Test
  void testMapper() {
    assertThat(
      mapper.fromDto(mapper.toDto(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1)),
      is(equalTo(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1))
    );
    assertThat(
      mapper.fromDto(mapper.toDto(ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4)),
      is(equalTo(ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4))
    );
  }
}
