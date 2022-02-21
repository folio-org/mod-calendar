package org.folio.calendar.repository;

import java.util.List;
import java.util.UUID;
import org.folio.calendar.domain.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * A {@link JpaRepository} of {@link Calendar} objects
 */
@Repository
public interface CalendarRepository
  extends JpaRepository<Calendar, UUID>, JpaSpecificationExecutor<Calendar> {
  /**
   * Find calendars for a service point ID
   *
   * @param servicePointId the UUID of the service point
   * @return a {@link java.util.List List} of {@link Calendar}s
   */
  @Query(
    "SELECT c FROM Calendar c INNER JOIN ServicePointCalendarAssignment r ON c.id = r.calendarId WHERE r.servicePointId = :servicePointId"
  )
  List<Calendar> findByServicePointId(@Param("servicePointId") UUID servicePointId);
}
