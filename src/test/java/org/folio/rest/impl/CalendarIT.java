package org.folio.rest.impl;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.folio.rest.RestVerticle;
import org.folio.rest.client.TenantClient;
import org.folio.rest.jaxrs.model.*;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.client.test.HttpClientMock2;
import org.folio.rest.tools.utils.NetworkUtils;
import org.folio.rest.utils.CalendarUtils;
import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(VertxUnitRunner.class)
public class CalendarIT {
  private static final Header TENANT_HEADER = new Header("X-Okapi-Tenant", "test");
  private static final Header TOKEN_HEADER = new Header("X-Okapi-Token", "test");
  private static final Header OKAPI_URL_HEADER = new Header("X-Okapi-Url", "http://localhost:9130");
  private static final String TENANT = "test";
  private static final String TOKEN = "test";
  private static final String HOST = "localhost";
  private static final Header JSON_CONTENT_TYPE_HEADER = new Header("Content-Type", "application/json");
  private static final Logger log = LoggerFactory.getLogger(CalendarIT.class);

  private static int port;
  private static Vertx vertx;

  @Rule
  public Timeout rule = Timeout.seconds(60);

  @BeforeClass
  public static void setup(TestContext context) {
    vertx = Vertx.vertx();
    port = NetworkUtils.nextFreePort();

    startEmbeddedPostgres(context);

    DeploymentOptions options = new DeploymentOptions()
      .setConfig(new JsonObject().put("http.port", port)
        .put(HttpClientMock2.MOCK_MODE, "true"));

    TenantClient tenantClient = new TenantClient(HOST, port, TENANT, TOKEN);
    Async async = context.async();
    vertx.deployVerticle(RestVerticle.class.getName(), options, res -> {
      try {
        tenantClient.post(null, res2 -> async.complete());
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
      String sql = "drop schema if exists test_mod_calendar cascade;\n"
        + "drop role if exists test_mod_calendar;\n";
      executeSql(context, sql);
    } catch (Exception e) {
      log.error("", e);
      context.fail(e);
    }
  }

  @AfterClass
  public static void teardown(TestContext context) {
    Async async = context.async();
    vertx.close(context.asyncAssertSuccess(res -> {
      PostgresClient.stopEmbeddedPostgres();
      async.complete();
    }));
  }

  @Test
  public void checkEndpointsTest() {

    given()
      .get("/calendar/periods/non_exist")
      .then()
      .statusCode(400);

    getWithHeaderAndBody("/calendar/periods")
      .then()
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningHoursCollection.json"))
      .statusCode(200);

    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/period")
      .then()
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningCollection.json"))
      .statusCode(200);

    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/period/uuid")
      .then()
      .contentType(ContentType.TEXT)
      .assertThat().body(equalTo("uuid"))
      .statusCode(404);
  }

  //@Test
  public void postgresClientFailureTest(TestContext context) {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 7, servicePointUUID, uuid, true, true, false);

    String sql = "ALTER TABLE test_mod_calendar.openings RENAME TO openings_temp;" +
      "ALTER TABLE test_mod_calendar.regular_hours RENAME TO regular_hours_temp;" +
      "ALTER TABLE test_mod_calendar.actual_opening_hours RENAME TO actual_opening_hours_temp;";

    executeSql(context, sql);

    postWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.TEXT)
      .assertThat().body(equalTo("Error while listing events."))
      .statusCode(500);

    putFailure(servicePointUUID, opening);

    sql = "ALTER TABLE test_mod_calendar.openings_temp RENAME TO openings;" +
      "ALTER TABLE test_mod_calendar.regular_hours_temp RENAME TO regular_hours;";

    executeSql(context, sql);

    putFailure(servicePointUUID, opening);

    sql = "ALTER TABLE test_mod_calendar.openings RENAME TO openings_temp;" +
      "ALTER TABLE test_mod_calendar.regular_hours RENAME TO regular_hours_temp;";
    executeSql(context, sql);

    deleteWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .statusCode(500);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.TEXT)
      .statusCode(500);

    getWithHeaderAndBody("/calendar/periods")
      .then()
      .contentType(ContentType.TEXT)
      .statusCode(500);

    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/period")
      .then()
      .contentType(ContentType.TEXT)
      .statusCode(500);

