package org.folio.calendar.testconstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;

@UtilityClass
public class ExceptionRanges {

  public static final ExceptionRange CLOSED_ALL_YEAR = ExceptionRange
    .builder()
    .name(Names.NAME_1)
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_12_31)
    .openings(Arrays.asList())
    .build();

  public static final ExceptionRange CLOSED_JAN_1 = ExceptionRange
    .builder()
    .name(Names.NAME_2)
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_01)
    .openings(Arrays.asList())
    .build();

  public static final ExceptionRange CLOSED_JAN_1_TO_JAN_2 = ExceptionRange
    .builder()
    .name(Names.NAME_3)
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_02)
    .openings(Arrays.asList())
    .build();

  public static final ExceptionRange CLOSED_JAN_1_THRU_JAN_4 = ExceptionRange
    .builder()
    .name(Names.NAME_4)
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_04)
    .openings(Arrays.asList())
    .build();

  public static final ExceptionRange CLOSED_JAN_3_TO_JAN_4 = ExceptionRange
    .builder()
    .name(Names.NAME_5)
    .startDate(Dates.DATE_2021_01_03)
    .endDate(Dates.DATE_2021_01_04)
    .openings(Arrays.asList())
    .build();

  public static final ExceptionRange OPEN_ALL_DAY_JAN_1_THRU_JAN_4 = ExceptionRange
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_04)
    .openings(Arrays.asList(ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4))
    .build();

  public static final ExceptionRange OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4 = ExceptionRange
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_04)
    .openings(
      Arrays.asList(
        ExceptionHours.OPEN_04_00_TO_14_59_JAN_1,
        ExceptionHours.OPEN_04_00_TO_14_59_JAN_2,
        ExceptionHours.OPEN_04_00_TO_14_59_JAN_3,
        ExceptionHours.OPEN_04_00_TO_14_59_JAN_4
      )
    )
    .build();

  public static final ExceptionRange OPEN_00_00_TO_14_59_JAN_1 = ExceptionRange
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_01)
    .openings(Arrays.asList(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1))
    .build();

  /**
   * Get an ExceptionRange with all IDs set to a custom one.  Useful for tests which require equality
   * @param range the range to set IDs on, preferable from this class
   * @param id the ID to use
   * @return the new range with the specified ID
   */
  public static ExceptionRange withExceptionId(ExceptionRange range, UUID id) {
    range = range.withId(id);

    Set<ExceptionHour> hours = new HashSet<>();

    for (ExceptionHour opening : range.getOpenings()) {
      hours.add(opening.withException(range));
    }

    return range.withOpenings(hours);
  }
}
