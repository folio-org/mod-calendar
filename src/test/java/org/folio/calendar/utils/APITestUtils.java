package org.folio.calendar.utils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import com.github.tomakehurst.wiremock.junit.Stubbing;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@UtilityClass
public class APITestUtils {

  public static final String TENANT_ID = "test";

  public static void mockGet(
    String url,
    String body,
    int status,
    String contentType,
    Stubbing mockServer
  ) {
    mockServer.stubFor(
      get(urlMatching(url))
        .willReturn(
          aResponse()
            .withBody(body)
            .withHeader(HttpHeaders.CONTENT_TYPE, contentType)
            .withStatus(status)
        )
    );
  }

  public static void mockPost(String url, String body, int status, Stubbing mockServer) {
    mockServer.stubFor(
      post(urlMatching(url))
        .willReturn(
          aResponse()
            .withBody(body)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withStatus(status)
        )
    );
  }
}
