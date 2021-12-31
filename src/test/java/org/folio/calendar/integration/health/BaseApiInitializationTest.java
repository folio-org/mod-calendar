package org.folio.calendar.integration.health;

import static org.assertj.core.api.Assertions.assertThat;

import org.folio.calendar.integration.BaseApiTest;
import org.junit.jupiter.api.Test;

class BaseApiInitializationTest extends BaseApiTest {

  @Test
  void testContextLoads() {
    assertThat(metadata)
      .as("Ensure application context starts and database can be initialized")
      .isNotNull();
  }
}
