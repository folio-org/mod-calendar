package org.folio.rest.impl;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
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

//  @Test
//  public void avoidAnnoyingErrorMessageWhenRunningCleanInstall() {
//    assertTrue(true);
//  }

  @Rule
  public Timeout rule = Timeout.seconds(10);

  @BeforeClass
  public static void setup(TestContext context) {
    vertx = Vertx.vertx();
    port = NetworkUtils.nextFreePort();
    Async async = context.async();

    startEmbeddedPostgres(context);

    DeploymentOptions options = new DeploymentOptions()
      .setConfig(new JsonObject().put("http.port", port)
        .put(HttpClientMock2.MOCK_MODE, "true"));

    TenantClient tenantClient = new TenantClient(HOST, port, TENANT, TOKEN);
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
      Async async = context.async();
      PostgresClient.getInstance(vertx).runSQLFile(sql, true, result -> {
        if (result.failed()) {
          context.fail(result.cause());
        } else if (!result.result().isEmpty()) {
          context.fail("runSQLFile failed with: " + result.result().stream().collect(Collectors.joining(" ")));
        }
        async.complete();
      });
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
  public void checkEndpointTests() {


    given()
      .get("/calendar/periods/non_exist")
      .then()
      .statusCode(400);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .get("/calendar/periods")
      .then()
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningHoursCollection.json"))
      .statusCode(200);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .get("/calendar/periods/1/period")
      .then()
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningCollection.json"))
      .statusCode(200);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .get("/calendar/periods/1/period/uuid")
      .then()
      .contentType(ContentType.TEXT)
      .assertThat().body(equalTo("uuid"))
      .statusCode(404);
  }

  @Test
  public void testAddNewPeriod(TestContext context) {
    String uuid = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 7, "1", generateBasicOpeningDays(), uuid);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .post("/calendar/periods/1/period")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .statusCode(201);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .get("/calendar/periods/1/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);
  }

  @Test
  public void testIntervalOverlapPeriod(TestContext context) {
    String uuid = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 7, "2", generateBasicOpeningDays(), uuid);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .post("/calendar/periods/2/period")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .statusCode(201);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .get("/calendar/periods/2/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .post("/calendar/periods/2/period")
      .then()
      .contentType(ContentType.TEXT)
      .assertThat().body(equalTo("Intervals can not overlap."))
      .statusCode(500);
  }

  @Test
  public void tesPutPeriod(TestContext context) {
    String uuid = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2017, Calendar.JANUARY, 1, 7, "3", generateBasicOpeningDays(), uuid);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .post("/calendar/periods/3/period")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .statusCode(201);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .get("/calendar/periods/3/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);

    opening.setName("PUT_TEST");

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .put("/calendar/periods/3/period/" + uuid)
      .then()
      .statusCode(204);

    given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .get("/calendar/periods/3/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("name", equalTo("PUT_TEST"))
      .statusCode(200);
  }

  private OpeningPeriod_ generateDescription(int startYear, int month, int day, int numberOfDays, String servicePointId, List<OpeningDay_> openingDays, String uuid) {

    Calendar startDate = createStartDate(startYear, month, day);
    Calendar endDate = createEndDate(startDate, numberOfDays);
    OpeningPeriod_ openingPeriod = new OpeningPeriod_();
    openingPeriod.setId(uuid);
    openingPeriod.setStartDate(startDate.getTime());
    openingPeriod.setEndDate(endDate.getTime());
    openingPeriod.setServicePointId(servicePointId);
    openingPeriod.setName("test");

    OpeningDay_ opening = new OpeningDay_();
    opening.setWeekdays(new Weekdays().withDay(Weekdays.Day.MONDAY));
    OpeningDay openingDay = new OpeningDay().withAllDay(true).withOpen(true).withExceptional(false);
    List<OpeningHour> openingHours = new ArrayList<>();
    OpeningHour openingHour = new OpeningHour();
    openingHour.setStartTime("10:00");
    openingHour.setEndTime("12:00");
    openingHours.add(openingHour);
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

  private List<OpeningDay_> generateBasicOpeningDays() {
    List<OpeningDay_> openingDays = new ArrayList<>();
    OpeningDay_ monday = new OpeningDay_();
    monday.setWeekdays(new Weekdays().withDay(Weekdays.Day.MONDAY));
    monday.setOpeningDay(new OpeningDay().withAllDay(true).withOpen(true).withExceptional(false));
    openingDays.add(monday);
    OpeningDay_ tuesday = new OpeningDay_();
    tuesday.setWeekdays(new Weekdays().withDay(Weekdays.Day.TUESDAY));
    tuesday.setOpeningDay(new OpeningDay().withAllDay(false).withOpen(true).withExceptional(false));
    openingDays.add(tuesday);
    return openingDays;
  }
}
