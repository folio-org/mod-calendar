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
import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import java.util.*;

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

    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/calculateopening")
      .then()
      .statusCode(400);

    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/calculateopening?startDate=test&unit=day&amount=22")
      .then()
      .statusCode(400);

    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/calculateopening?startDate=2018-10-12&unit=day")
      .then()
      .statusCode(400);

    getWithHeaderAndBody("/calendar/periods/" + UUID.randomUUID().toString() + "/calculateopening?startDate=2018-10-12&unit=day&amount=-1")
      .then()
      .statusCode(400);  }

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

  @Test
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

  @Test
  public void calculateOpeningsWithSingleOpeningTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescription(2018, Calendar.NOVEMBER, 20, 60, servicePointUUID, uuid, false, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/calculateopening?startDate=2118-11-20&unit=day&amount=10")
      .then()
      .statusCode(404);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/calculateopening?startDate=2018-11-20&unit=day&amount=10")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("servicePointId", equalTo(servicePointUUID))
      .body("name", equalTo("test"))
      .body("startDate", equalTo("2018-11-20T00:00:00.000+0000"))
      .body("endDate", equalTo("2019-01-19T00:00:00.000+0000"))
      .body("openingDays[0].openingDay.date", equalTo("2018-11-26Z"))
      .body("openingDays[0].openingDay.open", equalTo(true))
      .body("openingDays[0].weekdays.day", equalTo("MONDAY"))
      .body("openingDays[1].openingDay.date", equalTo("2018-11-30Z"))
      .body("openingDays[1].openingDay.open", equalTo(false))
      .body("openingDays[1].weekdays.day", equalTo("FRIDAY"))
      .body("openingDays[2].openingDay.date", equalTo("2018-12-03Z"))
      .body("openingDays[2].openingDay.open", equalTo(true))
      .body("openingDays[2].weekdays.day", equalTo("MONDAY"))
      .statusCode(200);

    //previous day is out of schedule == closed
    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/calculateopening?startDate=2018-11-20&unit=hour&amount=1")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("servicePointId", equalTo(servicePointUUID))
      .body("name", equalTo("test"))
      .body("startDate", equalTo("2018-11-20T00:00:00.000+0000"))
      .body("endDate", equalTo("2019-01-19T00:00:00.000+0000"))
      .body("openingDays[0].openingDay.date", equalTo("2018-11-19Z"))
      .body("openingDays[0].openingDay.open", equalTo(false))
      .body("openingDays[0].weekdays.day", equalTo("MONDAY"))
      .body("openingDays[1].openingDay.date", equalTo("2018-11-20Z"))
      .body("openingDays[1].openingDay.open", equalTo(false))
      .body("openingDays[1].weekdays.day", equalTo("TUESDAY"))
      .body("openingDays[2].openingDay.date", equalTo("2018-11-26Z"))
      .body("openingDays[2].openingDay.open", equalTo(true))
      .body("openingDays[2].weekdays.day", equalTo("MONDAY"))
      .statusCode(200);

    //next day is out of schedule == closed
    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/calculateopening?startDate=2019-01-18&unit=hour&amount=1")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("servicePointId", equalTo(servicePointUUID))
      .body("name", equalTo("test"))
      .body("startDate", equalTo("2018-11-20T00:00:00.000+0000"))
      .body("endDate", equalTo("2019-01-19T00:00:00.000+0000"))
      .body("openingDays[0].openingDay.date", equalTo("2019-01-14Z"))
      .body("openingDays[0].openingDay.open", equalTo(true))
      .body("openingDays[0].weekdays.day", equalTo("MONDAY"))
      .body("openingDays[1].openingDay.date", equalTo("2019-01-18Z"))
      .body("openingDays[1].openingDay.open", equalTo(false))
      .body("openingDays[1].weekdays.day", equalTo("FRIDAY"))
      .body("openingDays[2].openingDay.date", equalTo("2019-01-21Z"))
      .body("openingDays[2].openingDay.open", equalTo(false))
      .body("openingDays[2].weekdays.day", equalTo("MONDAY"))
      .statusCode(200);
  }

  @Test
  public void calculateOpeningsWithMultipleOpeningsTest() {
    String uuid = UUID.randomUUID().toString();
    String servicePointUUID = UUID.randomUUID().toString();
    OpeningPeriod_ opening = generateDescriptionWithMultipleDays(2019, Calendar.MAY, 1, 31, servicePointUUID, uuid, false, true, false);

    postPeriod(servicePointUUID, opening);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/calculateopening?startDate=2019-05-01&unit=day&amount=10")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("servicePointId", equalTo(servicePointUUID))
      .body("name", equalTo("test"))
      .body("startDate", equalTo("2019-05-01T00:00:00.000+0000"))
      .body("endDate", equalTo("2019-06-01T00:00:00.000+0000"))
      .body("openingDays[0].openingDay.date", equalTo("2019-05-10Z"))
      .body("openingDays[0].openingDay.open", equalTo(true))
      .body("openingDays[0].weekdays.day", equalTo("FRIDAY"))
      .body("openingDays[1].openingDay.date", equalTo("2019-05-11Z"))
      .body("openingDays[1].openingDay.open", equalTo(false))
      .body("openingDays[1].weekdays.day", equalTo("SATURDAY"))
      .body("openingDays[2].openingDay.date", equalTo("2019-05-13Z"))
      .body("openingDays[2].openingDay.open", equalTo(true))
      .body("openingDays[2].weekdays.day", equalTo("MONDAY"))
      .statusCode(200);

    getWithHeaderAndBody("/calendar/periods/" + servicePointUUID + "/calculateopening?startDate=2019-05-01&unit=day&amount=9")
      .then()
      .contentType(ContentType.JSON)
      .body(matchesJsonSchemaInClasspath("ramls/schemas/Opening.json"))
      .body("id", equalTo(uuid))
      .body("servicePointId", equalTo(servicePointUUID))
      .body("name", equalTo("test"))
      .body("startDate", equalTo("2019-05-01T00:00:00.000+0000"))
      .body("endDate", equalTo("2019-06-01T00:00:00.000+0000"))
      .body("openingDays[0].openingDay.date", equalTo("2019-05-08Z"))
      .body("openingDays[0].openingDay.open", equalTo(true))
      .body("openingDays[0].weekdays.day", equalTo("WEDNESDAY"))
      .body("openingDays[1].openingDay.date", equalTo("2019-05-10Z"))
      .body("openingDays[1].openingDay.open", equalTo(true))
      .body("openingDays[1].weekdays.day", equalTo("FRIDAY"))
      .body("openingDays[2].openingDay.date", equalTo("2019-05-13Z"))
      .body("openingDays[2].openingDay.open", equalTo(true))
      .body("openingDays[2].weekdays.day", equalTo("MONDAY"))
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
    createAndAddOpeningDay(openingDays, Weekdays.Day.MONDAY, isAllDay, isOpen, isExceptional);
    openingPeriod.setOpeningDays(openingDays);
    return openingPeriod;
  }

  private OpeningPeriod_ generateDescriptionWithMultipleDays(int startYear, int month, int day, int numberOfDays, String servicePointId, String uuid, boolean isAllDay, boolean isOpen, boolean isExceptional) {
    List<OpeningDay_> openingDays = new ArrayList<>();
    Calendar startDate = createStartDate(startYear, month, day);
    Calendar endDate = createEndDate(startDate, numberOfDays);
    OpeningPeriod_ openingPeriod = new OpeningPeriod_();
    openingPeriod.setId(uuid);
    openingPeriod.setStartDate(startDate.getTime());
    openingPeriod.setEndDate(endDate.getTime());
    openingPeriod.setServicePointId(servicePointId);
    openingPeriod.setName("test");

    createAndAddOpeningDay(openingDays, Weekdays.Day.MONDAY, isAllDay, isOpen, isExceptional);
    createAndAddOpeningDay(openingDays, Weekdays.Day.WEDNESDAY, isAllDay, isOpen, isExceptional);
    createAndAddOpeningDay(openingDays, Weekdays.Day.FRIDAY, isAllDay, isOpen, isExceptional);

    openingPeriod.setOpeningDays(openingDays);
    return openingPeriod;
  }

  private void createAndAddOpeningDay(List<OpeningDay_> openingDays, Weekdays.Day day, boolean isAllDay, boolean isOpen, boolean isExceptional) {
    OpeningDay_ opening = new OpeningDay_();
    OpeningDay openingDay = new OpeningDay().withAllDay(isAllDay).withOpen(isOpen).withExceptional(isExceptional);
    List<OpeningHour> openingHours = new ArrayList<>();
    if (!isExceptional) {
      opening.setWeekdays(new Weekdays().withDay(day));
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
      .header(OKAPI_URL_HEADER)
      .header(JSON_CONTENT_TYPE_HEADER);
  }
}