    sql = "ALTER TABLE test_mod_calendar.openings_temp RENAME TO openings;" +
      "ALTER TABLE test_mod_calendar.regular_hours_temp RENAME TO regular_hours;" +
      "ALTER TABLE test_mod_calendar.actual_opening_hours_temp RENAME TO actual_opening_hours;";
    executeSql(context, sql);
  }

  private static Future executeSql(TestContext context, String sql) {
    Async async = context.async();
    Future future = Future.future();
    PostgresClient postgresClient = PostgresClient.getInstance(vertx);
    postgresClient.runSQLFile(sql, false, result -> {
      if (result.failed()) {
        context.fail(result.cause());
        future.failed();
      } else if (!result.result().isEmpty()) {
        context.fail("runSQLFile failed with: " + result.result().stream().collect(Collectors.joining(" ")));
        future.failed();
      } else if (result.succeeded()) {
        future.complete();
      }
      async.complete();
    });
    return future;
  }


  @Test
  public void addNewPeriodTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 7, servicePointUUID, uuid, true, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);
  }

  @Test
  public void addInvalidPeriodTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 0, servicePointUUID, uuid, true, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);
  }

  @Test
  public void addNewPeriodWithoutServicePoint() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 7, servicePointUUID, uuid, true, true, false);
    opening.setServicePointId(null);
    postWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.TEXT)
      .assertThat().body(equalTo("Not valid json object. Missing field(s)..."))
      .statusCode(400);
  }

  @Test
  public void getPeriodsWithServicePointIdTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2018, Calendar.AUGUST, 27, 8, servicePointUUID, uuid, true, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods?servicePointId=" + servicePointUUID + "&includeClosedDays=false")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningHoursCollection.json"))
      .body("totalRecords", equalTo(2))
      .statusCode(200);
  }

  @Test

  public void getPeriodsWithIncludedDateRange() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2018, Calendar.AUGUST, 27, 8, servicePointUUID, uuid, true, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods?startDate=2018-06-01&endDate=2018-10-30&servicePointId=" + servicePointUUID + "&includeClosedDays=false")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningHoursCollection.json"))
      .body("totalRecords", equalTo(2))
      .statusCode(200);
  }

  public void getPeriodsWithExcludedDateRange() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2018, Calendar.AUGUST, 27, 8, servicePointUUID, uuid, true, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods?startDate=2020-06-01&endDate=2020-10-30&servicePointId=" + servicePointUUID + "&includeClosedDays=false")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningHoursCollection.json"))
      .body("totalRecords", equalTo(0))
      .statusCode(200);
  }

  private void postPeriod(String servicePointUUID, OpeningPeriod_ opening) {
    postWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .statusCode(201);
  }

  @Test
  public void getPeriodWithOpeningDaysTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2018, Calendar.JANUARY, 1, 7, servicePointUUID, uuid, true, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid + "?withOpeningDays=true&showPast=true")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);
  }

  @Test
  public void getPeriodsExceptionalTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2019, Calendar.JANUARY, 1, 7, servicePointUUID, uuid, true, false, true);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period?withOpeningDays=true&showPast=true&showExceptional=true")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningCollection.json"))
      .body("totalRecords", greaterThan(0))
      .statusCode(200);
  }

  @Test
  public void deletePeriodTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2020, Calendar.JANUARY, 1, 7, servicePointUUID, uuid, true, true, true);

    deleteWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .statusCode(204);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .body(equalTo(uuid))
      .statusCode(404);
  }

  @Test
  public void overlappingPeriodsTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 7, servicePointUUID, uuid, false, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);

    postWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.TEXT)
      .assertThat().body(equalTo("Intervals can not overlap."))
      .statusCode(500);
  }

  @Test
  public void putPeriodTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 7, servicePointUUID, uuid, true, false, true);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);

    opening.setName("PUT_TEST");

    putWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .statusCode(204);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("name", equalTo("PUT_TEST"))
      .statusCode(200);
  }

  private OpeningPeriod_ generateDescription(int startYear, int month, int day, int numberOfDays, String servicePointId, String uuid, boolean isAllDay, boolean isOpen, boolean isExceptional) {
    List<OpeningDay_> openingDays = new ArrayList<>();
    Calendar startDate = createStartDate(startYear, month, day);
    Calendar endDate = createEndDate(startDate, numberOfDays);
    OpeningPeriod_ openingPeriod = new OpeningPeriod_();
    openingPeriod.setId(uuid);
    openingPeriod.setStartDate(startDate.getTime());
    openingPeriod.setEndDate(endDate.getTime());
    openingPeriod.setServicePointId(servicePointId);
    openingPeriod.setName("test");

    OpeningDay_ opening = new OpeningDay_();
    OpeningDay openingDay = new OpeningDay().withAllDay(isAllDay).withOpen(isOpen).withExceptional(isExceptional);
    List<OpeningHour> openingHours = new ArrayList<>();
    if (!isExceptional) {
      opening.setWeekdays(new Weekdays().withDay(Weekdays.Day.MONDAY));
      OpeningHour openingHour = new OpeningHour();
      openingHour.setStartTime("08:00");
      openingHour.setEndTime("12:00");
      openingHours.add(openingHour);
      openingHour.setStartTime("13:00");
      openingHour.setEndTime("17:00");
      openingHours.add(openingHour);
    } else {
      openingDay.setAllDay(true);
    }
    openingDay.setOpeningHour(openingHours);
    opening.setOpeningDay(openingDay);

    openingDays.add(opening);
    openingPeriod.setOpeningDays(openingDays);
    return openingPeriod;
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

  private void putFailure(String servicePointUUID, OpeningPeriod_ opening) {
    putWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period/" + UUID.randomUUID().toString())
      .then()
      .statusCode(500);
  }

  private Response getWithHeaderAndBody(String path) {
    return restGivenWithHeader()
      .get(path);
  }

  private Response postWithHeaderAndBody(OpeningPeriod_ opening, String path) {
    return restGivenWithHeader()
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .post(path);
  }

  private Response putWithHeaderAndBody(OpeningPeriod_ opening, String path) {
    return restGivenWithHeader()
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .put(path);
  }

  private Response deleteWithHeaderAndBody(OpeningPeriod_ opening, String path) {
    return restGivenWithHeader()
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .delete(path);
  }

  private RequestSpecification restGivenWithHeader() {
    return given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER);
  }
}
