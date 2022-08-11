package org.folio.calendar.repository;

import lombok.Data;
import lombok.With;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Custom {@link Pageable Pageable} to allow partial pages (e.g. offset 15 with a limit of 10).
 * Stock {@code Pageable} implementations required full pages (e.g. only offsets 10 and 20).
 */
@Data
@With
public class CustomOffsetPageRequest implements Pageable {

  protected long offset;
  protected int limit;

  public CustomOffsetPageRequest(long offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  @Override
  public int getPageNumber() {
    return ((int) this.getOffset()) / this.getLimit();
  }

  @Override
  public int getPageSize() {
    return this.getLimit();
  }

  @Override
  public Sort getSort() {
    return Sort.unsorted();
  }

  @Override
  public Pageable next() {
    return this.withOffset(this.getOffset() + this.getLimit());
  }

  @Override
  public Pageable previousOrFirst() {
    return this.withOffset(Math.max(this.getOffset() - this.getLimit(), 0));
  }

  @Override
  public Pageable first() {
    return this.withOffset(0);
  }

  @Override
  public Pageable withPage(int pageNumber) {
    return this.withOffset(pageNumber * this.getLimit());
  }

  @Override
  public boolean hasPrevious() {
    return this.offset != 0L;
  }
}
