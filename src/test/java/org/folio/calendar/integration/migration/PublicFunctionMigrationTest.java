package org.folio.calendar.integration.migration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;

class PublicFunctionMigrationTest extends AbstractMigrationTest {

  @Test
  void migrationDoesNotDropPublicFUnaccent() throws SQLException {
    loadMigrationSql();
    createPublicFUnaccent();

    runMigration();

    assertThat(
      "public.f_unaccent(text) is preserved",
      publicFUnaccentExists(),
      is(true)
    );
  }

  private void createPublicFUnaccent() throws SQLException {
    try (
      Connection connection = dataSource.getConnection();
      Statement statement = connection.createStatement()
    ) {
      statement.execute(
        """
        CREATE OR REPLACE FUNCTION public.f_unaccent(text)
        RETURNS text AS $$
          SELECT $1
        $$ LANGUAGE sql IMMUTABLE PARALLEL SAFE STRICT;
        """
      );
    }
  }

  private boolean publicFUnaccentExists() throws SQLException {
    try (
      Connection connection = dataSource.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(
        "SELECT to_regprocedure('public.f_unaccent(text)') IS NOT NULL"
      )
    ) {
      resultSet.next();

      return resultSet.getBoolean(1);
    }
  }
}
