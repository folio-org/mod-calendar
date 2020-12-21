package org.folio.rest.impl;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.folio.rest.client.TenantClient;
import org.folio.rest.persist.PostgresClient;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public class EmbeddedPostgresBase {
  static {
    // PostgresClient automatically starts embedded postgres if needed.
    // Stop it after all IT tests have finished.
    Runtime.getRuntime().addShutdownHook(new Thread(PostgresClient::stopEmbeddedPostgres));
  }

  /**
   * Delete the tenant if it exists.
   */
  static public void deleteTenant(TenantClient tenantClient, String operationId) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    tenantClient.getTenantByOperationId(operationId, 0, ar -> {
      HttpResponse<Buffer> get = ar.result();
      if (get.statusCode() != 200) {
        future.completeExceptionally(new RuntimeException(
          get.statusCode() + ": " + get.statusMessage()));
        return;
      }

        String tenantExists = get.bodyAsString(StandardCharsets.UTF_8.name());
        if ("false".equals(tenantExists)) {
          // tenant does not exists, no need to delete
          future.complete(null);
          return;
        }
        tenantClient.deleteTenantByOperationId(operationId, delAr -> {
          HttpResponse<Buffer> deleted = delAr.result();
          if (deleted.statusCode() != 204) {
            future.completeExceptionally(new RuntimeException(
                deleted.statusCode() + ": " + deleted.statusMessage()));
            return;
          }
          future.complete(null);
        });

    });
    future.join();
  }
}
