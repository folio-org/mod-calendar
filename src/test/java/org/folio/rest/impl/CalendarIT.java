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
import org.folio.rest.jaxrs.model.*;
import org.folio.rest.tools.client.test.HttpClientMock2;
import org.folio.rest.tools.utils.NetworkUtils;
import org.folio.rest.utils.CalendarUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.Calendar;

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
    startDate.set(2017, Calendar.JANUARY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.JANUARY, 31, 23, 59, 59);
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
  public void testAddNewDescriptionWithCheckingInputs(TestContext context) {
    int startHour1 = 8;
    int startMinute1 = 00;
    int endHour1 = 9;
    int endMinute1 = 00;
    int startHour2 = 9;
    int startMinute2 = 30;
    int endHour2 = 10;
    int endMinute2 = 30;
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.JULY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.JULY, 10, 23, 59, 59);
    descriptionCheckWithInputs(true, context, startDate, endDate, startHour1, startMinute1, endHour1, endMinute1, startHour2, startMinute2, endHour2, endMinute2);
  }

  @Test
  public void testAddNewDescriptionWithCheckingInputsFailEndsBeforeStarts(TestContext context) {
    int startHour1 = 8;
    int startMinute1 = 00;
    int endHour1 = 7;
    int endMinute1 = 00;
    int startHour2 = 9;
    int startMinute2 = 30;
    int endHour2 = 10;
    int endMinute2 = 30;
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.JULY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.JULY, 10, 23, 59, 59);
    descriptionCheckWithInputs(false, context, startDate, endDate, startHour1, startMinute1, endHour1, endMinute1, startHour2, startMinute2, endHour2, endMinute2);
  }

  @Test
  public void testAddNewDescriptionWithCheckingInputsFailOverlappingTime1(TestContext context) {
    int startHour1 = 8;
    int startMinute1 = 00;
    int endHour1 = 9;
    int endMinute1 = 00;
    int startHour2 = 8;
    int startMinute2 = 30;
    int endHour2 = 10;
    int endMinute2 = 30;
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.JULY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.JULY, 10, 23, 59, 59);
    descriptionCheckWithInputs(false, context, startDate, endDate, startHour1, startMinute1, endHour1, endMinute1, startHour2, startMinute2, endHour2, endMinute2);
  }
  @Test
  public void testAddNewDescriptionWithCheckingInputsFailOverlappingTime2(TestContext context) {
    int startHour1 = 8;
    int startMinute1 = 00;
    int endHour1 = 9;
    int endMinute1 = 00;
    int startHour2 = 7;
    int startMinute2 = 30;
    int endHour2 = 8;
    int endMinute2 = 30;
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.JULY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.JULY, 10, 23, 59, 59);
    descriptionCheckWithInputs(false, context, startDate, endDate, startHour1, startMinute1, endHour1, endMinute1, startHour2, startMinute2, endHour2, endMinute2);
  }
  @Test
  public void testAddNewDescriptionWithCheckingInputsFailOverlappingTime3(TestContext context) {
    int startHour1 = 8;
    int startMinute1 = 00;
    int endHour1 = 9;
    int endMinute1 = 00;
    int startHour2 = 7;
    int startMinute2 = 30;
    int endHour2 = 10;
    int endMinute2 = 30;
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.JULY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.JULY, 10, 23, 59, 59);
    descriptionCheckWithInputs(false, context, startDate, endDate, startHour1, startMinute1, endHour1, endMinute1, startHour2, startMinute2, endHour2, endMinute2);
  }

  private void descriptionCheckWithInputs(boolean isAcceptableTime, TestContext context, Calendar startDate, Calendar endDate, int startHour1, int startMinute1, int endHour1, int endMinute1, int startHour2, int startMinute2, int endHour2, int endMinute2) {
    Async async = context.async();
    List<OpeningHour> openingHourList = new ArrayList<>();
    openingHourList.add(new OpeningHour().withStartTime(startHour1+":"+startMinute1+":00.000Z")
      .withEndTime(endHour1+":"+endMinute1+":00.000Z"));
    openingHourList.add(new OpeningHour().withStartTime(startHour2+":"+startMinute2+":00.000Z")
      .withEndTime(endHour2+":"+endMinute2+":00.000Z"));
    List<OpeningDay> openingDays = new ArrayList<>();
    OpeningDay monday = new OpeningDay().withDay(OpeningDay.Day.MONDAY).withOpen(true).withAllDay(false);
    monday.setOpeningHour(openingHourList);
    openingDays.add(monday);
    if(isAcceptableTime) {
      postDescription(startDate, endDate, openingDays).setHandler(result -> {
        if (result.succeeded()) {
          async.complete();
        } else {
          result.cause().printStackTrace();
          context.fail();
        }
      });
    } else {
      postDescription(startDate, endDate, openingDays).setHandler(result -> {
        if (result.succeeded()) {
          result.cause().printStackTrace();
          context.fail("Saving invalid interval should have fail");
        } else {
          async.complete();
        }
      });
    }
  }


  @Test
  public void testUpdateDescription(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.FEBRUARY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.FEBRUARY, 28, 23, 59, 59);
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

  @Test
  public void testDeleteDescription(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.AUGUST, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.AUGUST, 31, 23, 59, 59);
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
      deleteDescription(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(handler -> {
        if (handler.failed() && "Can not find description object.".equals(handler.cause().getMessage())) {
          System.out.println("Delete was successful.");
          f.complete();
        } else {
          f.fail("Failed to delete description.");
        }
      });
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
  public void testListEvents(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.APRIL, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.APRIL, 30, 23, 59, 59);
    List<OpeningDay> openingDays = new ArrayList<>();
    OpeningDay monday = new OpeningDay().withDay(OpeningDay.Day.MONDAY).withOpen(true).withAllDay(true);
    openingDays.add(monday);
    OpeningDay tuesday = new OpeningDay().withDay(OpeningDay.Day.TUESDAY).withOpen(true).withAllDay(true);
    openingDays.add(tuesday);
    OpeningDay wednesday = new OpeningDay().withDay(OpeningDay.Day.WEDNESDAY).withOpen(true).withAllDay(true);
    openingDays.add(wednesday);
    OpeningDay thursday = new OpeningDay().withDay(OpeningDay.Day.THURSDAY).withOpen(true).withAllDay(true);
    openingDays.add(thursday);
    OpeningDay friday = new OpeningDay().withDay(OpeningDay.Day.FRIDAY).withOpen(true).withAllDay(true);
    openingDays.add(friday);
    OpeningDay saturday = new OpeningDay().withDay(OpeningDay.Day.SATURDAY).withOpen(true).withAllDay(true);
    openingDays.add(saturday);
    OpeningDay sunday = new OpeningDay().withDay(OpeningDay.Day.SUNDAY).withOpen(true).withAllDay(true);
    openingDays.add(sunday);
    postDescription(startDate, endDate, openingDays).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      listEvents(f1.result(), 30).setHandler(f.completer());
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
  public void testListEventsWithOpeningHours(TestContext context) {
    int startHour = 8;
    int startMinute = 30;
    int endHour = 18;
    int endMinute = 40;
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.MAY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.MAY, 31, 23, 59, 59);
    List<OpeningHour> openingHourList = new ArrayList<>();
    openingHourList.add(new OpeningHour().withStartTime(getParsedTimeForHourAndMinute(startHour, startMinute))
      .withEndTime(getParsedTimeForHourAndMinute(endHour, endMinute)));
    List<OpeningDay> openingDays = new ArrayList<>();
    OpeningDay monday = new OpeningDay().withDay(OpeningDay.Day.MONDAY).withOpen(true).withAllDay(false);
    monday.setOpeningHour(openingHourList);
    openingDays.add(monday);
    OpeningDay tuesday = new OpeningDay().withDay(OpeningDay.Day.TUESDAY).withOpen(true).withAllDay(false);
    tuesday.setOpeningHour(openingHourList);
    openingDays.add(tuesday);
    OpeningDay wednesday = new OpeningDay().withDay(OpeningDay.Day.WEDNESDAY).withOpen(true).withAllDay(false);
    wednesday.setOpeningHour(openingHourList);
    openingDays.add(wednesday);
    OpeningDay thursday = new OpeningDay().withDay(OpeningDay.Day.THURSDAY).withOpen(true).withAllDay(false);
    thursday.setOpeningHour(openingHourList);
    openingDays.add(thursday);
    OpeningDay friday = new OpeningDay().withDay(OpeningDay.Day.FRIDAY).withOpen(true).withAllDay(false);
    friday.setOpeningHour(openingHourList);
    openingDays.add(friday);
    OpeningDay saturday = new OpeningDay().withDay(OpeningDay.Day.SATURDAY).withOpen(true).withAllDay(true);
    openingDays.add(saturday);
    OpeningDay sunday = new OpeningDay().withDay(OpeningDay.Day.SUNDAY).withOpen(true).withAllDay(true);
    openingDays.add(sunday);
    postDescription(startDate, endDate, openingDays).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      listEvents(f1.result(), 31).setHandler(f.completer());
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
  public void testAddExistingDescription(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.JUNE, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.JUNE, 30, 23, 59, 59);
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
      postDescription(startDate, endDate, openingDays).setHandler(handler -> {
        if (handler.failed() && "Failed to add description. Conflict".equals(handler.cause().getMessage())) {
          System.out.println("Does not allow to add overlapping periods.");
          f.complete();
        } else {
          f.fail("Should not allow to add the same interval multiple times.");
        }
      });
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
  public void testAddNewDescriptionWithoutEvents(TestContext context) {
    Async async = context.async();
    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, Calendar.FEBRUARY, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, Calendar.JANUARY, 1, 23, 59, 59);
    List<OpeningDay> openingDays = new ArrayList<>();
    OpeningDay monday = new OpeningDay().withDay(OpeningDay.Day.MONDAY).withOpen(true).withAllDay(true);
    openingDays.add(monday);
    postDescription(startDate, endDate, openingDays).setHandler(res -> {
      if (res.succeeded()) {
        context.fail("Saving invalid interval should have failed.");
      } else {
        res.cause().printStackTrace();
        async.complete();
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
        future.fail("Failed to add description. " + res.statusMessage());
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
            future.fail("Can not find description object.");
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
                  Date previousStartDate = mappedDescription.getStartDate();
                  Date previousEndDate = mappedDescription.getEndDate();
                  mappedDescription.setDescription("TEST_DESCRIPTION");
                  Calendar startDate = Calendar.getInstance();
                  startDate.set(2017, Calendar.MARCH, 1, 12, 0, 0);
                  Calendar endDate = Calendar.getInstance();
                  endDate.set(2017, Calendar.MARCH, 31, 18, 0, 0);
                  mappedDescription.setStartDate(startDate.getTime());
                  mappedDescription.setEndDate(endDate.getTime());
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
                                    if (mappedUpdatedDescription.getDescription().equals(mappedDescription.getDescription())
                                      && mappedUpdatedDescription.getStartDate().equals(mappedDescription.getStartDate())
                                      && mappedUpdatedDescription.getEndDate().equals(mappedDescription.getEndDate())
                                      && !previousStartDate.equals(mappedUpdatedDescription.getStartDate())
                                      && !previousEndDate.equals(mappedUpdatedDescription.getEndDate())) {
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
      .putHeader(CONTENT_TYPE_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .putHeader(ACCEPT_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .exceptionHandler(e -> {
        future.fail(e);
      })
      .end();
    return future;
  }

  private Future<String> deleteDescription(String descriptionId) {
    System.out.println("Deleting a description\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.delete(port, HOST, "/calendar/eventdescriptions/" + descriptionId, res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        future.complete();
      } else {
        future.fail("Failed to delete description with status: " + res.statusCode());
      }
    })
      .putHeader(TENANT_HEADER_KEY, TENANT)
      .putHeader(CONTENT_TYPE_HEADER_KEY, JSON_CONTENT_TYPE_HEADER_VALUE)
      .putHeader(ACCEPT_HEADER_KEY, "text/plain")
      .exceptionHandler(e -> {
        future.fail(e);
      })
      .end();
    return future;
  }

  private Future<String> listEvents(String descriptionId, int numberOfExpectedEvents) {
    System.out.println("Retrieving events for a description\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/events", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(buf -> {
          CalendarEventCollection eventListObject = buf.toJsonObject().mapTo(CalendarEventCollection.class);
          if (eventListObject.getTotalRecords() > 0) {
            List<Event> eventList = eventListObject.getEvents();
            List<Event> foundEvents = new ArrayList<>();
            for (Event event : eventList) {
              if (descriptionId.equals(event.getDescriptionId())) {
                foundEvents.add(event);
              }
            }
            if (foundEvents.size() == numberOfExpectedEvents) {
              future.complete(descriptionId);
            } else {
              future.fail("Can not find event object.");
            }
          } else {
            future.fail("Can not find event object.");
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

  private Description generateDescription(Calendar startDate, Calendar endDate, List<OpeningDay> openingDays) {
    return new Description()
      .withId(UUID.randomUUID().toString())
      .withDescription(UUID.randomUUID().toString())
      .withDescriptionType(Description.DescriptionType.OPENING_DAY)
      .withStartDate(startDate.getTime())
      .withEndDate(endDate.getTime())
      .withOpeningDays(openingDays);
  }

  private String getParsedTimeForHourAndMinute(int hour, int minute) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minute);
    return CalendarUtils.TIME_FORMAT.format(cal.getTime());
  }
}
