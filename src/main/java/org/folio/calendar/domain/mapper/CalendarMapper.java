package org.folio.calendar.domain.mapper;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.CalendarDTO.CalendarDTOBuilder;
import org.folio.calendar.domain.dto.ExceptionRangeDTO;
import org.folio.calendar.domain.dto.MetadataDTO;
import org.folio.calendar.domain.dto.MetadataDTO.MetadataDTOBuilder;
import org.folio.calendar.domain.dto.NormalHoursDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.Calendar.CalendarBuilder;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.mapstruct.Mapper;
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

    if (source.getMetadata() != null) {
      calendar.createdDate(source.getMetadata().getCreatedDate());
      calendar.createdByUserId(source.getMetadata().getCreatedByUserId());
      calendar.updatedDate(source.getMetadata().getUpdatedDate());
      calendar.updatedByUserId(source.getMetadata().getUpdatedByUserId());
    }

    Calendar result = calendar.build();
    result.propagate();
    return result;
  }

  default CalendarDTO toDto(Calendar source) {
    if (source == null) {
      return null;
    }

    CalendarDTOBuilder calendar = CalendarDTO.builder();

    calendar.assignments(
      servicePointCalendarAssignmentCollectionToUuidList(source.getServicePoints())
    );
    calendar.id(source.getId());
    calendar.name(source.getName());
    calendar.startDate(source.getStartDate());
    calendar.endDate(source.getEndDate());
    calendar.normalHours(normalOpeningCollectionToNormalHoursDTOList(source.getNormalHours()));
    calendar.exceptions(exceptionRangeCollectionToExceptionRangeDTOList(source.getExceptions()));

    MetadataDTOBuilder metadata = MetadataDTO.builder();
    metadata.createdDate(source.getCreatedDate());
    metadata.createdByUserId(source.getCreatedByUserId());
    metadata.updatedDate(source.getUpdatedDate());
    metadata.updatedByUserId(source.getUpdatedByUserId());
    calendar.metadata(metadata.build());

    return calendar.build();
  }

  Collection<ServicePointCalendarAssignment> uuidListToServicePointCalendarAssignmentCollection(
    List<UUID> source
  );
  List<UUID> servicePointCalendarAssignmentCollectionToUuidList(
    Collection<ServicePointCalendarAssignment> source
  );
  Collection<NormalOpening> normalHoursDTOListToNormalOpeningCollection(
    List<NormalHoursDTO> source
  );
  List<NormalHoursDTO> normalOpeningCollectionToNormalHoursDTOList(
    Collection<NormalOpening> source
  );
  Collection<ExceptionRange> exceptionRangeDTOListToExceptionRangeCollection(
    List<ExceptionRangeDTO> source
  );
  List<ExceptionRangeDTO> exceptionRangeCollectionToExceptionRangeDTOList(
    Collection<ExceptionRange> source
  );
}
