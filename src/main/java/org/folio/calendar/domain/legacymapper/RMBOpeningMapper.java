package org.folio.calendar.domain.legacymapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.types.LegacyPeriodDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Conversion class between openings from the legacy RMB-based schema to a {@link Period Period}
 */
@Data
@Log4j2
public class RMBOpeningMapper implements RowMapper<Period> {

  public static final String GET_RMB_OPENING_HOURS =
    "SELECT jsonb FROM regular_hours WHERE jsonb->>'openingId' = ?";

  protected final JdbcTemplate jdbcTemplate;
  protected final ObjectMapper mapper;

  public Period mapRow(ResultSet result, int rowNum) throws SQLException {
    try {
      JsonNode json = mapper.readTree(result.getString("jsonb"));
      UUID openingId = UUID.fromString(json.get("id").asText());

      log.info(String.format("Attempting to migrate period %s", openingId));

      log.info(String.format("Getting hours for period %s", openingId));
      List<OpeningDayRelative> openings = jdbcTemplate.queryForObject(
        GET_RMB_OPENING_HOURS,
        new RMBHoursMapper(mapper),
        openingId.toString()
      );

      log.info(String.format("Successfully read period %s", openingId));

      return Period
        .builder()
        .id(openingId)
        .servicePointId(UUID.fromString(json.get("servicePointId").asText()))
        .name(json.get("name").asText())
        .startDate(new LegacyPeriodDate(json.get("startDate").asText()))
        .endDate(new LegacyPeriodDate(json.get("endDate").asText()))
        .openingDays(openings)
        .build();
    } catch (Exception e) {
      log.error(
        String.format(
          "Could not migrate period #%d with JSON %s",
          rowNum,
          result.getString("jsonb")
        )
      );
      log.error(e);
      return null;
    }
  }
}
