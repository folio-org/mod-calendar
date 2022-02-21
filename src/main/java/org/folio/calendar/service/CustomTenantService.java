package org.folio.calendar.service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Priority;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.legacymapper.RMBOpeningMapper;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.spring.FolioExecutionContext;
import org.folio.spring.liquibase.FolioSpringLiquibase;
import org.folio.spring.service.TenantService;
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

  // must be redeclared to change visibility
  /** the JDBC template to use for database queries */
  protected final JdbcTemplate jdbcTemplate;
  /** information about the environment FOLIO is running in */
  protected final FolioExecutionContext context;
  /** a custom instance of {@link liquibase.integration.spring.SpringLiquibase SpringLiquibase} */
  protected final FolioSpringLiquibase folioSpringLiquibase;

  protected final CalendarService calendarService;

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
    // initialized here in addition to super as super's fields are private
    this.jdbcTemplate = jdbcTemplate;
    this.context = context;
    this.folioSpringLiquibase = folioSpringLiquibase;
    this.calendarService = calendarService;
  }

  /**
   * Get the name of the schema to create.  Re-defined from {@link TenantService#getSchemaName} due to visibility
   * @return a name for the module's schema
   */
  protected String getDBSchemaName() {
    return context.getFolioModuleMetadata().getDBSchemaName(context.getTenantId());
  }

  /**
   * Perform all applicable transformations for a new tenant environment
   */
  @Override
  public void createTenant() {
    boolean shouldMigrate = jdbcTemplate.query(
      IS_RMB_SQL,
      (ResultSet resultSet) -> {
        resultSet.next();
        return resultSet.getBoolean(1);
      },
      this.getDBSchemaName(),
      RMB_INTERNAL
    );

    List<Period> periodsToMigrate = new ArrayList<>();

    if (shouldMigrate) {
      log.info("Existing RMB installation detected.  Attempting migration.");

      ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .setSerializationInclusion(Include.NON_NULL);

      periodsToMigrate =
        jdbcTemplate.query(GET_RMB_OPENINGS, new RMBOpeningMapper(jdbcTemplate, mapper));
      periodsToMigrate.removeAll(Collections.singletonList(null));

      log.info(String.format("Found %d periods to migrate", periodsToMigrate.size()));
      log.debug(periodsToMigrate);
    }

    super.createTenant();

    for (Period period : periodsToMigrate) {
      try {
        log.info(String.format("Attempting to save calendar with ID %s", period.getId()));
        this.calendarService.createCalendarFromPeriod(period, period.getServicePointId());
      } catch (AbstractCalendarException e) {
        log.error(String.format("Could not save calendar with ID %s", period.getId()));
        log.error(e);
      }
    }
  }
}
