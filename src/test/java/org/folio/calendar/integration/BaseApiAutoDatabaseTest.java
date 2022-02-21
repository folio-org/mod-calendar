package org.folio.calendar.integration;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
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

  @AfterAll
  void afterAll(TestInfo testInfo, @Autowired DataSource dataSource) {
    if (testInfo.getTags().contains("idempotent")) {
      cleanDatabase(dataSource);
    }
  }

  @AfterEach
  void afterEach(TestInfo testInfo, @Autowired DataSource dataSource) {
    if (!testInfo.getTags().contains("idempotent")) {
      cleanDatabase(dataSource);
    }
  }

  protected void cleanDatabase(@Autowired DataSource dataSource) {
    log.info("Truncating database");

    if (System.getenv().getOrDefault("PROXY_ENABLE", "false").equals("true")) {
      ra(false).get(getRequestUrl("/_/tests/_/database-truncate"));
    }

    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("database-clean.sql"));
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    }
  }
}
