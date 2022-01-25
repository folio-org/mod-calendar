package org.folio.calendar.service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Priority;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.domain.legacymapper.RMBOpeningMapper;
import org.folio.calendar.domain.sample.SampleCalendars;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.utils.PeriodUtils;
import org.folio.spring.FolioExecutionContext;
import org.folio.spring.liquibase.FolioSpringLiquibase;
import org.folio.spring.service.TenantService;
import org.folio.tenant.domain.dto.TenantAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@Priority(-10)
public class CustomTenantService extends TenantService {

  public static final String IS_RMB_SQL =
    "SELECT EXISTS(SELECT 1 FROM pg_tables WHERE schemaname=? AND tablename=?)";
  public static final String RMB_INTERNAL = "rmb_internal";
  public static final String GET_RMB_OPENINGS = "SELECT jsonb FROM openings";

  protected final CalendarService calendarService;

  protected List<Period> periodsToMigrate;

  /**
   * Constructor for a new CustomTenantService.  Directly wraps the original {@link TenantService TenantService} constructor.
   * @param jdbcTemplate the JDBC template to use for database queries
   * @param context information about the environment FOLIO is running in
   * @param folioSpringLiquibase a custom instance of {@link liquibase.integration.spring.SpringLiquibase SpringLiquibase}
   * @param calendarService an autowired CalendarService for adding calendars when migrating
   */
  @Autowired
  public CustomTenantService(
    JdbcTemplate jdbcTemplate,
    FolioExecutionContext context,
    FolioSpringLiquibase folioSpringLiquibase,
    CalendarService calendarService
  ) {
    super(jdbcTemplate, context, folioSpringLiquibase);
    this.calendarService = calendarService;
    this.periodsToMigrate = new ArrayList<>();
  }

  /**
   * Parse all calendars from RMB-style database, if applicable
   */
  @Override
  protected void beforeLiquibaseUpdate(TenantAttributes attributes) {
    boolean shouldMigrate = jdbcTemplate.query(
      IS_RMB_SQL,
      (ResultSet resultSet) -> {
        resultSet.next();
        return resultSet.getBoolean(1);
      },
      this.getSchemaName(),
      RMB_INTERNAL
    );

    if (shouldMigrate) {
      log.info("Existing RMB installation detected.  Attempting migration.");

      ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .setSerializationInclusion(Include.NON_NULL);

      this.periodsToMigrate =
        jdbcTemplate.query(GET_RMB_OPENINGS, new RMBOpeningMapper(jdbcTemplate, mapper));
      this.periodsToMigrate.removeAll(Collections.singletonList(null));

      log.info(String.format("Found %d periods to migrate", this.periodsToMigrate.size()));
      log.debug(this.periodsToMigrate);
    }
  }

  /**
   * Add all periods from beforeLiquibaseUpdate and add them into the newly created schema
   */
  @Override
  protected void afterLiquibaseUpdate(TenantAttributes attributes) {
    for (Period period : this.periodsToMigrate) {
      try {
        log.info(String.format("Attempting to save calendar with ID %s", period.getId()));
        this.calendarService.createCalendarFromPeriod(period, period.getServicePointId());
      } catch (AbstractCalendarException e) {
        log.error(String.format("Could not save calendar with ID %s", period.getId()));
        log.error(e);
      }
    }
  }

  /**
   * Load any applicable reference data.  This is for things which would likely
   * be relevant in a production install; no pre-populated data makes sense for
   * this module.  Therefore, nothing is done.
   */
  @Override
  public void loadReferenceData() {
    log.info("No reference data is applicable for mod-calendar");
  }

  /**
   * Load sample data into the application.
   */
  @Override
  public void loadSampleData() {
    log.info(
      "Assuming that mod-inventory-storage's default reference service points have been loaded..."
    );

    SampleCalendars
      .getSampleCalendars()
      .forEach((Calendar calendar) -> {
        try {
          this.calendarService.checkPeriod(
              PeriodUtils.toPeriod(calendar.withExceptions(new HashSet<>())),
              calendar
                .getServicePoints()
                .stream()
                .map(ServicePointCalendarAssignment::getServicePointId)
                .findFirst()
                .get()
            );
          this.calendarService.insertCalendar(calendar);
          log.info(String.format("Loaded calendar %s", calendar.getId()));
        } catch (AbstractCalendarException e) {
          log.error("Could not load sample calendar - skipping and continuing");
          log.error(e);
        }
      });

    log.info("Added sample calendars");
  }
}
