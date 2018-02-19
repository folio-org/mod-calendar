package org.folio.rest.impl;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.folio.rest.RestVerticle;
import org.folio.rest.client.TenantClient;
import org.folio.rest.jaxrs.model.Description;
import org.folio.rest.jaxrs.model.OpeningDay;
import org.folio.rest.tools.client.test.HttpClientMock2;
import org.folio.rest.tools.utils.NetworkUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RunWith(VertxUnitRunner.class)
public class CalendarAPITest {

  private static final String TENANT_HEADER_KEY = "X-Okapi-Tenant";
  private static final String TOKEN_HEADER_KEY = "X-Okapi-Token";
  private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
  private static final String TENANT = "test";
  private static final String TOKEN = "test";
  private static final String HOST = "localhost";
  private static final String JSON_CONTENT_TYPE_HEADER_VALUE = "application/json";
  private static final String ACCEPT_HEADER_KEY = "Accept";

  public static int port;
  private static Vertx vertx;

  @BeforeClass
  public static void setup(TestContext context) throws Exception {
    vertx = Vertx.vertx();

    Async async = context.async();
    port = NetworkUtils.nextFreePort();
    TenantClient tenantClient = new TenantClient(HOST, port, TENANT, TOKEN);

    DeploymentOptions options = new DeploymentOptions()
      .setConfig(new JsonObject().put("http.port", port)
        .put(HttpClientMock2.MOCK_MODE, "true"));

    vertx.deployVerticle(RestVerticle.class.getName(), options, res -> {
      try {
        tenantClient.post(null, res2 -> {
          async.complete();
        });
      } catch (Exception e) {
        context.fail(e);
      }
    });

  }

  @AfterClass
  public static void teardown(TestContext context) {
    Async async = context.async();
    TenantClient tenantClient = new TenantClient(HOST, port, TENANT, TOKEN);
    tenantClient.delete(res2 -> {
      vertx.close(context.asyncAssertSuccess(res -> {
        async.complete();
      }));
    });
  }

  @Test
  public void testAddNewDescription(TestContext context) {
    String description = UUID.randomUUID().toString();

    Async async = context.async();
    Future<Void> startFuture;
    Future<Void> f1 = Future.future();
    List<OpeningDay> openingDays = new ArrayList<>();
    OpeningDay monday = new OpeningDay().withDay(OpeningDay.Day.MONDAY).withOpen(true).withAllDay(true);
    openingDays.add(monday);

    postDescription(description, openingDays).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<Void> f = Future.future();
      listDescriptions(description).setHandler(f.completer());
      return f;
    });

    startFuture.setHandler(res -> {
      if (res.succeeded()) {
        async.complete();
      } else {
        res.cause().printStackTrace();
        context.fail(res.cause());
      }
    });
  }

  private Future<Void> postDescription(String description, List<OpeningDay> openingDays) {
    System.out.println("Creating a new description\n");
    Future future = Future.future();
    Description eventDescription = generateDescription(description, openingDays);
    HttpClient client = vertx.createHttpClient();
    client.post(port, HOST, "/calendar/eventdescriptions", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        future.complete();
      } else {
        future.fail("Got status code: " + res.statusCode());
      }
    })
      .putHeader(TENANT_HEADER_KEY, TENANT)
      .putHeader(TOKEN_HEADER_KEY, TOKEN)
      .putHeader(CONTENT_TYPE_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .putHeader(ACCEPT_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .exceptionHandler(e -> {
        future.fail(e);
      })
      .end(JsonObject.mapFrom(eventDescription).encode());
    return future;
  }

  private Future<Void> listDescriptions(String description) {

    System.out.println("Retrieving a description\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/eventdescriptions", res -> {
      if (res.statusCode() == 200) {
        res.bodyHandler(buf -> {
          JsonObject descriptionObject = buf.toJsonObject();
          if (descriptionObject.getInteger("totalRecords") > 0) {
            JsonArray descriptionList = descriptionObject.getJsonArray("descriptions");
            for (Object object : descriptionList) {
              if (object instanceof JsonObject) {
                Description mappedDescription = ((JsonObject) object).mapTo(Description.class);
                if (description.equals(mappedDescription.getDescription())) {
                  future.complete();
                }
              } else {
                future.fail("Can not parse description object.");
              }
            }
            future.fail("Can not find description object.");
          } else {
            future.fail("Unable to read proper data from JSON return value: " + buf.toString());
          }
        });
      } else {
        future.fail("Bad response: " + res.statusCode());
      }
    })
      .putHeader(TENANT_HEADER_KEY, TENANT)
      .putHeader(CONTENT_TYPE_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .putHeader(ACCEPT_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .exceptionHandler(e -> {
        future.fail(e);
      })
      .end();
    return future;
  }

  private Description generateDescription(String description, List<OpeningDay> openingDays) {
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, 0, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, 0, 31, 23, 59, 59);

    return new Description()
      .withId(UUID.randomUUID().toString())
      .withDescription(description)
      .withDescriptionType(Description.DescriptionType.OPENING_DAY)
      .withStartDate(startDate.getTime())
      .withEndDate(endDate.getTime())
      .withOpeningDays(openingDays);
  }

}
