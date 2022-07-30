package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.folio.calendar.domain.dto.SingleDayOpeningCollectionDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.utils.CalendarUtils;
import org.junit.jupiter.api.Test;

class CalendarUtilsOpeningMapPaginationTest {

  @Test
  void testPagination() {
    // keep sorted
    Map<LocalDate, SingleDayOpeningDTO> map = new TreeMap<>();
    // fills the entire range
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().normalHour(NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND).build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_03_16
    );

    List<SingleDayOpeningDTO> expectedDateInfo = map
      .entrySet()
      .stream()
      .map(Map.Entry::getValue)
      .toList();

    SingleDayOpeningCollectionDTO collection0Limit10 = CalendarUtils.openingMapToCollection(
      map,
      0,
      10
    );
    assertThat(collection0Limit10.getTotalRecords(), is(expectedDateInfo.size()));
    assertThat(collection0Limit10.getDates(), hasSize(10));
    assertThat(collection0Limit10.getDates(), is(equalTo(expectedDateInfo.subList(0, 10))));

    SingleDayOpeningCollectionDTO collection1Limit5 = CalendarUtils.openingMapToCollection(
      map,
      1,
      5
    );
    assertThat(collection1Limit5.getTotalRecords(), is(expectedDateInfo.size()));
    assertThat(collection1Limit5.getDates(), hasSize(5));
    assertThat(collection1Limit5.getDates(), is(equalTo(expectedDateInfo.subList(1, 6))));

    SingleDayOpeningCollectionDTO collection1000Limit10 = CalendarUtils.openingMapToCollection(
      map,
      1000,
      10
    );
    assertThat(collection1000Limit10.getTotalRecords(), is(expectedDateInfo.size()));
    assertThat(collection1000Limit10.getDates(), hasSize(0));

    SingleDayOpeningCollectionDTO collection0Limit1000 = CalendarUtils.openingMapToCollection(
      map,
      0,
      1000
    );
    assertThat(collection0Limit1000.getTotalRecords(), is(expectedDateInfo.size()));
    assertThat(collection0Limit1000.getDates(), hasSize(expectedDateInfo.size()));
    assertThat(collection0Limit1000.getDates(), is(equalTo(expectedDateInfo)));
  }
}
