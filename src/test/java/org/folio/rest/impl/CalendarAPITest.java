package org.folio.rest.impl;

import io.restassured.RestAssured;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.folio.rest.RestVerticle;
import org.folio.rest.beans.Openings;
import org.folio.rest.client.TenantClient;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.client.test.HttpClientMock2;
import org.folio.rest.tools.utils.NetworkUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(VertxUnitRunner.class)
public class CalendarAPITest {

  private static final String TENANT = "test";
  private static final String TOKEN = "test";
  private static final String HOST = "localhost";
  private static final Logger log = LoggerFactory.getLogger(CalendarIT.class);

  private static Vertx vertx;

  @BeforeClass
  public static void setup(TestContext context) {
    vertx = Vertx.vertx();
    int port = NetworkUtils.nextFreePort();

    startEmbeddedPostgres(context);

    DeploymentOptions options = new DeploymentOptions()
      .setConfig(new JsonObject().put("http.port", port)
        .put(HttpClientMock2.MOCK_MODE, "true"));

    TenantClient tenantClient = new TenantClient(HOST, port, TENANT, TOKEN);
    Async async = context.async();
    vertx.deployVerticle(RestVerticle.class.getName(), options, res -> {
      try {
        tenantClient.postTenant(null, res2 -> async.complete());
      } catch (Exception e) {
        context.fail(e);
      }
    });

    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  private static void startEmbeddedPostgres(TestContext context) {
    try {
      PostgresClient.setIsEmbedded(true);
      PostgresClient.getInstance(vertx).startEmbeddedPostgres();
    } catch (Exception e) {
      log.error("", e);
      context.fail(e);
    }
  }

  @AfterClass
  public static void teardown(TestContext context) {
    PostgresClient.stopEmbeddedPostgres();
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testMethodHandleExceptions() {
    String expectedResponse = "Internal Server Error";

    PostgresClient instance = PostgresClient.getInstance(vertx);
    instance.startTx(startTx ->
      CalendarAPI.handleExceptions(() -> {
        throw new RuntimeException();
      }, instance, startTx, handler -> assertEquals(expectedResponse, handler.result().getEntity())));
  }

  @Test
  public void testTxHandleExceptions() {
    String expectedResponse = "Internal Server Error";

    PostgresClient instance = PostgresClient.getInstance(vertx);
    instance.startTx(startTx ->
      instance.get(startTx, "test_table", Openings.class, new Criterion(), true, false,
        result -> CalendarAPI.handleExceptions(() -> {
          throw new RuntimeException(result.cause());
        }, instance, startTx, handler -> {
          assertEquals(expectedResponse, handler.result().getEntity());
        }))
    );
  }
}
