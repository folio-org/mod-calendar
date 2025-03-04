package org.folio.calendar.integration;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * Base abstract class for testing APIs that need a clean database every run
 */
@Log4j2
public abstract class BaseApiAutoDatabaseTest extends BaseApiTest {

  protected boolean hasCreated = false;

  @AfterEach
  void afterEach(TestInfo testInfo, @Autowired DataSource dataSource) {
    cleanDatabase(dataSource);
  }

  protected void cleanDatabase(@Autowired DataSource dataSource) {
    log.info("Truncating database");
    hasCreated = false;

    if (System.getenv().getOrDefault("PROXY_ENABLE", "false").equals("true")) {
      ra(ValidationSchema.NONE).get(getRequestUrl("/_/tests/_/database-truncate"));
    }

    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("database-clean.sql"));
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    }
  }
}
