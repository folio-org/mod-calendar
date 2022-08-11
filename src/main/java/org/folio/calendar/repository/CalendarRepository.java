package org.folio.calendar.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.CheckForNull;
import org.folio.calendar.domain.entity.Calendar;
import org.springframework.data.domain.Pageable;
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
   * Find calendars based on a given set of IDs
   *
   * @param ids a {@link java.util.List List} of calendars to search for
   * @return a {@link java.util.List List} of {@link org.folio.calendar.domain.entity.Calendar}s
   */
  @Query("SELECT c FROM Calendar c " + "WHERE c.id IN :calendarIds")
  List<Calendar> findByIds(@Param("calendarIds") Set<UUID> calendarIds);

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
    "WHERE r.servicePointId = :servicePointId AND c.endDate >= :date " +
    "ORDER BY c.startDate"
  )
  List<Calendar> findByServicePointIdOnOrAfterDate(
    @Param("servicePointId") UUID servicePointId,
    @Param("date") LocalDate date
  );

  /**
   * Find calendars for a service point ID and in the given (optional ends) date range
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
    "(cast(:endDate as date) is null OR c.startDate <= :endDate) " +
    "ORDER BY c.startDate"
  )
  List<Calendar> findWithServicePointAndDateRange(
    @Param("servicePointId") UUID servicePointId,
    @Param("startDate") @CheckForNull LocalDate startDate,
    @Param("endDate") @CheckForNull LocalDate endDate
  );

  /**
   * Find calendars for a service point(s) and in the given (optional ends) date range
   *
   * @param servicePointIds a list of service point UUIDs to search
   * @param startDate the date which returned results will end before
   * @param endDate the date which returned results will not start after
   * @return a {@link java.util.List List} of {@link org.folio.calendar.domain.entity.Calendar}s
   */
  @Query(
    "SELECT c FROM Calendar c INNER JOIN ServicePointCalendarAssignment r ON c.id = r.calendar.id " +
    "WHERE (r.servicePointId IN :servicePointIds) AND " +
    "(cast(:startDate as date) is null OR c.endDate >= :startDate) AND " +
    "(cast(:endDate as date) is null OR c.startDate <= :endDate) " +
    "ORDER BY c.startDate"
  )
  List<Calendar> findWithServicePointsAndDateRange(
    @Param("servicePointIds") List<UUID> servicePointIds,
    @Param("startDate") @CheckForNull LocalDate startDate,
    @Param("endDate") @CheckForNull LocalDate endDate
  );

  /**
   * Find calendars for (optional) service point(s) and in the given (optional ends) date range
   *
   * @param checkCalendarIds if calendar IDs should be checked
   * @param calendarIds a list of calendar IDs to limit the search to
   * @param checkServicePoints if service points should be checked
   * @param servicePointIds a list of service point UUIDs to search, no effect if {@code checkServicePoints} is false
   * @param startDate the date which returned results will end before
   * @param endDate the date which returned results will not start after
   * @param page current page, for pagination
   * @return a {@link java.util.List List} of {@link org.folio.calendar.domain.entity.Calendar}s
   */
  @Query(
    "SELECT c FROM Calendar c " +
    "WHERE (:checkCalendarIds = FALSE OR c.id IN :calendarIds) AND " +
    "(:checkServicePoints = FALSE OR " +
    "EXISTS (SELECT 1 FROM ServicePointCalendarAssignment r WHERE r.calendar.id = c.id AND r.servicePointId IN :servicePointIds)" +
    ") AND " +
    "(cast(:startDate as date) is null OR c.endDate >= :startDate) AND " +
    "(cast(:endDate as date) is null OR c.startDate <= :endDate) " +
    "ORDER BY c.startDate"
  )
  List<Calendar> findWithIdsServicePointsDateRangeAndPagination(
    @Param("checkCalendarIds") Boolean checkCalendarIds,
    @Param("calendarIds") List<UUID> calendarIds,
    @Param("checkServicePoints") Boolean checkServicePoints,
    @Param("servicePointIds") List<UUID> servicePointIds,
    @Param("startDate") @CheckForNull LocalDate startDate,
    @Param("endDate") @CheckForNull LocalDate endDate,
    Pageable page
  );

  /**
   * Count calendars for a service point(s) and in the given (optional ends) date range
   *
   * @param checkCalendarIds if calendar IDs should be checked
   * @param calendarIds a list of calendar IDs to limit the search to
   * @param checkServicePoints if service points should be checked
   * @param servicePointIds a list of service point UUIDs to search, no effect if {@code checkServicePoints} is false
   * @param startDate the date which returned results will end before
   * @param endDate the date which returned results will not start after
   * @param page current page, for pagination
   * @return a {@link java.util.List List} of {@link org.folio.calendar.domain.entity.Calendar}s
   */
  @Query(
    "SELECT COUNT(*) FROM Calendar c " +
    "WHERE (:checkCalendarIds = FALSE OR c.id IN :calendarIds) AND " +
    "(:checkServicePoints = FALSE OR " +
    "EXISTS (SELECT 1 FROM ServicePointCalendarAssignment r WHERE r.calendar.id = c.id AND r.servicePointId IN :servicePointIds)" +
    ") AND " +
    "(cast(:startDate as date) is null OR c.endDate >= :startDate) AND " +
    "(cast(:endDate as date) is null OR c.startDate <= :endDate)"
  )
  Integer countWithIdsServicePointsDateRangeAndPagination(
    @Param("checkCalendarIds") Boolean checkCalendarIds,
    @Param("calendarIds") List<UUID> calendarIds,
    @Param("checkServicePoints") Boolean checkServicePoints,
    @Param("servicePointIds") List<UUID> servicePointIds,
    @Param("startDate") @CheckForNull LocalDate startDate,
    @Param("endDate") @CheckForNull LocalDate endDate
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
