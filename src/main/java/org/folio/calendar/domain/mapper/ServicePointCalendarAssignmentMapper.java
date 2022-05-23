package org.folio.calendar.domain.mapper;

import java.util.UUID;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServicePointCalendarAssignmentMapper {
  ServicePointCalendarAssignmentMapper INSTANCE = Mappers.getMapper(
    ServicePointCalendarAssignmentMapper.class
  );

  default ServicePointCalendarAssignment fromUuid(UUID servicePointId) {
    return ServicePointCalendarAssignment.builder().servicePointId(servicePointId).build();
  }

  default UUID toUuid(ServicePointCalendarAssignment assignment) {
    return assignment.getServicePointId();
  }
}
