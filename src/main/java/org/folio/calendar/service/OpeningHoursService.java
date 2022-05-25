package org.folio.calendar.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.domain.error.CalendarOverlapErrorData;
import org.folio.calendar.domain.error.NormalOpeningOverlapErrorData;
import org.folio.calendar.domain.request.Parameters;
import org.folio.calendar.domain.request.TranslationKey;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.exception.DataConflictException;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.exception.NestedCalendarException;
import org.folio.calendar.i18n.TranslationService;
import org.folio.calendar.repository.CalendarRepository;
import org.folio.calendar.utils.NormalOpeningUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * A Service class for calendar-related API calls
 */
@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class OpeningHoursService {

  private final TranslationService translationService;

  private final CalendarRepository calendarRepository;

  /**
   * Get all the calendars for a certain service point
   *
   * @param servicePointId the service point
   * @return a {@link java.util.List List} of {@link java.util.Calendar Calendar}s
   */
  public List<Calendar> getCalendarsForServicePoint(UUID servicePointId) {
    return this.calendarRepository.findByServicePointId(servicePointId);
  }

  /**
   * Insert (or update) a calendar to the database
   *
   * @param calendar the calendar to insert/update/save
   */
  public void saveCalendar(Calendar calendar) {
    this.calendarRepository.save(calendar);
  }

  /**
   * Validate the integrity and sanity of a calendar
   * @param calendar
   * @throws InvalidDataException
   * @throws DataConflictException
   */
  public void validate(Calendar calendar) {
    List<AbstractCalendarException> validationErrors = new ArrayList<>();
    HttpStatus errorStatusCode = AbstractCalendarException.DEFAULT_STATUS_CODE;

    if (calendar.getName().isBlank()) {
      validationErrors.add(
        new InvalidDataException(
          ErrorCodeDTO.CALENDAR_NO_NAME,
          new ExceptionParameters(Parameters.NAME, calendar.getName()),
          translationService.format(TranslationKey.ERROR_CALENDAR_NAME_EMPTY)
        )
      );
    }
    if (calendar.getStartDate().isAfter(calendar.getEndDate())) {
      validationErrors.add(
        new InvalidDataException(
          ErrorCodeDTO.INVALID_DATE_RANGE,
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

    List<DataConflictException> conflicts = validate(calendar.getServicePoints(), calendar);
    if (!conflicts.isEmpty()) {
      validationErrors.addAll(conflicts);
      errorStatusCode = DataConflictException.DEFAULT_STATUS_CODE;
    }
    Optional<InvalidDataException> normalOpeningException = validate(calendar.getNormalHours());
    if (normalOpeningException.isPresent()) {
      validationErrors.add(normalOpeningException.get());
    }

    if (!validationErrors.isEmpty()) {
      throw new NestedCalendarException(errorStatusCode, validationErrors);
    }
  }

  protected Optional<InvalidDataException> validate(Set<NormalOpening> normalHours) {
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
        new ExceptionParameters(Parameters.NORMAL_HOURS, normalHours),
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
   * Check if any assigned service points already have calendars within the new calendar's date
   * range, returning a list of found conflicts.  If none are found, an empty list is returned
   * @param calendar
   * @return any conflicts, represented as exceptions
   */
  protected List<DataConflictException> validate(
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
}
