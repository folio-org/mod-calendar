package org.folio.calendar.integration;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Base abstract class for testing APIs that need a clean database every run
 */
@ExtendWith(ApiTestWatcher.class)
public abstract class BaseApiAutoDatabaseTest extends BaseApiTest {

  @Getter
  @Setter
  protected static boolean dbInitialized = false;

  @BeforeEach
  void recreateDatabase(TestInfo testInfo) {
    if (!testInfo.getTags().contains(DatabaseUsage.NONE.value) && !isDbInitialized()) {
      createDatabase();

      setDbInitialized(true);
    }
  }

  @AfterEach
  void cleanDatabase(TestInfo testInfo) {
    if (
      testInfo.getTags().contains(DatabaseUsage.NONE.value) ||
      testInfo.getTags().contains(DatabaseUsage.IDEMPOTENT.value)
    ) {
      return;
    }

    destroyDatabase();

    setDbInitialized(false);
  }
}
