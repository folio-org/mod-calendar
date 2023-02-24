package org.folio.calendar.domain.mapper;

import org.folio.calendar.domain.dto.NormalHoursDTO;
import org.folio.calendar.domain.entity.NormalOpening;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NormalOpeningMapper {
  NormalOpeningMapper INSTANCE = Mappers.getMapper(NormalOpeningMapper.class);

  @Mapping(target = "calendarId", ignore = true)
  NormalHoursDTO toDto(NormalOpening source);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "calendar", ignore = true)
  NormalOpening fromDto(NormalHoursDTO source);
}
