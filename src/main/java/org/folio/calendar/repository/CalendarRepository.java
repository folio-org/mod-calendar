package org.folio.calendar.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.folio.calendar.domain.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * A {@link org.springframework.data.jpa.repository.JpaRepository} of {@link org.folio.calendar.domain.entity.Calendar} objects
 */
@Repository
public interface CalendarRepository
  extends JpaRepository<Calendar, UUID>, JpaSpecificationExecutor<Calendar> {
  /**
   * Find calendars for a service point ID
   *
   * @param servicePointId the UUID of the service point
   * @return a {@link java.util.List List} of {@link org.folio.calendar.domain.entity.Calendar}s
   */
  @Query(
    "SELECT c FROM Calendar c INNER JOIN ServicePointCalendarAssignment r ON c.id = r.calendarId WHERE r.servicePointId = :servicePointId"
  )
  List<Calendar> findByServicePointId(@Param("servicePointId") UUID servicePointId);

  /**
   * Find calendars for a service point ID on or after (greater than or equal to) a certain date
   *
   * @param servicePointId the UUID of the service point
   * @param date the date which returned results will be equal to or after
   * @return a {@link java.util.List List} of {@link org.folio.calendar.domain.entity.Calendar}s
   */
  @Query(
    "SELECT c FROM Calendar c INNER JOIN ServicePointCalendarAssignment r ON c.id = r.calendarId WHERE r.servicePointId = :servicePointId AND c.endDate >= :date"
  )
  List<Calendar> findByServicePointIdOnOrAfterDate(
    @Param("servicePointId") UUID servicePointId,
    @Param("date") LocalDate date
  );
}
