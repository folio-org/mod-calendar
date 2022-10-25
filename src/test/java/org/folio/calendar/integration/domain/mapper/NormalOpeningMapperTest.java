package org.folio.calendar.integration.domain.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.mapper.NormalOpeningMapper;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.testconstants.NormalOpenings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NormalOpeningMapperTest extends BaseApiTest {

  @Autowired
  NormalOpeningMapper mapper;

  @Test
  void testMapper() {
    assertThat(
      mapper.fromDto(mapper.toDto(NormalOpenings.MONDAY_00_00_TO_12_30)),
      is(equalTo(NormalOpenings.MONDAY_00_00_TO_12_30))
    );
    assertThat(
      mapper.fromDto(mapper.toDto(NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59)),
      is(equalTo(NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59))
    );
  }
}
