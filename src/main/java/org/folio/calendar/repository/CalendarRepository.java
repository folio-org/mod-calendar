package org.folio.calendar.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.folio.calendar.domain.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A {@link org.springframework.data.jpa.repository.JpaRepository} of {@link org.folio.calendar.domain.entity.Calendar} objects
 */
@Repository
public interface CalendarRepository extends JpaRepository<Calendar, UUID> {
  /**
   * Find calendars for a service point ID
   *
   * @param servicePointId the UUID of the service point
   * @return a {@link java.util.List List} of {@link org.folio.calendar.domain.entity.Calendar}s
   */
  @Query(
    "SELECT c FROM Calendar c INNER JOIN ServicePointCalendarAssignment r ON c.id = r.calendar.id " +
    "WHERE r.servicePointId = :servicePointId"
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
    "SELECT c FROM Calendar c INNER JOIN ServicePointCalendarAssignment r ON c.id = r.calendar.id " +
    "WHERE r.servicePointId = :servicePointId AND c.endDate >= :date"
  )
  List<Calendar> findByServicePointIdOnOrAfterDate(
    @Param("servicePointId") UUID servicePointId,
    @Param("date") LocalDate date
  );

  /**
   * Find calendars for a service point ID and in the given date range
   *
   * @param servicePointId the UUID of the service point
   * @param startDate the date which returned results will end before
   * @param endDate the date which returned results will not start after
   * @return a {@link java.util.List List} of {@link org.folio.calendar.domain.entity.Calendar}s
   */
  @Query(
    "SELECT c FROM Calendar c INNER JOIN ServicePointCalendarAssignment r ON c.id = r.calendar.id " +
    "WHERE (cast(:servicePointId as text) is null OR r.servicePointId = :servicePointId) AND " +
    "(cast(:startDate as date) is null OR c.endDate >= :startDate) AND " +
    "(cast(:endDate as date) is null OR c.startDate <= :endDate)"
  )
  List<Calendar> findWithServicePointAndDateRange(
    @Param("servicePointId") UUID servicePointId,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
  );

  /**
   * Delete a calendar and child elements (hours, exceptions...) by its ID
   * (Explicit query as Postgres cascading is not supported by default)
   *
   * @param calendarId the UUID of the calendar to delete
   */
  @Modifying
  @Transactional
  @Query("DELETE FROM Calendar c WHERE c.id = :calendarId")
  void deleteCascadingById(@Param("calendarId") UUID calendarId);
}
