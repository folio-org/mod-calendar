package org.folio.calendar.integration;

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
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.i18n.TranslationService;
import org.folio.calendar.testutils.MapperUtils;
import org.folio.calendar.testutils.WireMockInitializer;
import org.folio.calendar.utils.DateUtils;
import org.folio.spring.FolioModuleMetadata;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.tenant.domain.dto.TenantAttributes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base abstract class for testing APIs.  Contains centralized APIs for Rest Assured, database initialization, etc
 */
@Log4j2
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase(refresh = RefreshMode.NEVER)
@ContextConfiguration(initializers = { WireMockInitializer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiTest {

  public static final String TENANT_ID = "test";

  @Getter
  @Setter
  protected static boolean initialized = false;

  @Autowired
  protected WireMockServer wireMockServer;

  @Autowired
  protected FolioModuleMetadata metadata;

  @Autowired
  protected TranslationService translationService;

  @Value("${x-okapi-url}")
  protected String okapiUrl = null;

  @LocalServerPort
  protected Integer port;

  protected final OpenApiValidationFilter legacyValidationFilter = new OpenApiValidationFilter(
    "api/legacy/mod-calendar.yaml"
  );
  protected final OpenApiValidationFilter newValidationFilter = new OpenApiValidationFilter(
    "api/opening-hours.yaml"
  );

  @BeforeEach
  void clearCurrentDateOverride() {
    DateUtils.setCurrentDateOverride(null);
  }

  @BeforeEach
  void proxyLogTestStart(TestInfo testInfo) {
    if (System.getenv().getOrDefault("PROXY_ENABLE", "false").equals("true")) {
      String path = "/_/tests/";
      if (testInfo.getTestClass().isPresent()) {
        path += testInfo.getTestClass().get().getSimpleName() + "/";
      } else {
        path += "unknown/";
      }
      if (testInfo.getTestMethod().isPresent()) {
        path += testInfo.getTestMethod().get().getName();
      } else {
        path += "unknown";
      }
      ra(ValidationSchema.NONE).get(getRequestUrl(path));
    }
  }

  @AfterEach
  void proxyLogTestFinish() {
    if (System.getenv().getOrDefault("PROXY_ENABLE", "false").equals("true")) {
      ra(ValidationSchema.NONE).get(getRequestUrl("/_/tests/_/finish"));
    }
  }

  @BeforeAll
  static void initialize() {
    Locale.setDefault(Locale.US);

    log.info("Configuring JSON to parse decimals as doubles, not floats");
    // allow comparison with doubles, not floats
    JsonConfig jsonConfig = JsonConfig
      .jsonConfig()
      .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE);
    RestAssured.config = RestAssured.config().jsonConfig(jsonConfig);

    log.info("Configuring JSON date mapping");
    RestAssured.config =
      RestAssured
        .config()
        .objectMapperConfig(
          ObjectMapperConfig
            .objectMapperConfig()
            .jackson2ObjectMapperFactory((a, b) -> MapperUtils.MAPPER)
        );

    if (System.getenv().getOrDefault("PROXY_ENABLE", "false").equals("true")) {
      String host = System.getenv().getOrDefault("PROXY_HOST", "localhost");
      int port = Integer.parseInt(System.getenv().getOrDefault("PROXY_PORT", "8888"));
      String scheme = System.getenv().getOrDefault("PROXY_SCHEME", "http");

      log.info(String.format("Configuring proxy to %s://%s:%d", scheme, host, port));

      RestAssured.proxy = new ProxySpecification(host, port, scheme);
    }
  }

  @BeforeEach
  public void createDatabase() {
    if (!isInitialized()) {
      tenantInstall(new TenantAttributes().moduleTo("mod-calendar"));

      setInitialized(true);
    }
  }

  public void tenantInstall(TenantAttributes tenantAttributes) {
    // "/_/tenant" is not in Swagger schema, therefore, validation must be disabled
    // the v2.0 API of /_/tenant requires a non-empty moduleTo; without this, the module will not be initialized properly or enabled
    // the string we use does not matter (as there will be no modules in the database)
    log.info(String.format("Initializing database by posting to /_/tenant %s", tenantAttributes));
    ra(ValidationSchema.NONE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(tenantAttributes)
      .post(getRequestUrl("/_/tenant"))
      .then()
      .statusCode(both(greaterThanOrEqualTo(200)).and(lessThanOrEqualTo(299)));
  }

  @AfterEach
  void resetWiremock() {
    this.wireMockServer.resetAll();
  }

  /**
   * Create a RestAssured object with the proper headers for Okapi testing
   *
   * @param validate What schema the response must match
   * @return a @link {RequestSpecification} ready for .get/.post and other
   *         RestAssured library methods
   */
  public RequestSpecification ra(ValidationSchema validation) {
    RequestSpecification ra = RestAssured.given();
    switch (validation) {
      case LEGACY:
        ra = ra.filter(legacyValidationFilter);
        break;
      case OPENING_HOURS:
        ra = ra.filter(newValidationFilter);
        break;
      case NONE:
      default:
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
  public RequestSpecification ra() {
    return ra(ValidationSchema.LEGACY);
  }

  /**
   * Fully qualify a URL for testing. For example, if the path is "/test", this
   * method may return something like "http://localhost:8103/hello".
   *
   * @param path The API route's path
   * @return fully qualified URL
   */
  public String getRequestUrl(String path) {
    return String.format("http://localhost:%d%s", port, path);
  }
}
