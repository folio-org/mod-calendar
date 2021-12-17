package org.folio.calendar.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class BaseApiInitializationTest extends BaseApiTest {

  @Test
  void testContextLoads() {
    assertThat(metadata)
      .as("Ensure application context starts and database can be initialized")
      .isNotNull();
  }
}
