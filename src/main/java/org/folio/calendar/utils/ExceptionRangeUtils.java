package org.folio.calendar.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.request.TranslationKey;
import org.folio.calendar.i18n.TranslationService;

/**
 * Utilities for exception ranges
 */
@UtilityClass
public class ExceptionRangeUtils {

  /**
   * Find overlaps within a set of exceptional opening hours, if any exist
   * @param openings a set of openings to consider
   * @return an optional list of openings that overlap (empty/no value if there were no overlaps).
   * Not all overlaps may be returned, however, if there are overlap(s), then this function will
   * return at least two overlapping openings.
   */
  public static Optional<Set<ExceptionHour>> getHourOverlaps(Collection<ExceptionHour> openings) {
    return TemporalUtils.getOverlaps(
      openings
        .stream()
        .map(hour ->
          new TemporalRange<LocalDateTime, ExceptionHour>(
            hour,
            LocalDateTime.of(hour.getStartDate(), hour.getStartTime()),
            LocalDateTime.of(hour.getEndDate(), hour.getEndTime())
          )
        )
        .collect(Collectors.toList())
    );
  }

  /**
   * Find overlaps between a set of exception ranges, if any exist
   * @param ranges a set of ranges to evaluate
   * @return an optional list of ranges that overlap (empty/no value if there were no overlaps).
   * Not all overlaps may be returned, however, if there are overlap(s), then this function will
   * return at least two overlapping openings.
   */
  public static Optional<Set<ExceptionRange>> getOverlaps(Collection<ExceptionRange> ranges) {
    return TemporalUtils.getOverlaps(
      ranges
        .stream()
        .map(range ->
          new TemporalRange<LocalDate, ExceptionRange>(
            range,
            range.getStartDate(),
            range.getEndDate()
          )
        )
        .collect(Collectors.toList())
    );
  }

  /**
   * Get a translation-generating function for this exception range.  This will
   * look something like ""Foo" on Jan 12, 2022" or
   * ""Foo" from Jan 12, 2022 to Jan 13, 2022", depending on if the range spans
   * multiple days.
   * @param range the range to generate a translation function for
   * @return A function that, when a {@link TranslationService TranslationService}
   * is applied, will return the proper translated representation of this range.
   */
  public static Function<TranslationService, String> getTranslation(ExceptionRange range) {
    if (range.getStartDate().equals(range.getEndDate())) {
      return translationService ->
        translationService.format(
          TranslationKey.EXCEPTION_RANGE_SINGLE_DAY,
          TranslationKey.EXCEPTION_RANGE_SINGLE_DAY_P.NAME,
          range.getName(),
          TranslationKey.EXCEPTION_RANGE_SINGLE_DAY_P.DATE,
          range.getStartDate()
        );
    } else {
      return translationService ->
        translationService.format(
          TranslationKey.EXCEPTION_RANGE_MULTIPLE_DAYS,
          TranslationKey.EXCEPTION_RANGE_MULTIPLE_DAYS_P.NAME,
          range.getName(),
          TranslationKey.EXCEPTION_RANGE_MULTIPLE_DAYS_P.START_DATE,
          range.getStartDate(),
          TranslationKey.EXCEPTION_RANGE_MULTIPLE_DAYS_P.END_DATE,
          range.getEndDate()
        );
    }
  }
}
