package org.folio.calendar.integration;

import static org.folio.calendar.testutils.APITestUtils.TENANT_ID;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.http.Header;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.specification.RequestSpecification;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.testutils.APITestUtils;
import org.folio.calendar.testutils.WireMockInitializer;
import org.folio.spring.FolioModuleMetadata;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.tenant.domain.dto.TenantAttributes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base abstract class for testing APIs.  Contains centralized APIs for Rest Assured, database initialization, etc
 */
@Log4j2
@ActiveProfiles("test")
@ExtendWith(ApiTestWatcher.class)
@AutoConfigureEmbeddedDatabase(refresh = RefreshMode.NEVER)
@ContextConfiguration(initializers = { WireMockInitializer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiTest {

  @Getter
  @Setter
  protected static boolean initialized = false;

  @Getter
  @Setter
  protected static boolean dbInitialized = false;

  @Autowired
  protected WireMockServer wireMockServer;

  @Autowired
  protected FolioModuleMetadata metadata;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Value("${x-okapi-url}")
  protected String okapiUrl = null;

  @LocalServerPort
  protected Integer port;

  protected final OpenApiValidationFilter validationFilter = new OpenApiValidationFilter(
    "api/mod-calendar.yaml"
  );

  @BeforeEach
  void createDatabase(TestInfo testInfo) {
    if (!testInfo.getTags().contains(DatabaseUsage.NONE.value) && !isDbInitialized()) {
      log.info("Initializing database by posting to /_/tenant");
      ra(false) // "/_/tenant" is not in Swagger schema, therefore, validation must be disabled
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(new TenantAttributes().moduleTo(""))
        .post(getRequestUrl("/_/tenant"))
        .then()
        .statusCode(both(greaterThanOrEqualTo(200)).and(lessThanOrEqualTo(299)));

      setDbInitialized(true);
    }
  }

  @BeforeEach
  void addJsonConfig() {
    // workaround for JUnit 5 as each test is idempotent (no @Before) but we only need to do this once
    if (!isInitialized()) {
      log.info("Configuring JSON to parse decimals as doubles, not floats");
      // allow comparison with doubles, not floats
      JsonConfig jsonConfig = JsonConfig
        .jsonConfig()
        .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE);
      RestAssured.config = RestAssured.config().jsonConfig(jsonConfig);

      RestAssured.config =
        RestAssured
          .config()
          .objectMapperConfig(
            ObjectMapperConfig
              .objectMapperConfig()
              .jackson2ObjectMapperFactory((a, b) -> APITestUtils.MAPPER)
          );

      setInitialized(true);
    }
  }

  @AfterEach
  void cleanDatabase(TestInfo testInfo) {
    if (
      testInfo.getTags().contains(DatabaseUsage.NONE.value) ||
      testInfo.getTags().contains(DatabaseUsage.IDEMPOTENT.value)
    ) {
      return;
    }
    log.info("Recreating database");

    log.info("Deleting database by posting to /_/tenant");
    ra(false) // "/_/tenant" is not in Swagger schema, therefore, validation must be disabled
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(new TenantAttributes().moduleTo(""))
      .delete(getRequestUrl("/_/tenant"))
      .then()
      .statusCode(both(greaterThanOrEqualTo(200)).and(lessThanOrEqualTo(299)));

    setDbInitialized(false);
  }

  @AfterEach
  void resetWiremock() {
    this.wireMockServer.resetAll();
  }

  /**
   * Create a RestAssured object with the proper headers for Okapi testing
   *
   * @param validate Whether or not the request/response must match the schema
   * @return a @link {RequestSpecification} ready for .get/.post and other
   *         RestAssured library methods
   */
  protected RequestSpecification ra(boolean validate) {
    RequestSpecification ra = RestAssured.given();
    if (validate) {
      ra = ra.filter(validate ? validationFilter : null);
    }
    return ra
      .header(new Header(XOkapiHeaders.URL, okapiUrl))
      .header(new Header(XOkapiHeaders.TENANT, TENANT_ID));
  }

  /**
   * Create a RestAssured object with the proper headers for Okapi testing and
   * builtin schema validation
   *
   * @return a {@link RequestSpecification} ready for .get/.post and other
   *         RestAssured library methods
   */
  protected RequestSpecification ra() {
    return ra(true);
  }

  /**
   * Fully qualify a URL for testing. For example, if the path is "/test", this
   * method may return something like "http://localhost:8103/hello".
   *
   * @param path The API route's path
   * @return fully qualified URL
   */
  protected String getRequestUrl(String path) {
    return String.format("http://localhost:%d%s", port, path);
  }
}
