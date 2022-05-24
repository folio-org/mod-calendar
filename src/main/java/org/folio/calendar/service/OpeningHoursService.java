package org.folio.calendar.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.bcel.ExceptionRange;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.domain.error.CalendarOverlapErrorData;
import org.folio.calendar.domain.request.Parameters;
import org.folio.calendar.domain.request.TranslationKey;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.exception.DataConflictException;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.exception.NestedCalendarException;
import org.folio.calendar.i18n.TranslationService;
import org.folio.calendar.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    if (calendar.getName().isBlank()) {
      throw new InvalidDataException(
        ErrorCodeDTO.CALENDAR_NO_NAME,
        new ExceptionParameters(Parameters.NAME, calendar.getName()),
        translationService.format(TranslationKey.ERROR_CALENDAR_NAME_EMPTY)
      );
    }
    if (calendar.getStartDate().isAfter(calendar.getEndDate())) {
      throw new InvalidDataException(
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
      );
    }

    validateServicePointOverlaps(calendar);
    // validateNormalOpeningIntegrity(calendar.getNormalHours());
  }

  /**
   * Check if any assigned service points already have calendars within the new calendar's date
   * range
   * @param calendar
   */
  protected void validateServicePointOverlaps(Calendar calendar) {
    if (calendar.getServicePoints().isEmpty()) {
      return;
    }
    List<UUID> servicePointAssignmentList = calendar
      .getServicePoints()
      .stream()
      .map(ServicePointCalendarAssignment::getServicePointId)
      .collect(Collectors.toList());
    List<Calendar> overlaps = calendarRepository.findWithServicePointsAndDateRange(
      servicePointAssignmentList,
      calendar.getStartDate(),
      calendar.getEndDate()
    );
    if (!overlaps.isEmpty()) {
      List<DataConflictException> exceptions = overlaps
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

      throw new NestedCalendarException(DataConflictException.DEFAULT_STATUS_CODE, exceptions);
    }
  }

  protected void validate(ExceptionRange range) {}
}
