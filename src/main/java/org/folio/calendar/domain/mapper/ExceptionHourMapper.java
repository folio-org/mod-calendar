package org.folio.calendar.domain.mapper;

import org.folio.calendar.domain.dto.ExceptionalOpeningDTO;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExceptionHourMapper {
  ExceptionHourMapper INSTANCE = Mappers.getMapper(ExceptionHourMapper.class);

  ExceptionalOpeningDTO toDto(ExceptionHour source);
  ExceptionHour fromDto(ExceptionalOpeningDTO source);
}
