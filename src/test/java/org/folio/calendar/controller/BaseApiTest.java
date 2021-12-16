package org.folio.calendar.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.calendar.testutils.APITestUtils.TENANT_ID;
import static org.hamcrest.Matchers.is;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.Header;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.specification.RequestSpecification;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.testutils.WireMockInitializer;
import org.folio.spring.FolioModuleMetadata;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.tenant.domain.dto.TenantAttributes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Log4j2
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase
@ContextConfiguration(initializers = { WireMockInitializer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseApiTest {

  @Getter
  @Setter
  protected static boolean initialized = false;

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
    "swagger.api/mod-calendar.yaml"
  );

  @BeforeEach
  void beforeEach() {
    // workaround for JUnit 5 as each test is idempotent
    if (!isInitialized()) {
      log.info("Initializing database by posting to /_/tenant");

      ra(false) // "/_/tenant" is not in Swagger schema, therefore, validation must be disabled
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(new TenantAttributes().moduleTo(""))
        .post(getRequestUrl("/_/tenant"))
        .then()
        .statusCode(is(HttpStatus.OK.value()));

      log.info("Configuring JSON to parse decimals as doubles, not floats");
      // allow comparison with doubles, not floats
      JsonConfig jsonConfig = JsonConfig
        .jsonConfig()
        .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE);
      RestAssured.config = RestAssured.config().jsonConfig(jsonConfig);

      setInitialized(true);
    }
  }

  @AfterEach
  void afterEach() {
    this.wireMockServer.resetAll();
  }

  @Test
  void testContextLoads() {
    assertThat(metadata)
      .as("Ensure application context starts and database can be initialized")
      .isNotNull();
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
