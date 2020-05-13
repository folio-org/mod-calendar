package org.folio.rest.impl;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.folio.rest.client.TenantClient;
import org.folio.rest.persist.PostgresClient;

public class EmbeddedPostgresBase {
  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        // PostgresClient automatically starts embedded postgres if needed.
        // Stop it after all IT tests have finished.
        PostgresClient.stopEmbeddedPostgres();
      }
    });
  }

  /**
   * Delete the tenant if it exists.
   */
  static public void deleteTenant(TenantClient tenantClient) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    tenantClient.getTenant(get -> {
      if (get.statusCode() != 200) {
        future.completeExceptionally(new RuntimeException(
          get.statusCode() + ": " + get.statusMessage()));
        return;
      }
      get.bodyHandler(body -> {
        String tenantExists = body.getString(0, body.length(), StandardCharsets.UTF_8.name());
        if ("false".equals(tenantExists)) {
          // tenant does not exists, no need to delete
          future.complete(null);
          return;
        }
        tenantClient.deleteTenant(deleted -> {
          if (deleted.statusCode() != 204) {
            future.completeExceptionally(new RuntimeException(
                deleted.statusCode() + ": " + deleted.statusMessage()));
            return;
          }
          future.complete(null);
        });
      });
    });
    future.join();
  }
}
