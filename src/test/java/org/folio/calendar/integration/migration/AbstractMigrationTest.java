package org.folio.calendar.integration.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.integration.ValidationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * Base abstract class for testing APIs that use migration database setups
 */
@Log4j2
public abstract class AbstractMigrationTest extends BaseApiTest {

  @Autowired
  DataSource dataSource;

  // lacking @BeforeEach as to not attempt to do normal startup
  @Override
  public void createDatabase() {}

  public void runMigration() {
    setInitialized(false);
    super.createDatabase();
  }

  protected void loadMigrationSql(String... scripts) {
    setInitialized(false);

    List<String> filenames = new ArrayList<>();
    filenames.add("database-migrate-setup.sql");
    filenames.addAll(Arrays.asList(scripts));

    for (String filename : filenames) {
      log.info(String.format("Running SQL %s", filename));

      if (System.getenv().getOrDefault("PROXY_ENABLE", "false").equals("true")) {
        ra(ValidationSchema.NONE).get(getRequestUrl(String.format("/_/tests/_/%s", filename)));
      }

      try (Connection conn = dataSource.getConnection()) {
        ScriptUtils.executeSqlScript(conn, new ClassPathResource(filename));
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
