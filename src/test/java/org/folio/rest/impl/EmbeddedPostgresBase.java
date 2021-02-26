package org.folio.rest.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.HttpStatus;
import org.folio.rest.client.TenantClient;
import org.folio.rest.jaxrs.model.TenantAttributes;
import org.folio.rest.jaxrs.model.TenantJob;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.PomReader;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public class EmbeddedPostgresBase {
  private static final Logger LOGGER = LogManager.getLogger();

  public static final int GET_TENANT_TIMEOUT_MS = 1000;

  static TenantClient tenantClient;
  private static String jobId;

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        // PostgresClient automatically starts embedded postgres if needed.
        // Stop it after all IT tests have finished.
        PostgresClient.stopEmbeddedPostgres();
      }
    });
  }

  static public void postTenant(TenantClient tenantClient, Runnable runAfterSuccess,
    Runnable runAfterFailure) {

    try {
      TenantAttributes t = new TenantAttributes()
        .withModuleTo(String.format("mod-calendar-%s", PomReader.INSTANCE.getVersion()));

      tenantClient.postTenant(t, postResult -> {
        if (postResult.failed()) {
          Throwable cause = postResult.cause();
          LOGGER.error(cause);
          runAfterFailure.run();
          return;
        }

        final HttpResponse<Buffer> postResponse = postResult.result();
        assertThat(postResponse.statusCode(), is(HttpStatus.HTTP_CREATED.toInt()));

        jobId = postResponse.bodyAsJson(TenantJob.class).getId();

        tenantClient.getTenantByOperationId(jobId, GET_TENANT_TIMEOUT_MS, getResult -> {
          if (getResult.failed()) {
            Throwable cause = getResult.cause();
            LOGGER.error(cause.getMessage());
            runAfterFailure.run();
            return;
          }

          final HttpResponse<Buffer> getResponse = getResult.result();
          assertThat(getResponse.statusCode(), is(HttpStatus.HTTP_OK.toInt()));
          assertThat(getResponse.bodyAsJson(TenantJob.class).getComplete(), is(true));

          runAfterSuccess.run();
        });
      });
    } catch (Exception e) {
      runAfterFailure.run();
    }
  }

  static void deleteTenant(TenantClient tenantClient) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    tenantClient.deleteTenantByOperationId(jobId, deleted -> {
      if (deleted.failed()) {
        future.completeExceptionally(new RuntimeException("Failed to delete tenant"));
        return;
      }
      future.complete(null);
    });
  }
}
