package org.folio.calendar.repository;

import java.util.UUID;
import org.folio.calendar.domain.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository
  extends JpaRepository<Calendar, UUID>, JpaSpecificationExecutor<Calendar> {}
