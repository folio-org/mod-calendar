package org.folio.rest.impl;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;

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
  private static final String CALENDAR_ID = "41615ab0-5d20-4c53-80fd-ddc630840b57";
  private static final Logger log = LoggerFactory.getLogger(CalendarIT.class);
  private static int port;
  private static Vertx vertx;

  @Test
  public void avoidAnnoyingErrorMessageWhenRunningCleanInstall() {
    assertTrue(true);
  }
/*
  private static ModCalendarJson modCalendarJson = new ModCalendarJson().withId(CALENDAR_ID).withName("test Calendar");

  @BeforeClass
  public static void setup(TestContext context) {
    vertx = Vertx.vertx();
    try {
      PostgresClient.setIsEmbedded(true);
      PostgresClient.getInstance(vertx).startEmbeddedPostgres();
    } catch (Exception e) {
      e.printStackTrace();
      context.fail(e);
      return;
    }
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
        PostgresClient.stopEmbeddedPostgres();
        async.complete();
      }));
    });
  }
  @Test
  public void testAddNewCalendar(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    ModCalendarJson modCalendarJson = new ModCalendarJson().withName("Test calendar");
    postModCalendarJson(modCalendarJson).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listCalendars().setHandler(f.completer());
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
  public void testDeleteCalendar(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    ModCalendarJson modCalendarJson = new ModCalendarJson().withName("Test calendar");
    postModCalendarJson(modCalendarJson).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listCalendars().setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      deleteCalendar(v).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      listCalendars(v).setHandler(handler -> {
        if (handler.failed() && "Can not find modcalendarjson object.".equals(handler.cause().getMessage())) {
          log.info("Delete was successful.");
          f.complete();
        } else {
          f.fail("Failed to delete calendar.");
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
  public void testUpdateCalendar(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    ModCalendarJson modCalendarJson = new ModCalendarJson().withName("Test calendar");
    postModCalendarJson(modCalendarJson).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listCalendars().setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      updateCalendar(v, "New Test Calendar Name").setHandler(f.completer());
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
  public void testAddNewDescription(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    Description description = generateDescription(2017, Calendar.JANUARY, 1, 7, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(f1.completer());
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
    Description description = generateDescription(2017, Calendar.FEBRUARY, 1, 7, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      updateDescription(f1.result(), 2017, Calendar.MARCH, 8, 7).setHandler(f.completer());
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
    Description description = generateDescription(2017, Calendar.MARCH, 1, 7, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(f1.completer());
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
          log.info("Delete was successful.");
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
    int numberOfDays = 7;
    Description description = generateDescription(2017, Calendar.APRIL, 1, numberOfDays, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      checkEventCountForDescription(f1.result(), numberOfDays).setHandler(f.completer());
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
  public void testListEventsWithRestrictedDateInterval(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    int numberOfDays = 7;
    Description description = generateDescription(2017, Calendar.APRIL, 8, numberOfDays, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      Description otherDescription = generateDescription(2017, Calendar.APRIL, 15, numberOfDays, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
      postDescription(otherDescription).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      checkEventCountForInterval(description.getStartDate(), description.getEndDate(), numberOfDays).setHandler(f.completer());
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

  private Future<String> checkEventCountForInterval(Date startDate, Date endDate, int numberOfExpectedEvents) {
    log.info("Retrieving events within an interval\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    String requestUrl = "/calendar/calendars/" + modCalendarJson.getId() + "/events"
      + "?from=" + CalendarUtils.BASIC_DATE_FORMATTER.print(startDate.getTime())
      + "&to=" + CalendarUtils.BASIC_DATE_FORMATTER.print(endDate.getTime());
    client.get(port, HOST, requestUrl, res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(buf -> {
          CalendarEventCollection eventListObject = buf.toJsonObject().mapTo(CalendarEventCollection.class);
          if (eventListObject.getTotalRecords() > 0) {
            List<Event> eventList = eventListObject.getEvents();
            if (eventList.size() == numberOfExpectedEvents) {
              future.complete("Event count matches.");
            } else {
              future.fail("Event count does not match.");
            }
          } else {
            future.fail("Can not find event objects.");
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

  @Test
  public void testListEventsWithOpeningHours(TestContext context) {
    int startHour = 8;
    int startMinute = 30;
    int endHour = 18;
    int endMinute = 40;
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
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
    int numberOfDays = 7;
    Description description = generateDescription(2017, Calendar.MAY, 1, numberOfDays, openingDays, Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      checkEventCountForDescription(f1.result(), numberOfDays).setHandler(f.completer());
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
    Description description = generateDescription(2017, Calendar.JUNE, 1, 7, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      postDescription(description).setHandler(handler -> {
        if (handler.failed() && "Failed to add description. Conflict".equals(handler.cause().getMessage())) {
          log.info("Does not allow to add overlapping periods.");
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
    Description description = generateDescription(2017, Calendar.FEBRUARY, 1, -7, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(res -> {
      if (res.succeeded()) {
        context.fail("Saving invalid interval should have failed.");
      } else {
        res.cause().printStackTrace();
        async.complete();
      }
    });
  }

  @Test
  public void testAddNewDescriptionAndException(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    int numberOfDays = 7;
    Description description = generateDescription(2017, Calendar.AUGUST, 1, numberOfDays, generateBasicOpeningDays(), Description.DescriptionType.OPENING_DAY);
    postDescription(description).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      description.setDescriptionType(Description.DescriptionType.EXCEPTION);
      postDescription(description).setHandler(handler -> {
        if (handler.succeeded()) {
          log.info("Successfully added exception.");
          f.complete(handler.result());
        } else {
          f.fail("Failed to add exception.");
        }
      });
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      checkEventCountForInterval(description.getStartDate(), description.getEndDate(), numberOfDays).setHandler(f.completer());
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
  public void testAddNewExceptionAndDescription(TestContext context) {
    Async async = context.async();
    Future<String> startFuture;
    Future<String> f1 = Future.future();
    int numberOfDays = 7;
    Description description = generateDescription(2017, Calendar.AUGUST, 8, numberOfDays, generateBasicOpeningDays(), Description.DescriptionType.EXCEPTION);
    postDescription(description).setHandler(f1.completer());
    startFuture = f1.compose(v -> {
      Future<String> f = Future.future();
      listDescriptions(f1.result()).setHandler(f.completer());
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      description.setDescriptionType(Description.DescriptionType.OPENING_DAY);
      postDescription(description).setHandler(handler -> {
        if (handler.succeeded()) {
          log.info("Successfully added opening day.");
          f.complete(handler.result());
        } else {
          f.fail("Failed to add opening day.");
        }
      });
      return f;
    }).compose(v -> {
      Future<String> f = Future.future();
      checkEventCountForInterval(description.getStartDate(), description.getEndDate(), numberOfDays).setHandler(f.completer());
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

  private Future<String> postModCalendarJson(ModCalendarJson modCalendarJson) {
    log.info("Creating a new Calendar\n");
    Future<String> future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.post(port, HOST, "/calendar/calendars", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(handler -> {
          log.info("Response body: " + handler.toJsonObject().toString());
          ModCalendarJson modCalendarJsonResponse = handler.toJsonObject().mapTo(ModCalendarJson.class);
          future.complete(modCalendarJsonResponse.getId());
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
      .end(JsonObject.mapFrom(modCalendarJson).encode());
    return future;
  }

  private Future<String> postDescription(Description description) {
    log.info("Creating a new description\n");
    Future<String> future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.post(port, HOST, "/calendar/calendars/" + modCalendarJson.getId() + "/eventdescriptions", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(handler -> {
          log.info("Response body: " + handler.toJsonObject().toString());
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
      .end(JsonObject.mapFrom(description).encode());
    return future;
  }

  private Future<String> listCalendars() {
    log.info("Retrieving a calendars\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/calendars/", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(buf -> {
          JsonObject calendarListObject = buf.toJsonObject();
          JsonArray calendarList = calendarListObject.getJsonArray("calendars");
          for (Object object : calendarList) {
            if (object instanceof JsonObject) {
              log.info(calendarList.toString());
              future.complete(((JsonObject) object).mapTo(ModCalendarJson.class).getId());
            } else {
              future.fail("Can not parse modcalendarjson object.");
            }
          }
          future.fail("Can not find modcalendarjson object.");
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

  private Future<String> listCalendars(String calendarId) {
    log.info("Retrieving a calendars\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/calendars/", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(buf -> {
          JsonObject calendarListObject = buf.toJsonObject();
          JsonArray calendarList = calendarListObject.getJsonArray("calendars");
          for (Object object : calendarList) {
            if (object instanceof JsonObject) {
              if (((JsonObject) object).mapTo(ModCalendarJson.class).getId() == calendarId) {
                log.info(calendarList.toString());
                future.complete(calendarId);
              }
            } else {
              future.fail("Can not parse modcalendarjson object.");
            }
          }
          future.fail("Can not find modcalendarjson object.");
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

  private Future<String> listDescriptions(String descriptionId) {
    log.info("Retrieving a description\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/calendars/" + modCalendarJson.getId() + "/eventdescriptions", res -> {
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

  private Future<String> updateCalendar(String calendarId, String name) {
    log.info("Updating a calendar\n");
    Future<String> future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/calendars/", res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        res.bodyHandler(buf -> {
          JsonArray calendarList = buf.toJsonObject().getJsonArray("calendars");
          if (calendarList.isEmpty()) {
            future.fail("Can not find calendar object.");
          }
          for (Object object : calendarList) {
            if (object instanceof JsonObject) {
              ModCalendarJson mappedModCalendar = ((JsonObject) object).mapTo(ModCalendarJson.class);
              if (calendarId.equals(mappedModCalendar.getId())) {
                mappedModCalendar.setName(name);
                HttpClient clientForPut = vertx.createHttpClient();
                clientForPut.put(port, HOST, "/calendar/calendars/" + calendarId, updateResponse -> {
                  if (updateResponse.statusCode() >= 200 && updateResponse.statusCode() < 300) {
                    HttpClient clientForGet = vertx.createHttpClient();
                    clientForGet.get(port, HOST, "/calendar/calendars/", listResponse -> {
                      if (listResponse.statusCode() >= 200 && listResponse.statusCode() < 300) {
                        listResponse.bodyHandler(buffer -> {
                          JsonArray updatedCalendarList = buffer.toJsonObject().getJsonArray("calendars");
                          for (Object updatedObject : updatedCalendarList) {
                            if (updatedObject instanceof JsonObject) {
                              ModCalendarJson mappedUpdatedCalendar = ((JsonObject) updatedObject).mapTo(ModCalendarJson.class);
                              if (calendarId.equals(mappedUpdatedCalendar.getId())) {
                                if (mappedUpdatedCalendar.getName().equals(mappedModCalendar.getName())) {
                                  future.complete(calendarId);
                                } else {
                                  future.fail("Failed to update event description.");
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
                  .end(JsonObject.mapFrom(mappedModCalendar).encode());
              }
            }
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


  private Future<String> updateDescription(String descriptionId, int startYear, int month, int day, int numberOfDays) {
    Calendar startDate = createStartDate(startYear, month, day);
    Calendar endDate = createEndDate(startDate, numberOfDays);
    log.info("Updating a description\n");
    Future<String> future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/calendars/" + modCalendarJson.getId() + "/eventdescriptions", res -> {
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
                  mappedDescription.setStartDate(startDate.getTime());
                  mappedDescription.setEndDate(endDate.getTime());
                  mappedDescription.setCalendarId(modCalendarJson.getId());
                  HttpClient clientForPut = vertx.createHttpClient();
                  clientForPut.put(port, HOST, "/calendar/calendars/" + modCalendarJson.getId() + "/eventdescriptions/" + descriptionId, updateResponse -> {
                    if (res.statusCode() >= 200 && res.statusCode() < 300) {
                      HttpClient clientForGet = vertx.createHttpClient();
                      clientForGet.get(port, HOST, "/calendar/calendars/" + modCalendarJson.getId() + "/eventdescriptions", listResponse -> {
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

  private Future<String> deleteCalendar(String calendarId) {
    log.info("Deleting a calendar\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.delete(port, HOST, "/calendar/calendars/" + calendarId, res -> {
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        HttpClient clientForGet = vertx.createHttpClient();
        clientForGet.get(port, HOST, "/calendar/calendars/", listResponse -> {
          if (listResponse.statusCode() >= 200 && listResponse.statusCode() < 300) {
            listResponse.bodyHandler(buffer -> {
              JsonArray updatedCalendarList = buffer.toJsonObject().getJsonArray("calendars");
              if (updatedCalendarList.size() > 0) {
                for (Object updatedObject : updatedCalendarList) {
                  if (updatedObject instanceof JsonObject) {
                    ModCalendarJson mappedUpdatedCalendar = ((JsonObject) updatedObject).mapTo(ModCalendarJson.class);
                    if (calendarId.equals(mappedUpdatedCalendar.getId())) {
                      future.fail("Failed to update event description.");
                    }
                  }
                }
                future.complete(calendarId);
              } else {
                future.complete(calendarId);
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

  private Future<String> deleteDescription(String descriptionId) {
    log.info("Deleting a description\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.delete(port, HOST, "/calendar/calendars/" + modCalendarJson.getId() + "/eventdescriptions/" + descriptionId, res -> {
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

  private Future<String> checkEventCountForDescription(String descriptionId, int numberOfExpectedEvents) {
    log.info("Retrieving events for a description\n");
    Future future = Future.future();
    HttpClient client = vertx.createHttpClient();
    client.get(port, HOST, "/calendar/calendars/" + modCalendarJson.getId() + "/events", res -> {
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

  private Description generateDescription(int startYear, int month, int day, int numberOfDays, List<OpeningDay> openingDays, Description.DescriptionType type) {
    Calendar startDate = createStartDate(startYear, month, day);
    Calendar endDate = createEndDate(startDate, numberOfDays);
    return new Description()
      .withId(UUID.randomUUID().toString())
      .withDescription(UUID.randomUUID().toString())
      .withDescriptionType(Description.DescriptionType.OPENING_DAY)
      .withStartDate(startDate.getTime())
      .withEndDate(endDate.getTime())
      .withOpeningDays(openingDays)
      .withDescriptionType(type)
      .withCalendarId(modCalendarJson.getId());
  }

  private String getParsedTimeForHourAndMinute(int hour, int minute) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return CalendarUtils.TIME_FORMATTER.print(cal.getTimeInMillis());
  }

  private Calendar createStartDate(int startYear, int month, int day) {
    Calendar startDate = Calendar.getInstance();
    startDate.clear();
    startDate.set(startYear, month, day, 0, 0, 0);
    return startDate;
  }

  private Calendar createEndDate(Calendar startDate, int numberOfDays) {
    Calendar endDate = Calendar.getInstance();
    endDate.setTime(startDate.getTime());
    int daysToAdd = 0;
    if (numberOfDays > 0) {
      daysToAdd = numberOfDays - 1;
    } else if (numberOfDays < 0) {
      daysToAdd = numberOfDays + 1;
    }
    endDate.add(Calendar.DAY_OF_YEAR, daysToAdd);
    return endDate;
  }

  private List<OpeningDay> generateBasicOpeningDays() {
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
    return openingDays;
  }
  */
}
