package org.folio.rest.impl;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.folio.rest.utils.CalendarConstants.SERVICE_POINT_ID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.StringContains.containsString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.folio.postgres.testing.PostgresTesterContainer;
import org.folio.rest.RestVerticle;
import org.folio.rest.client.TenantClient;
import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Errors;
import org.folio.rest.jaxrs.model.OpeningDay;
import org.folio.rest.jaxrs.model.OpeningDayWeekDay;
import org.folio.rest.jaxrs.model.OpeningHour;
import org.folio.rest.jaxrs.model.OpeningPeriod;
import org.folio.rest.jaxrs.model.Weekdays;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.utils.NetworkUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class CalendarIT extends EmbeddedPostgresBase {

  private static final Header TENANT_HEADER = new Header("X-Okapi-Tenant", "test");
  private static final Header TOKEN_HEADER = new Header("X-Okapi-Token", "test");
  private static final Header OKAPI_URL_HEADER = new Header("X-Okapi-Url", "http://localhost:9130");
  private static final String TENANT = "test";
  private static final String TOKEN = "test";
  private static final String HOST = "localhost";
  private static final Header JSON_CONTENT_TYPE_HEADER = new Header("Content-Type", "application/json");

  private static final String ERROR_CODE_INTERVALS_OVERLAP = "intervalsOverlap";
  private static final String ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP = "Intervals cannot overlap.";
  private static final String ERROR_MESSAGE_OPENING_PERIOD_INTERVALS_OVERLAP = "The date range entered overlaps " +
    "with another calendar for this service point. Please correct the date range or enter the hours as exceptions.";

  @Rule
  public Timeout rule = Timeout.seconds(60);

  @BeforeClass
  public static void setup(TestContext context) {
    PostgresClient.setPostgresTester(new PostgresTesterContainer());

    Vertx vertx = Vertx.vertx();
    int port = NetworkUtils.nextFreePort();

    DeploymentOptions options = new DeploymentOptions()
      .setConfig(new JsonObject().put("http.port", port));

    tenantClient = new TenantClient("http://" + HOST + ":" + port, TENANT, TOKEN);

    Async async = context.async();
    vertx.deployVerticle(RestVerticle.class.getName(), options,
      res -> postTenant(tenantClient, async::complete, context::fail));

    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterClass
  public static void tearDown() {
    deleteTenant(tenantClient);
  }

  @Test
  public void checkEndpointsTest() {

    given()
      .get("/calendar/periods/non_exist")
      .then()
      .statusCode(404);

    getWithHeaderAndBody("/calendar/periods")
      .then()
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningHoursCollection.json"))
      .statusCode(200);

    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/period")
      .then()
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningCollection.json"))
      .statusCode(200);

    String uuid = UUID.randomUUID().toString();
    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/period/" + uuid)
      .then()
      .contentType(ContentType.TEXT)
      .assertThat().body(containsString(uuid))
      .statusCode(404);
  }

  @Test
  public void addNewPeriodTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2017, Calendar.JANUARY, 1, 7, servicePointUUID, uuid,
      true, true, false, openingHours);

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
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2017, Calendar.JANUARY, 1, 0, servicePointUUID, uuid,
      true, true, false, openingHours);

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
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2017, Calendar.JANUARY, 1, 7, servicePointUUID, uuid,
      true, true, false, openingHours);
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
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2018, Calendar.AUGUST, 27, 8, servicePointUUID, uuid,
      true, true, false, openingHours);

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
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2018, Calendar.AUGUST, 27, 8, servicePointUUID, uuid,
      true, true, false, openingHours);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods?startDate=2018-06-01&endDate=2018-10-30&servicePointId=" + servicePointUUID + "&includeClosedDays=false")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningHoursCollection.json"))
      .body("totalRecords", equalTo(2))
      .statusCode(200);
  }

  @Test
  public void getPeriodsWithExcludedDateRange() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");

    OpeningPeriod opening = generateDescription(2018, Calendar.AUGUST, 27, 8, servicePointUUID, uuid,
      true, true, false, openingHours);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods?startDate=2020-06-01&endDate=2020-10-30&servicePointId=" + servicePointUUID + "&includeClosedDays=false")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningHoursCollection.json"))
      .body("totalRecords", equalTo(0))
      .statusCode(200);
  }

  private void postPeriod(String servicePointUUID, OpeningPeriod opening) {
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

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2018, Calendar.JANUARY, 1, 7, servicePointUUID, uuid,
      true, true, false, openingHours);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid + "?withOpeningDays=true&showPast=true")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);
  }

  @Test
  public void testGetCalendarPeriodsCalculateOpening() {
    String pathCalculateOpening = "/calendar/periods/%s/calculateopening?requestedDate=%s";
    LocalDate date = LocalDate.of(2019, Month.MARCH, 15);
    String requestedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      .withZone(ZoneOffset.UTC)
      .format(ZonedDateTime.of(date, LocalTime.of(0, 0), ZoneOffset.UTC));

    restGivenWithHeader()
      .when()
      .get(String.format(pathCalculateOpening,
        SERVICE_POINT_ID, requestedDate))
      .then()
      .contentType(ContentType.JSON)
      .statusCode(200);
  }

  @Test
  public void getPeriodsExceptionalTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2019, Calendar.JANUARY, 1, 7, servicePointUUID, uuid,
      true, false, true, openingHours);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period?withOpeningDays=true&showPast=true&showExceptional=true")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/OpeningCollection.json"))
      .body("totalRecords", greaterThan(0))
      .statusCode(200);
  }

  @Test
  public void postExceptionalTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2019, Calendar.JANUARY, 1, 7, servicePointUUID, uuid,
      null, false, true, openingHours);

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

    // create a new period
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2020, Calendar.JANUARY, 1, 7, servicePointUUID, uuid,
      true, true, true, openingHours);
    postPeriod(servicePointUUID, opening);

    // check created period
    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body("id", equalTo(uuid))
      .statusCode(200);

    // delete created period
    deleteWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .statusCode(204);

    // verify that the created period has been deleted
    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .body(containsString(uuid))
      .statusCode(404);
  }

  @Test
  public void deleteNotExistPeriodTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    String msg = String.format("Openings with id '%s' is not found", uuid);

    // delete not exist period
    deleteWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.TEXT)
      .assertThat().body(containsString(msg))
      .statusCode(404);
  }

  @Test
  public void overlappingPeriodsTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2017, Calendar.JANUARY, 1, 7, servicePointUUID, uuid,
      false, true, false, openingHours);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .statusCode(200);

    postWithHeaderAndBody(opening, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void canCreateExceptionalPeriodWithoutOverlapping() {
    String servicePointUUID = UUID.randomUUID().toString();

    // create a new calendar
    String uuid = UUID.randomUUID().toString();
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2020, Calendar.MARCH, 1, 5,
      servicePointUUID, uuid, true, true, false, openingHours);
    postPeriod(servicePointUUID, opening);

    createExceptionalExistingPeriod(servicePointUUID);

    // save a new exceptional period
    String newIdExPeriod = UUID.randomUUID().toString();
    OpeningPeriod newExceptionalPeriod = generateDescription(2020, Calendar.MARCH, 6, 1,
      servicePointUUID, newIdExPeriod, true, true, true, openingHours);
    postWithHeaderAndBody(newExceptionalPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .statusCode(201);
  }

  @Test
  public void cannotCreateExceptionalPeriodWithOverlappingAtTheBeginning() {
    String servicePointUUID = UUID.randomUUID().toString();
    createExceptionalExistingPeriod(servicePointUUID);

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod invalidExceptionalPeriod = generateDescription(2020, Calendar.MARCH, 3, 3,
      servicePointUUID, UUID.randomUUID().toString(), true, false, true, openingHours);
    postWithHeaderAndBody(invalidExceptionalPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateExceptionalPeriodWithOverlappingAtTheEnd() {
    String servicePointUUID = UUID.randomUUID().toString();
    createExceptionalExistingPeriod(servicePointUUID);

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod invalidExceptionalPeriod = generateDescription(2020, Calendar.FEBRUARY, 28, 3,
      servicePointUUID, UUID.randomUUID().toString(), true, false, true, openingHours);
    postWithHeaderAndBody(invalidExceptionalPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateExceptionalPeriodInsideOverlapping() {
    String servicePointUUID = UUID.randomUUID().toString();
    createExceptionalExistingPeriod(servicePointUUID);

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod invalidExceptionalPeriod = generateDescription(2020, Calendar.MARCH, 2, 2,
      servicePointUUID, UUID.randomUUID().toString(), true, false, true, openingHours);
    postWithHeaderAndBody(invalidExceptionalPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateExceptionalPeriodWithTotalOverlapping() {
    String servicePointUUID = UUID.randomUUID().toString();
    createExceptionalExistingPeriod(servicePointUUID);

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod invalidExceptionalPeriod = generateDescription(2020, Calendar.FEBRUARY, 28, 10,
      servicePointUUID, UUID.randomUUID().toString(), true, false, true, openingHours);
    postWithHeaderAndBody(invalidExceptionalPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void putPeriodTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod opening = generateDescription(2017, Calendar.JANUARY, 1, 7,
      servicePointUUID, uuid, true, false, true, openingHours);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("name", equalTo("test"))
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

  @Test
  public void cannotUpdateOverlappingExceptionalPeriod() {

    String servicePointUUID = UUID.randomUUID().toString();
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod existingOpening = generateDescription(2020, Calendar.JANUARY, 1, 7,
      servicePointUUID, UUID.randomUUID().toString(), true, false, true, openingHours);
    postPeriod(servicePointUUID, existingOpening);

    String uuid = UUID.randomUUID().toString();
    OpeningPeriod opening = generateDescription(2020, Calendar.JANUARY, 10, 7,
      servicePointUUID, uuid, true, false, true, openingHours);
    postPeriod(servicePointUUID, opening);

    OpeningPeriod invalidUpdatedOpening = generateDescription(2020, Calendar.JANUARY, 4, 7,
      servicePointUUID, uuid, true, false, true, openingHours);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("name", equalTo("test"))
      .statusCode(200);

    putWithHeaderAndBody(invalidUpdatedOpening.withName("PUT_TEST"), "/calendar/periods/" + servicePointUUID + "/period/" + uuid)
      .then()
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateOpeningPeriodWithOverlappingAtTheBeginning() {
    String servicePointUUID = UUID.randomUUID().toString();
    createWorkingHoursExistingPeriod(servicePointUUID);

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod invalidOpeningPeriod = generateDescription(2020, Calendar.MARCH, 3, 3,
      servicePointUUID, UUID.randomUUID().toString(), true, false, false, openingHours);
    postWithHeaderAndBody(invalidOpeningPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_OPENING_PERIOD_INTERVALS_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateOpeningPeriodWithOverlappingAtTheEnd() {
    String servicePointUUID = UUID.randomUUID().toString();
    createWorkingHoursExistingPeriod(servicePointUUID);

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod invalidOpeningPeriod = generateDescription(2020, Calendar.FEBRUARY, 28, 3,
      servicePointUUID, UUID.randomUUID().toString(), true, false, false, openingHours);
    postWithHeaderAndBody(invalidOpeningPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_OPENING_PERIOD_INTERVALS_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateOpeningPeriodInsideOverlapping() {
    String servicePointUUID = UUID.randomUUID().toString();
    createWorkingHoursExistingPeriod(servicePointUUID);

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod invalidOpeningPeriod = generateDescription(2020, Calendar.MARCH, 2, 2,
      servicePointUUID, UUID.randomUUID().toString(), true, false, false, openingHours);
    postWithHeaderAndBody(invalidOpeningPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_OPENING_PERIOD_INTERVALS_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateOpeningPeriodWithTotalOverlapping() {
    String servicePointUUID = UUID.randomUUID().toString();
    createWorkingHoursExistingPeriod(servicePointUUID);

    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod invalidOpeningPeriod = generateDescription(2020, Calendar.FEBRUARY, 28, 10,
      servicePointUUID, UUID.randomUUID().toString(), true, false, false, openingHours);
    postWithHeaderAndBody(invalidOpeningPeriod, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_OPENING_PERIOD_INTERVALS_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateWorkingHoursWithOverlappingAtTheEnd() {
    String servicePointUUID = UUID.randomUUID().toString();

    List<OpeningHour> openingHoursWithOverlapping = prepareOpeningHours("08:00", "14:00", "12:00", "16:00");
    OpeningPeriod invalidOpeningPeriodWithOverlapping = generateDescription(2020, Calendar.JANUARY, 8, 2,
      servicePointUUID, UUID.randomUUID().toString(), false, true, false, openingHoursWithOverlapping);

    postWithHeaderAndBody(invalidOpeningPeriodWithOverlapping, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateWorkingHoursWithOverlappingAtTheBeginning() {
    String servicePointUUID = UUID.randomUUID().toString();

    List<OpeningHour> openingHoursWithOverlapping = prepareOpeningHours("08:00", "14:00", "06:00", "09:00");
    OpeningPeriod invalidOpeningPeriodWithOverlapping = generateDescription(2020, Calendar.JANUARY, 8, 2,
      servicePointUUID, UUID.randomUUID().toString(), false, true, false, openingHoursWithOverlapping);

    postWithHeaderAndBody(invalidOpeningPeriodWithOverlapping, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  @Test
  public void cannotCreateWorkingHoursWithTotalOverlapping() {
    String servicePointUUID = UUID.randomUUID().toString();

    List<OpeningHour> openingHoursWithOverlapping = prepareOpeningHours("08:00", "14:00", "10:00", "12:00");
    OpeningPeriod invalidOpeningPeriodWithOverlapping = generateDescription(2020, Calendar.JANUARY, 8, 2,
      servicePointUUID, UUID.randomUUID().toString(), false, true, false, openingHoursWithOverlapping);

    postWithHeaderAndBody(invalidOpeningPeriodWithOverlapping, "/calendar/periods/" + servicePointUUID + "/period")
      .then()
      .contentType(ContentType.JSON)
      .assertThat().body(matchesJsonSchema(errIntervalsOverlapSchema(ERROR_MESSAGE_INTERVALS_CANNOT_OVERLAP)))
      .statusCode(422);
  }

  private OpeningPeriod generateDescription(
    int startYear, int month, int day, int numberOfDays, String servicePointId, String uuid, Boolean isAllDay,
    boolean isOpen, boolean isExceptional, List<OpeningHour> openingHours) {
    List<OpeningDayWeekDay> openingDays = new ArrayList<>();
    Calendar startDate = createStartDate(startYear, month, day);
    Calendar endDate = createEndDate(startDate, numberOfDays);
    OpeningPeriod openingPeriod = new OpeningPeriod();
    openingPeriod.setId(uuid);
    openingPeriod.setStartDate(startDate.getTime());
    openingPeriod.setEndDate(endDate.getTime());
    openingPeriod.setServicePointId(servicePointId);
    openingPeriod.setName("test");
    createAndAddOpeningDay(openingDays, Weekdays.Day.MONDAY, isAllDay, isOpen, isExceptional, openingHours);
    openingPeriod.setOpeningDays(openingDays);
    return openingPeriod;
  }

  private void createAndAddOpeningDay(
    List<OpeningDayWeekDay> openingDays, Weekdays.Day day, Boolean isAllDay, boolean isOpen, boolean isExceptional, List<OpeningHour> openingHours) {
    OpeningDayWeekDay opening = new OpeningDayWeekDay();
    OpeningDay openingDay = new OpeningDay().withAllDay(isAllDay).withOpen(isOpen).withExceptional(isExceptional);
    if (!isExceptional) {
      opening.setWeekdays(new Weekdays().withDay(day));
    } else {
      openingDay.setAllDay(true);
    }
    openingDay.setOpeningHour(openingHours);
    opening.setOpeningDay(openingDay);
    openingDays.add(opening);
  }

  private List<OpeningHour> prepareOpeningHours(
    String startFirstPeriod, String endFirstPeriod, String startSecondPeriod, String endSecondPeriod) {

    List<OpeningHour> openingHours = new ArrayList<>();
    OpeningHour firstOpeningHour = new OpeningHour();
    firstOpeningHour.setStartTime(startFirstPeriod);
    firstOpeningHour.setEndTime(endFirstPeriod);
    openingHours.add(firstOpeningHour);
    OpeningHour secondOpeningHour = new OpeningHour();
    secondOpeningHour.setStartTime(startSecondPeriod);
    secondOpeningHour.setEndTime(endSecondPeriod);
    openingHours.add(secondOpeningHour);

    return openingHours;
  }

  private Calendar createStartDate(int startYear, int month, int day) {
    Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    startDate.clear();
    startDate.set(startYear, month, day, 0, 0, 0);
    return startDate;
  }

  private Calendar createEndDate(Calendar startDate, int numberOfDays) {
    Calendar endDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    endDate.setTime(startDate.getTime());
    int daysToAdd = 0;
    if (numberOfDays > 0) {
      daysToAdd = numberOfDays;
    }
    endDate.add(Calendar.DAY_OF_YEAR, daysToAdd);
    return endDate;
  }

  private Response getWithHeaderAndBody(String path) {
    return restGivenWithHeader()
      .get(path);
  }

  private Response postWithHeaderAndBody(OpeningPeriod opening, String path) {
    return restGivenWithHeader()
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .post(path);
  }

  private Response putWithHeaderAndBody(OpeningPeriod opening, String path) {
    return restGivenWithHeader()
      .header(JSON_CONTENT_TYPE_HEADER)
      .body(opening)
      .put(path);
  }

  private Response deleteWithHeaderAndBody(String path) {
    return restGivenWithHeader()
      .delete(path);
  }

  private RequestSpecification restGivenWithHeader() {
    return given()
      .header(TENANT_HEADER)
      .header(TOKEN_HEADER)
      .header(OKAPI_URL_HEADER)
      .header(JSON_CONTENT_TYPE_HEADER);
  }

  private void createExceptionalExistingPeriod(String servicePointUUID) {
    String uuidExPeriod = UUID.randomUUID().toString();
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod exceptionalPeriod = generateDescription(2020, Calendar.MARCH, 1,
      3, servicePointUUID, uuidExPeriod, true, false, true, openingHours);
    postPeriod(servicePointUUID, exceptionalPeriod);
  }

  private void createWorkingHoursExistingPeriod(String servicePointUUID) {
    String uuidExPeriod = UUID.randomUUID().toString();
    List<OpeningHour> openingHours = prepareOpeningHours("08:00", "12:00", "13:00", "17:00");
    OpeningPeriod openingPeriod = generateDescription(2020, Calendar.MARCH, 1,
      3, servicePointUUID, uuidExPeriod, true, false, false, openingHours);
    postPeriod(servicePointUUID, openingPeriod);
  }

  private String errIntervalsOverlapSchema(String errorMessage) {
    Error error = new Error()
      .withMessage(errorMessage)
      .withCode(ERROR_CODE_INTERVALS_OVERLAP);

    return JsonObject
      .mapFrom(new Errors().withErrors(Collections.singletonList(error)))
      .toString();
  }
}
