package org.folio.calendar.integration.domain.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.domain.mapper.ExceptionHourMapper;
import org.folio.calendar.domain.mapper.ExceptionRangeMapper;
import org.folio.calendar.domain.mapper.NormalOpeningMapper;
import org.folio.calendar.domain.mapper.ServicePointCalendarAssignmentMapper;
import org.folio.calendar.integration.BaseApiTest;
import org.junit.jupiter.api.Test;

class MapperInstanceTest extends BaseApiTest {

  @Test
  void testInstance() {
    assertThat(CalendarMapper.INSTANCE, is(instanceOf(CalendarMapper.class)));
    assertThat(NormalOpeningMapper.INSTANCE, is(instanceOf(NormalOpeningMapper.class)));
    assertThat(ExceptionRangeMapper.INSTANCE, is(instanceOf(ExceptionRangeMapper.class)));
    assertThat(ExceptionHourMapper.INSTANCE, is(instanceOf(ExceptionHourMapper.class)));
    assertThat(
      ServicePointCalendarAssignmentMapper.INSTANCE,
      is(instanceOf(ServicePointCalendarAssignmentMapper.class))
    );
  }
}
