package org.folio.calendar.domain.mapper;

import org.folio.calendar.domain.dto.ExceptionalOpeningDTO;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExceptionHourMapper {
  ExceptionHourMapper INSTANCE = Mappers.getMapper(ExceptionHourMapper.class);

  @Mapping(target = "exceptionId", ignore = true)
  ExceptionalOpeningDTO toDto(ExceptionHour source);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exception", ignore = true)
  ExceptionHour fromDto(ExceptionalOpeningDTO source);
}
