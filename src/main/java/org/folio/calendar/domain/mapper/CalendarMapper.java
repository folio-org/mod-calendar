package org.folio.calendar.domain.mapper;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.ExceptionRangeDTO;
import org.folio.calendar.domain.dto.NormalHoursDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.Calendar.CalendarBuilder;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

// This class directly relates to Calendar and, due to MapStruct's automatic implementation
// generation, does not need to be prefixed with abstract
@SuppressWarnings("java:S118")
@Mapper(
  componentModel = "spring",
  uses = {
    ExceptionRangeMapper.class,
    NormalOpeningMapper.class,
    ServicePointCalendarAssignmentMapper.class,
  }
)
public interface CalendarMapper {
  CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);

  @Mapping(source = "servicePoints", target = "assignments")
  CalendarDTO toDto(Calendar source);

  default Calendar fromDto(CalendarDTO source) {
    if (source == null) {
      return null;
    }

    CalendarBuilder calendar = Calendar.builder();

    calendar.servicePoints(
      uuidListToServicePointCalendarAssignmentCollection(source.getAssignments())
    );
    calendar.id(source.getId());
    calendar.name(source.getName());
    calendar.startDate(source.getStartDate());
    calendar.endDate(source.getEndDate());
    calendar.normalHours(normalHoursDTOListToNormalOpeningCollection(source.getNormalHours()));
    calendar.exceptions(exceptionRangeDTOListToExceptionRangeCollection(source.getExceptions()));

    Calendar result = calendar.build();
    result.propagate();
    return result;
  }

  Collection<ServicePointCalendarAssignment> uuidListToServicePointCalendarAssignmentCollection(
    List<UUID> source
  );
  Collection<NormalOpening> normalHoursDTOListToNormalOpeningCollection(
    List<NormalHoursDTO> source
  );
  Collection<ExceptionRange> exceptionRangeDTOListToExceptionRangeCollection(
    List<ExceptionRangeDTO> source
  );
}
