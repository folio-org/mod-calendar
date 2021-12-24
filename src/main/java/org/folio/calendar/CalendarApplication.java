package org.folio.calendar;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main Spring Boot application class
 */
@SpringBootApplication
@EnableFeignClients
public class CalendarApplication {

  /**
   * Run the Spring Boot application
   *
   * @param args command line arguments
   */
  // @Generated needed to remove from code coverage tests
  @Generated
  public static void main(String[] args) {
    SpringApplication.run(CalendarApplication.class, args);
  }
}
