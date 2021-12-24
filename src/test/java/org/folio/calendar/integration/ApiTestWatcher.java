package org.folio.calendar.integration;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * Dirties the database state if an integration test fails
 */
public class ApiTestWatcher implements TestWatcher {

  @Override
  public void testAborted(ExtensionContext extensionContext, Throwable throwable) {
    BaseApiAutoDatabaseTest.setDbInitialized(false);
  }

  @Override
  public void testFailed(ExtensionContext extensionContext, Throwable throwable) {
    BaseApiAutoDatabaseTest.setDbInitialized(false);
  }
}
