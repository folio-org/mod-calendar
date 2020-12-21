package org.folio.rest.impl;

import static java.time.Month.APRIL;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static org.folio.rest.utils.CalendarUtils.DATE_PATTERN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.rest.RestVerticle;
import org.folio.rest.client.TenantClient;
import org.folio.rest.jaxrs.model.OpeningDay;
import org.folio.rest.jaxrs.model.OpeningDayWeekDay;
import org.folio.rest.jaxrs.model.OpeningHour;
import org.folio.rest.jaxrs.model.OpeningPeriod;
import org.folio.rest.jaxrs.model.TenantAttributes;
import org.folio.rest.jaxrs.model.TenantJob;
import org.folio.rest.jaxrs.model.Weekdays;
import org.folio.rest.tools.PomReader;
import org.folio.rest.tools.utils.NetworkUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import io.vertx.ext.web.client.HttpResponse;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class CalculateOpeningIT extends EmbeddedPostgresBase {

  private static final Logger logger = LogManager.getLogger(CalculateOpeningIT.class);

  private static RequestSpecification spec;
  private static SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);

  private static final String TENANT = "test";
  private static final String TOKEN = "test";
  private static final String SERVICE_POINT_ID = "acdee43c-9cf5-4f2c-aae0-8a70a2921c1a";

  private String testCaseName;
  private LocalDate requestedDate;
  private LocalDate prevDate;
  private LocalDate currentDate;
  private LocalDate nextDate;

  @Parameterized.Parameters
  public static List<Object[]> dates() {
    return Arrays.asList(new Object[][]{
      {
        "in the middle of the period (open day)",
        LocalDate.of(2019, Month.MARCH, 15),
        LocalDate.of(2019, Month.MARCH, 14),
        LocalDate.of(2019, Month.MARCH, 15),
        LocalDate.of(2019, Month.MARCH, 18)
      },
      {
        "in the middle of the period (closed day)",
        LocalDate.of(2019, Month.MARCH, 16),
        LocalDate.of(2019, Month.MARCH, 15),
        LocalDate.of(2019, Month.MARCH, 16),
        LocalDate.of(2019, Month.MARCH, 18)
      },
      {
        "at the beginning of the period",
        LocalDate.of(2019, Month.MARCH, 1),
        LocalDate.of(2019, JANUARY, 31),
        LocalDate.of(2019, Month.MARCH, 1),
        LocalDate.of(2019, Month.MARCH, 4)
      },
      {
        "at the end of the period",
        LocalDate.of(2019, Month.MARCH, 29),
        LocalDate.of(2019, Month.MARCH, 28),
        LocalDate.of(2019, Month.MARCH, 29),
        LocalDate.of(2019, Month.MAY, 1)
      },
      {
        "at the beginning of the period with exceptional",
        LocalDate.of(2020, Month.MARCH, 2),
        LocalDate.of(2020, Month.FEBRUARY, 15),
        LocalDate.of(2020, Month.MARCH, 2),
        LocalDate.of(2020, Month.MARCH, 3)
      },
      {
        "at the end of the period with exceptional",
        LocalDate.of(2020, Month.MARCH, 31),
        LocalDate.of(2020, Month.MARCH, 30),
        LocalDate.of(2020, Month.MARCH, 31),
        LocalDate.of(2020, Month.APRIL, 15)
      }
    });
  }


  public CalculateOpeningIT(String testCaseName,
                              LocalDate requestedDate,
                              LocalDate prevDate,
                              LocalDate currentDate,
                              LocalDate nextDate) {
    this.testCaseName = testCaseName;
    this.requestedDate = requestedDate;
    this.prevDate = prevDate;
    this.currentDate = currentDate;
    this.nextDate = nextDate;
  }

  @BeforeClass
  public static void setUp() {
    df.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

    Vertx vertx = Vertx.vertx();
    int port = NetworkUtils.nextFreePort();

    spec = new RequestSpecBuilder()
      .setContentType(ContentType.JSON)
      .setBaseUri("http://localhost:" + port)
      .addHeader("x-okapi-tenant", TENANT)
      .addHeader("x-okapi-token", TOKEN)
      .build();

    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
    TenantClient tenantClient = new TenantClient("http://localhost:" + port, TENANT, TOKEN);
    TenantAttributes tenantAttributes = new TenantAttributes()
      .withModuleTo(String.format("mod-calendar-%s", PomReader.INSTANCE.getVersion()));

    CompletableFuture<Void> future = new CompletableFuture<>();

    vertx.deployVerticle(RestVerticle.class.getName(), options, res -> {
      try {
        tenantClient.postTenant(tenantAttributes, postResult -> {
          if (postResult.failed()) {
            Throwable cause = postResult.cause();
            logger.error(cause);
            future.completeExceptionally(cause);
            return;
          }

          final HttpResponse<Buffer> postResponse = postResult.result();
          assertThat(postResponse.statusCode(), is(201));

          String jobId = postResponse.bodyAsJson(TenantJob.class).getId();

          tenantClient.getTenantByOperationId(jobId, 10000, getResult -> {
            if (getResult.failed()) {
              Throwable cause = getResult.cause();
              logger.error(cause.getMessage());
              future.completeExceptionally(cause);
              return;
            }

            final HttpResponse<Buffer> getResponse = getResult.result();
            assertThat(getResponse.statusCode(), is(200));
            assertThat(getResponse.bodyAsJson(TenantJob.class).getComplete(), is(true));

            populateOpeningPeriods();
            future.complete(null);
          });
        });
      } catch (Exception e) {
        logger.error(e.getMessage());
        future.completeExceptionally(e);
      }
    });

    future.join();
  }

  @Test
  public void testCalculateOpenings() throws ParseException {
    logger.info(String.format("Running test case `%s`", testCaseName));

    Response response = RestAssured.given()
      .spec(spec)
      .when()
      .get(String.format("/calendar/periods/%s/calculateopening?requestedDate=%s",
        SERVICE_POINT_ID, mapLocalDateToString(requestedDate, "yyyy-MM-dd")));

    List<OpeningDay> openingDays = new JsonObject(response.asString())
      .mapTo(OpeningPeriod.class)
      .getOpeningDays()
      .stream()
      .map(OpeningDayWeekDay::getOpeningDay)
      .collect(Collectors.toList());

    assertThat(mapStringToLocalDate(openingDays.get(0).getDate()), equalTo(prevDate));
    assertThat(mapStringToLocalDate(openingDays.get(1).getDate()), equalTo(currentDate));
    assertThat(mapStringToLocalDate(openingDays.get(2).getDate()), equalTo(nextDate));
  }

  private static void populateOpeningPeriods() {

    OpeningHour openingHour = new OpeningHour();
    openingHour.setStartTime("08:00");
    openingHour.setEndTime("17:00");
    List<OpeningHour> openingHours = Collections.singletonList(openingHour);

    List<OpeningDayWeekDay> openingDays = new ArrayList<>();
    openingDays.add(buildOpeningDayWeekDay(Weekdays.Day.MONDAY, openingHours));
    openingDays.add(buildOpeningDayWeekDay(Weekdays.Day.TUESDAY, openingHours));
    openingDays.add(buildOpeningDayWeekDay(Weekdays.Day.WEDNESDAY, openingHours));
    openingDays.add(buildOpeningDayWeekDay(Weekdays.Day.THURSDAY, openingHours));
    openingDays.add(buildOpeningDayWeekDay(Weekdays.Day.FRIDAY, openingHours));

    //populate 3 periods for testing
    //prev1     - January 2019, Monday-Friday, 8:00 - 17:00
    //current1  - March   2019, Monday-Friday, 8:00 - 17:00
    //next1     - May     2019, Monday-Friday, 8:00 - 17:00
    createOpeningPeriod("prev1", LocalDate.of(2019, JANUARY, 1), LocalDate.of(2019, JANUARY, 31), openingDays);
    createOpeningPeriod("current1", LocalDate.of(2019, MARCH, 1), LocalDate.of(2019, MARCH, 31), openingDays);
    createOpeningPeriod("next1", LocalDate.of(2019, MAY, 1), LocalDate.of(2019, MAY, 31), openingDays);

    //populate 3 more periods for testing with exceptional
    //prev2     - January 2020, Monday-Friday, 8:00 - 17:00
    //current2  - March   2020, Monday-Friday, 8:00 - 17:00
    //next2     - May     2020, Monday-Friday, 8:00 - 17:00
    createOpeningPeriod("prev2", LocalDate.of(2020, JANUARY, 1), LocalDate.of(2020, JANUARY, 31), openingDays);
    createOpeningPeriod("current2", LocalDate.of(2020, MARCH, 1), LocalDate.of(2020, MARCH, 31), openingDays);
    createOpeningPeriod("next2", LocalDate.of(2020, MAY, 1), LocalDate.of(2020, MAY, 31), openingDays);

    //populate exceptional
    createOpeningPeriod("prevExcptional", LocalDate.of(2020, FEBRUARY, 15), LocalDate.of(2020, FEBRUARY, 15),
      Collections.singletonList(
        buildOpeningDayWeekDay(mapLocalDateToString(
          LocalDate.of(2020, FEBRUARY, 15)), true, true, false, null, openingHours)));

    createOpeningPeriod("prevExcptional", LocalDate.of(2020, APRIL, 15), LocalDate.of(2020, APRIL, 15),
      Collections.singletonList(
        buildOpeningDayWeekDay(mapLocalDateToString(
          LocalDate.of(2020, APRIL, 15)), true, true, false, null, openingHours)));
  }

  private static void createOpeningPeriod(String name, LocalDate startDate, LocalDate endDate,
                                                   List<OpeningDayWeekDay> openingDays) {

    OpeningPeriod period = new OpeningPeriod();
    period.setId(UUID.randomUUID().toString());
    period.setName(name);
    period.setServicePointId(SERVICE_POINT_ID);
    period.setOpeningDays(openingDays);

    try {
      period.setStartDate(df.parse(mapLocalDateToString(startDate)));
      period.setEndDate(df.parse(mapLocalDateToString(endDate)));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    RestAssured.given()
      .spec(spec)
      .body(JsonObject.mapFrom(period).encode())
      .when()
      .post("/calendar/periods/" + SERVICE_POINT_ID + "/period")
      .then()
      .statusCode(201);
  }

  private static OpeningDayWeekDay buildOpeningDayWeekDay(String date, boolean open, boolean exceptional,
                                                          boolean allDay, Weekdays weekdays,
                                                          List<OpeningHour> openingHour) {

    OpeningDay openingDay = new OpeningDay();
    openingDay.setDate(date);
    openingDay.setOpen(open);
    openingDay.setExceptional(exceptional);
    openingDay.setAllDay(allDay);
    openingDay.setOpeningHour(openingHour);

    OpeningDayWeekDay openingDayWeekDay = new OpeningDayWeekDay();
    openingDayWeekDay.setWeekdays(weekdays);
    openingDayWeekDay.setOpeningDay(openingDay);

    return openingDayWeekDay;
  }

  private static OpeningDayWeekDay buildOpeningDayWeekDay(Weekdays.Day day, List<OpeningHour> openingHours) {
    return buildOpeningDayWeekDay(null, true, false, false, new Weekdays().withDay(day), openingHours);
  }

  private static LocalDate mapStringToLocalDate(String date) throws ParseException {
    return df.parse(date)
      .toInstant()
      .atZone(ZoneOffset.UTC)
      .toLocalDate();
  }

  private static String mapLocalDateToString(LocalDate date) {
    return mapLocalDateToString(date, DATE_PATTERN);
  }

  private static String mapLocalDateToString(LocalDate date, String pattern) {
    ZonedDateTime zonedDateTime = ZonedDateTime.of(date, LocalTime.of(0, 0), ZoneOffset.UTC);
    return DateTimeFormatter.ofPattern(pattern).withZone(ZoneOffset.UTC).format(zonedDateTime);
  }
}
