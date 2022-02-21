package org.folio.calendar.domain.legacymapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.springframework.jdbc.core.RowMapper;

/**
 * Conversion class for data from the regular_hours table to List&lt;OpeningDayRelative&gt;
 */
@Data
@Log4j2
public class RMBHoursMapper implements RowMapper<List<OpeningDayRelative>> {

  public static final String GET_RMB_OPENING_HOURS =
    "SELECT jsonb FROM regular_hours WHERE jsonb->'openingId' = ?";

  protected final ObjectMapper mapper;

  public List<OpeningDayRelative> mapRow(ResultSet result, int rowNum) throws SQLException {
    try {
      return Arrays.asList(
        mapper.treeToValue(
          mapper.readTree(result.getString("jsonb")).get("openingDays"),
          OpeningDayRelative[].class
        )
      );
    } catch (JsonProcessingException e) {
      log.error("Could not parse regular_hours JSON");
      log.error(e);

      return new ArrayList<>();
    }
  }
}
