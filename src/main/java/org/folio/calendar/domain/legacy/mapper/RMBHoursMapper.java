package org.folio.calendar.domain.legacy.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.legacy.dto.OpeningDayRelativeDTO;
import org.springframework.jdbc.core.RowMapper;
import tools.jackson.databind.ObjectMapper;

/**
 * Conversion class for data from the regular_hours table to List&lt;OpeningDayRelative&gt;
 */
@Data
@Log4j2
public class RMBHoursMapper implements RowMapper<List<OpeningDayRelativeDTO>> {

  public static final String GET_RMB_OPENING_HOURS =
    "SELECT jsonb FROM regular_hours WHERE jsonb->'openingId' = ?";

  protected final ObjectMapper mapper;

  public List<OpeningDayRelativeDTO> mapRow(ResultSet result, int rowNum) throws SQLException {
    return Arrays.asList(
      mapper.treeToValue(
        mapper.readTree(result.getString("jsonb")).get("openingDays"),
        OpeningDayRelativeDTO[].class
      )
    );
  }
}
