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
public class CalendarIT {

  private static final String TENANT_HEADER_KEY = "X-Okapi-Tenant";
  private static final String TOKEN_HEADER_KEY = "x-okapi-token";
  private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
  private static final String TENANT = "test";
  private static final String TOKEN = "test";
  private static final String HOST = "localhost";
  private static final String JSON_CONTENT_TYPE_HEADER_VALUE = "application/json";
  private static final String ACCEPT_HEADER_KEY = "Accept";

  private static int port;
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
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, 0, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, 0, 31, 23, 59, 59);
    List<OpeningDay> openingDays = new ArrayList<>();
    OpeningDay monday = new OpeningDay().withDay(OpeningDay.Day.MONDAY).withOpen(true).withAllDay(true);
    openingDays.add(monday);

    postDescription(startDate, endDate, openingDays).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
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

  @Test
  public void testUpdateDescription(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, 1, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, 1, 28, 23, 59, 59);
    List<OpeningDay> openingDays = new ArrayList<>();
    OpeningDay monday = new OpeningDay().withDay(OpeningDay.Day.MONDAY).withOpen(true).withAllDay(true);
    openingDays.add(monday);

    postDescription(startDate, endDate, openingDays).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      updateDescription(f1.result()).setHandler(f.completer());
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

  private Future<String> postDescription(Calendar startDate, Calendar endDate, List<OpeningDay> openingDays) {
    System.out.println("Creating a new description\n");
    Future<String> future = Future.future();
    Description eventDescription = generateDescription(startDate, endDate, openingDays);
    HttpClient client = vertx.createHttpClient();
    client.post(port, HOST, "/calendar/eventdescriptions", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(handler -> {
          System.out.println("Response body: " + handler.toJsonObject().toString());
          Description descriptionResponse = handler.toJsonObject().mapTo(Description.class);
          future.complete(descriptionResponse.getId());
        });
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

  private Future<String> listDescriptions(String descriptionId) {

    System.out.println("Retrieving a description\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/eventdescriptions", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(buf -> {
          JsonObject descriptionListObject = buf.toJsonObject();
          if (descriptionListObject.getInteger("totalRecords") > 0) {
            JsonArray descriptionList = descriptionListObject.getJsonArray("descriptions");
            for (Object object : descriptionList) {
              if (object instanceof JsonObject) {
                Description mappedDescription = ((JsonObject) object).mapTo(Description.class);
                if (descriptionId.equals(mappedDescription.getId())) {
                  future.complete(descriptionId);
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
      //.putHeader(TOKEN_HEADER_KEY, TOKEN)
      .putHeader(CONTENT_TYPE_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .putHeader(ACCEPT_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .exceptionHandler(e -> {
        future.fail(e);
      })
      .end();
    return future;
  }

  private Future<String> updateDescription(String descriptionId) {

    System.out.println("Updating a description\n");
    Future<String> future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/eventdescriptions", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(buf -> {
          JsonObject descriptionObject = buf.toJsonObject();
          if (descriptionObject.getInteger("totalRecords") > 0) {
            JsonArray descriptionList = descriptionObject.getJsonArray("descriptions");
            if (descriptionList.isEmpty()) {
              future.fail("Can not find description object.");
            }
            for (Object object : descriptionList) {
              if (object instanceof JsonObject) {
                Description mappedDescription = ((JsonObject) object).mapTo(Description.class);
                if (descriptionId.equals(mappedDescription.getId())) {
                  mappedDescription.setDescription("TEST_DESCRIPTION");
                  HttpClient clientForPut = vertx.createHttpClient();
                  clientForPut.put(port, HOST, "/calendar/eventdescriptions/" + descriptionId, updateResponse -> {
                    if (res.statusCode() >= 200 && res.statusCode() < 300) {
                      HttpClient clientForGet = vertx.createHttpClient();
                      clientForGet.get(port, HOST, "/calendar/eventdescriptions", listResponse -> {
                        if (listResponse.statusCode() >= 200 && listResponse.statusCode() < 300) {
                          listResponse.bodyHandler(buffer -> {
                            JsonObject updatedDescriptionObject = buffer.toJsonObject();
                            if (updatedDescriptionObject.getInteger("totalRecords") > 0) {
                              JsonArray updatedDescriptionList = updatedDescriptionObject.getJsonArray("descriptions");
                              for (Object updatedObject : updatedDescriptionList) {
                                if (updatedObject instanceof JsonObject) {
                                  Description mappedUpdatedDescription = ((JsonObject) updatedObject).mapTo(Description.class);
                                  if (descriptionId.equals(mappedUpdatedDescription.getId())) {
                                    if (mappedUpdatedDescription.getDescription().equals(mappedDescription.getDescription())) {
                                      future.complete(descriptionId);
                                    } else {
                                      future.fail("Failed to update event description.");
                                    }
                                  }
                                }
                              }
                            }
                          });
                        } else {
                          future.fail("Could not find event description.");
                        }
                      })
                        .putHeader(TENANT_HEADER_KEY, TENANT)
                        //.putHeader(TOKEN_HEADER_KEY, TOKEN)
                        .putHeader(CONTENT_TYPE_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
                        .putHeader(ACCEPT_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
                        .exceptionHandler(e -> {
                          future.fail(e);
                        })
                        .end();
                    } else {
                      future.fail("Failed to update description");
                    }
                  })
                    .putHeader(TENANT_HEADER_KEY, TENANT)
                    .putHeader(TOKEN_HEADER_KEY, TOKEN)
                    .putHeader(CONTENT_TYPE_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
                    .putHeader(ACCEPT_HEADER_KEY, "text/plain")
                    .exceptionHandler(e -> {
                      future.fail(e);
                    })
                    .end(JsonObject.mapFrom(mappedDescription).encode());
                }
              } else {
                future.fail("Can not parse description object.");
              }
            }
          } else {
            future.fail("Unable to read proper data from JSON return value: " + buf.toString());
          }
        });
      } else {
        future.fail("Bad response: " + res.statusCode());
      }
    })
      .putHeader(TENANT_HEADER_KEY, TENANT)
      //.putHeader(TOKEN_HEADER_KEY, TOKEN)
      .putHeader(CONTENT_TYPE_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .putHeader(ACCEPT_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .exceptionHandler(e -> {
        future.fail(e);
      })
      .end();
    return future;
  }

  /*private Future<Void> listEvents(TestContext context) {
    Future future = Future.future();
    future.complete();
    return future;
  }*/

  private Description generateDescription(Calendar startDate, Calendar endDate, List<OpeningDay> openingDays) {

    return new Description()
      .withId(UUID.randomUUID().toString())
      .withDescription(UUID.randomUUID().toString())
      .withDescriptionType(Description.DescriptionType.OPENING_DAY)
      .withStartDate(startDate.getTime())
      .withEndDate(endDate.getTime())
      .withOpeningDays(openingDays);
  }

}
