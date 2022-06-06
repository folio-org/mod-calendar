package org.folio.calendar.unit.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.repository.CustomOffsetPageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class CustomOffsetPageRequestTest {

  @Test
  void testGetters() {
    CustomOffsetPageRequest sample = new CustomOffsetPageRequest(32, 20);

    assertThat(sample.getOffset(), is(32L));
    assertThat(sample.getLimit(), is(20));
    assertThat(sample.getPageSize(), is(20));
    assertThat(sample.getPageNumber(), is(1)); // between page 1 and 2, truncated down
    assertThat(sample.getSort(), is(Sort.unsorted()));
  }

  @Test
  void testModifiers() {
    CustomOffsetPageRequest sample = new CustomOffsetPageRequest(32, 20);

    assertThat(sample.next(), is(equalTo(new CustomOffsetPageRequest(52, 20))));
    assertThat(sample.previousOrFirst(), is(equalTo(new CustomOffsetPageRequest(12, 20))));

    // never goes less than zero offset
    assertThat(
      sample.previousOrFirst().previousOrFirst(),
      is(equalTo(new CustomOffsetPageRequest(0, 20)))
    );
    assertThat(
      sample.previousOrFirst().previousOrFirst().previousOrFirst(),
      is(equalTo(new CustomOffsetPageRequest(0, 20)))
    );

    assertThat(sample.first(), is(equalTo(new CustomOffsetPageRequest(0, 20))));
    assertThat(sample.withPage(0), is(equalTo(new CustomOffsetPageRequest(0, 20))));
    assertThat(sample.withPage(10), is(equalTo(new CustomOffsetPageRequest(200, 20))));
    assertThat(sample.hasPrevious(), is(true));
    assertThat(sample.withOffset(0).hasPrevious(), is(false));
  }
}
