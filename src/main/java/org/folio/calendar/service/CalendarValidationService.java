package org.folio.calendar.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.domain.error.CalendarOverlapErrorData;
import org.folio.calendar.domain.error.ExceptionRangeOverlapErrorData;
import org.folio.calendar.domain.error.ExceptionRangeSingleErrorData;
import org.folio.calendar.domain.error.NormalOpeningOverlapErrorData;
import org.folio.calendar.domain.mapper.ExceptionRangeMapper;
import org.folio.calendar.domain.mapper.NormalOpeningMapper;
import org.folio.calendar.domain.request.Parameters;
import org.folio.calendar.domain.request.TranslationKey;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.exception.DataConflictException;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.exception.NestedCalendarException;
import org.folio.calendar.i18n.TranslationService;
import org.folio.calendar.repository.CalendarRepository;
import org.folio.calendar.utils.DateUtils;
import org.folio.calendar.utils.ExceptionRangeUtils;
import org.folio.calendar.utils.NormalOpeningUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * A Service class for validating calendars
 */
@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CalendarValidationService {

  private final TranslationService translationService;

  private final CalendarRepository calendarRepository;

  private final NormalOpeningMapper normalOpeningMapper;

  private final ExceptionRangeMapper exceptionRangeMapper;

  /**
   * Validate the integrity and sanity of a calendar
   * @param calendar
   * @throws NestedCalendarException
   */
  public void validate(Calendar calendar) {
    List<AbstractCalendarException> validationErrors = new ArrayList<>();
    HttpStatus errorStatusCode = AbstractCalendarException.DEFAULT_STATUS_CODE;

    validateCalendarName(calendar).ifPresent(validationErrors::add);
    validateCalendarDates(calendar).ifPresent(validationErrors::add);

    // check other SPs for conflict
    List<DataConflictException> conflicts = validateServicePointConflicts(
      calendar.getServicePoints(),
      calendar
    );
    if (!conflicts.isEmpty()) {
      validationErrors.addAll(conflicts);
      errorStatusCode = DataConflictException.DEFAULT_STATUS_CODE;
    }

    // check normal hours for self-consistency
    validateNormalOpenings(calendar.getNormalHours()).ifPresent(validationErrors::add);

    // check exception range metadata/integrity (name and dates)
    validateExceptionRangeNames(calendar.getExceptions()).ifPresent(validationErrors::add);
    validateExceptionRangeDateOrder(calendar.getExceptions()).ifPresent(validationErrors::addAll);
    validateExceptionRangeDateBounds(calendar, calendar.getExceptions())
      .ifPresent(validationErrors::addAll);

    // check exception ranges do not conflict
    validateExceptionRangeOverlaps(calendar.getExceptions()).ifPresent(validationErrors::add);

    // check exception hours
    validateExceptionHourBounds(calendar.getExceptions()).forEach(validationErrors::add);
    validateExceptionHourOverlaps(calendar.getExceptions()).forEach(validationErrors::add);

    if (!validationErrors.isEmpty()) {
      throw new NestedCalendarException(errorStatusCode, validationErrors);
    }
  }

  /**
   * Validate that a calendar's name is non-empty
   * @param calendar the calendar to validate
   * @return an InvalidDataException, if one has occurred
   */
  public Optional<InvalidDataException> validateCalendarName(Calendar calendar) {
    if (calendar.getName().isBlank()) {
      return Optional.of(
        new InvalidDataException(
          ErrorCodeDTO.CALENDAR_NO_NAME,
          new ExceptionParameters(Parameters.NAME, calendar.getName()),
          translationService.format(TranslationKey.ERROR_CALENDAR_NAME_EMPTY)
        )
      );
    }
    return Optional.empty();
  }

  /**
   * Validate that a calendar's dates are in the proper order
   * @param calendar the calendar to validate
   * @return an InvalidDataException, if one has occurred
   */
  public Optional<InvalidDataException> validateCalendarDates(Calendar calendar) {
    if (calendar.getStartDate().isAfter(calendar.getEndDate())) {
      return Optional.of(
        new InvalidDataException(
          ErrorCodeDTO.CALENDAR_INVALID_DATE_RANGE,
          new ExceptionParameters(
            Parameters.START_DATE,
            calendar.getStartDate(),
            Parameters.END_DATE,
            calendar.getEndDate()
          ),
          translationService.format(
            TranslationKey.ERROR_DATE_RANGE_INVALID,
            TranslationKey.ERROR_DATE_RANGE_INVALID_P.START_DATE,
            calendar.getStartDate(),
            TranslationKey.ERROR_DATE_RANGE_INVALID_P.END_DATE,
            calendar.getEndDate()
          )
        )
      );
    }
    return Optional.empty();
  }

  /**
   * Check if any assigned service points already have calendars within the new calendar's date
   * range, returning a list of found conflicts.  If none are found, an empty list is returned
   * @param calendar
   * @return any conflicts, represented as exceptions
   */
  public List<DataConflictException> validateServicePointConflicts(
    Collection<ServicePointCalendarAssignment> assignments,
    Calendar calendar
  ) {
    if (assignments.isEmpty()) {
      return new ArrayList<>();
    }
    List<UUID> servicePointAssignmentList = assignments
      .stream()
      .map(ServicePointCalendarAssignment::getServicePointId)
      .collect(Collectors.toList());
    List<Calendar> overlaps = calendarRepository.findWithServicePointsAndDateRange(
      servicePointAssignmentList,
      calendar.getStartDate(),
      calendar.getEndDate()
    );
    if (!overlaps.isEmpty()) {
      return overlaps
        .stream()
        .map(overlap ->
          new DataConflictException(
            ErrorCodeDTO.OVERLAPPING_CALENDAR,
            new ExceptionParameters(
              Parameters.ASSIGNMENTS,
              servicePointAssignmentList,
              Parameters.START_DATE,
              calendar.getStartDate(),
              Parameters.END_DATE,
              calendar.getEndDate()
            ),
            translationService.format(
              TranslationKey.ERROR_CALENDAR_OVERLAP,
              TranslationKey.ERROR_CALENDAR_OVERLAP_P.OVERLAP_NAME,
              overlap.getName(),
              TranslationKey.ERROR_CALENDAR_OVERLAP_P.OVERLAP_START_DATE,
              overlap.getStartDate(),
              TranslationKey.ERROR_CALENDAR_OVERLAP_P.OVERLAP_END_DATE,
              overlap.getEndDate()
            ),
            new CalendarOverlapErrorData(
              calendar
                .getServicePoints()
                .stream()
                .filter(overlap.getServicePoints()::contains)
                .map(ServicePointCalendarAssignment::getServicePointId)
                .collect(Collectors.toList())
            )
          )
        )
        .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * Validate a set of {@link NormalOpening NormalOpenings} does not overlap with itself.
   * If an overlap does occur, an InvalidDataException is returned containing all of the conflicts
   *
   * @param normalHours the set of normal openings to validate
   */
  public Optional<InvalidDataException> validateNormalOpenings(Set<NormalOpening> normalHours) {
    Set<NormalOpening> overlapping = NormalOpeningUtils.getOverlaps(normalHours);

    if (overlapping.isEmpty()) {
      return Optional.empty();
    }

    List<String> openingStrings = overlapping
      .stream()
      .map(opening ->
        translationService.format(
          TranslationKey.NORMAL_OPENING,
          TranslationKey.NORMAL_OPENING_P.START_WEEKDAY_SHORT,
          opening.getStartDay().getShortLocalizedString().apply(translationService),
          TranslationKey.NORMAL_OPENING_P.START_TIME,
          opening.getStartTime(),
          TranslationKey.NORMAL_OPENING_P.END_WEEKDAY_SHORT,
          opening.getEndDay().getShortLocalizedString().apply(translationService),
          TranslationKey.NORMAL_OPENING_P.END_TIME,
          opening.getEndTime()
        )
      )
      .collect(Collectors.toList());

    return Optional.of(
      new InvalidDataException(
        ErrorCodeDTO.CALENDAR_INVALID_NORMAL_OPENINGS,
        new ExceptionParameters(
          Parameters.NORMAL_HOURS,
          normalHours.stream().map(normalOpeningMapper::toDto).collect(Collectors.toList())
        ),
        translationService.format(
          TranslationKey.ERROR_CALENDAR_INVALID_NORMAL_OPENINGS,
          TranslationKey.ERROR_CALENDAR_INVALID_NORMAL_OPENINGS_P.OPENING_LIST,
          translationService.formatList(openingStrings)
        ),
        new NormalOpeningOverlapErrorData(overlapping)
      )
    );
  }

  /**
   * Validate that a set of exceptions have non-empty names.  If there are any
   * which do not, an {@link InvalidDataException InvalidDataException} will be
   * returned to report the error
   * @param ranges the set of ranges to validate
   * @return optionally, an {@code InvalidDataException} if validation failed.
   */
  public Optional<InvalidDataException> validateExceptionRangeNames(
    Collection<ExceptionRange> ranges
  ) {
    if (ranges.stream().anyMatch(range -> range.getName().isBlank())) {
      return Optional.of(
        new InvalidDataException(
          ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_NAME,
          new ExceptionParameters(
            Parameters.EXCEPTIONS,
            ranges.stream().map(exceptionRangeMapper::toDto).collect(Collectors.toList())
          ),
          translationService.format(TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_RANGES)
        )
      );
    }
    return Optional.empty();
  }

  /**
   * Validate that a set of exceptions have sensible dates (start <= end).  If
   * there are any which do not, an
   * {@link InvalidDataException InvalidDataException} will be returned to
   * report each error
   * @param ranges the set of ranges to validate
   * @return optionally, a list of {@code InvalidDataException}s if validation
   * failed.
   */
  public Optional<List<InvalidDataException>> validateExceptionRangeDateOrder(
    Collection<ExceptionRange> ranges
  ) {
    List<ExceptionRange> failed = ranges
      .stream()
      .filter(range -> range.getStartDate().isAfter(range.getEndDate()))
      .collect(Collectors.toList());
    if (!failed.isEmpty()) {
      return Optional.of(
        failed
          .stream()
          .map(range ->
            new InvalidDataException(
              ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_DATE_ORDER,
              new ExceptionParameters(
                Parameters.EXCEPTIONS,
                ranges.stream().map(exceptionRangeMapper::toDto).collect(Collectors.toList())
              ),
              translationService.format(
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER,
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER_P.NAME,
                range.getName(),
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER_P.START_DATE,
                range.getStartDate(),
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER_P.END_DATE,
                range.getEndDate()
              ),
              new ExceptionRangeSingleErrorData(range)
            )
          )
          .collect(Collectors.toList())
      );
    }
    return Optional.empty();
  }

  /**
   * Validate that a set of exceptions are within calendar bounds. For each
   * violation, {@link InvalidDataException InvalidDataException} will be
   * returned for later reporting
   * @param ranges the set of ranges to validate
   * @return optionally, a set of {@code InvalidDataException}s if issues
   * occurred.
   */
  public Optional<List<InvalidDataException>> validateExceptionRangeDateBounds(
    Calendar calendar,
    Collection<ExceptionRange> ranges
  ) {
    List<ExceptionRange> failed = ranges
      .stream()
      .filter(range ->
        !DateUtils.containsRange(
          range.getStartDate(),
          range.getEndDate(),
          calendar.getStartDate(),
          calendar.getEndDate()
        )
      )
      .collect(Collectors.toList());
    if (!failed.isEmpty()) {
      return Optional.of(
        failed
          .stream()
          .map(range ->
            new InvalidDataException(
              ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_DATE_BOUNDARY,
              new ExceptionParameters(
                Parameters.EXCEPTIONS,
                ranges.stream().map(exceptionRangeMapper::toDto).collect(Collectors.toList())
              ),
              translationService.format(
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS,
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS_P.NAME,
                range.getName(),
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS_P.EXCEPTION_START_DATE,
                range.getStartDate(),
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS_P.EXCEPTION_END_DATE,
                range.getEndDate(),
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS_P.CALENDAR_START_DATE,
                calendar.getStartDate(),
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS_P.CALENDAR_END_DATE,
                calendar.getEndDate()
              ),
              new ExceptionRangeSingleErrorData(range)
            )
          )
          .collect(Collectors.toList())
      );
    }
    return Optional.empty();
  }

  /**
   * Validate a set of {@link ExceptionRange ExceptionRanges} does not overlap with itself.
   * If an overlap does occur, an InvalidDataException is returned containing all of the conflicts
   *
   * @param exceptionRanges the set of ranges to validate
   */
  public Optional<InvalidDataException> validateExceptionRangeOverlaps(
    Set<ExceptionRange> exceptionRanges
  ) {
    Optional<Set<ExceptionRange>> overlapping = ExceptionRangeUtils.getOverlaps(exceptionRanges);

    if (overlapping.isEmpty()) {
      return Optional.empty();
    }

    List<String> rangeStrings = overlapping
      .get()
      .stream()
      .map(ExceptionRangeUtils::getTranslation)
      .map(f -> f.apply(translationService))
      .collect(Collectors.toList());

    return Optional.of(
      new InvalidDataException(
        ErrorCodeDTO.CALENDAR_INVALID_EXCEPTIONS,
        new ExceptionParameters(
          Parameters.EXCEPTIONS,
          exceptionRanges.stream().map(exceptionRangeMapper::toDto).collect(Collectors.toList())
        ),
        translationService.format(
          TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_RANGES,
          TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_RANGES_P.EXCEPTION_LIST,
          translationService.formatList(rangeStrings)
        ),
        new ExceptionRangeOverlapErrorData(overlapping.get())
      )
    );
  }

  /**
   * Validate that a set of exceptions' opening hours are within their
   * containing exception's bounds. For each violation, an
   * {@link InvalidDataException InvalidDataException} will be returned for
   * later reporting
   * @param ranges the set of ranges to validate
   * @return optionally, a set of {@code InvalidDataException}s if issues
   * occurred.
   */
  public List<InvalidDataException> validateExceptionHourBounds(Collection<ExceptionRange> ranges) {
    Stream<Optional<InvalidDataException>> stream = ranges
      .stream()
      .map((ExceptionRange range) -> {
        long failures = range
          .getOpenings()
          .stream()
          .filter(opening ->
            !DateUtils.containsRange(
              opening.getStartDate(),
              opening.getEndDate(),
              range.getStartDate(),
              range.getEndDate()
            )
          )
          .count();
        if (failures != 0) {
          return Optional.of(
            new InvalidDataException(
              ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_OPENING_BOUNDARY,
              new ExceptionParameters(
                Parameters.EXCEPTIONS,
                ranges.stream().map(exceptionRangeMapper::toDto).collect(Collectors.toList())
              ),
              translationService.format(
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_HOUR_OUT_OF_BOUNDS,
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_HOUR_OUT_OF_BOUNDS_P.NAME,
                range.getName(),
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_HOUR_OUT_OF_BOUNDS_P.NUM_ERRORS,
                (int) failures
              )
            )
          );
        }
        return Optional.empty();
      });
    return stream.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
  }

  /**
   * Validate that a set of exceptions' opening hours do not overlap with their
   * parent exception. For each violation, an
   * {@link InvalidDataException InvalidDataException} will be returned for
   * later reporting
   * @param ranges the set of ranges to validate
   * @return optionally, a set of {@code InvalidDataException}s if issues
   * occurred.
   */
  public List<InvalidDataException> validateExceptionHourOverlaps(
    Collection<ExceptionRange> ranges
  ) {
    Stream<Optional<InvalidDataException>> stream = ranges
      .stream()
      .map((ExceptionRange range) -> {
        Optional<Set<ExceptionHour>> overlaps = ExceptionRangeUtils.getHourOverlaps(
          range.getOpenings()
        );
        if (overlaps.isPresent()) {
          return Optional.of(
            new InvalidDataException(
              ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_OPENINGS,
              new ExceptionParameters(
                Parameters.EXCEPTIONS,
                ranges.stream().map(exceptionRangeMapper::toDto).collect(Collectors.toList())
              ),
              translationService.format(
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS,
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS_P.NAME,
                range.getName(),
                TranslationKey.ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS_P.OPENING_LIST,
                translationService.formatList(
                  overlaps
                    .get()
                    .stream()
                    .map(hour ->
                      translationService.format(
                        TranslationKey.EXCEPTION_OPENING,
                        TranslationKey.EXCEPTION_OPENING_P.START_DATE,
                        hour.getStartDate(),
                        TranslationKey.EXCEPTION_OPENING_P.START_TIME,
                        hour.getStartTime(),
                        TranslationKey.EXCEPTION_OPENING_P.END_DATE,
                        hour.getEndDate(),
                        TranslationKey.EXCEPTION_OPENING_P.END_TIME,
                        hour.getEndTime()
                      )
                    )
                    .collect(Collectors.toList())
                )
              )
            )
          );
        }
        return Optional.empty();
      });
    return stream.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
  }
}
