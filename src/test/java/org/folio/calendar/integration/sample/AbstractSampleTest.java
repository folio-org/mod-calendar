package org.folio.calendar.integration.sample;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.integration.ValidationSchema;
import org.folio.tenant.domain.dto.Parameter;
import org.folio.tenant.domain.dto.TenantAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * Base abstract class for testing loadReference and loadSample deployment parameters
 */
@Log4j2
public abstract class AbstractSampleTest extends BaseApiTest {

  @Autowired
  DataSource dataSource;

  /**
   * Deletes any existing database schema, replacing it with the sample data
   */
  @Override
  @BeforeEach
  public void createDatabase() {
    super.createDatabase();

    log.info("Truncating database");

    if (System.getenv().getOrDefault("PROXY_ENABLE", "false").equals("true")) {
      ra(ValidationSchema.NONE).get(getRequestUrl("/_/tests/_/database-truncate"));
    }

    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("database-clean.sql"));
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    }

    this.loadSample();
  }

  void loadSample() {
    this.tenantInstall(
        new TenantAttributes()
          .moduleTo("mod-calendar")
          .addParametersItem(new Parameter().key("loadSample").value("true"))
          .addParametersItem(new Parameter().key("loadReference").value("true"))
      );
  }
}
