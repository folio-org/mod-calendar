package org.folio.calendar.domain.mapper;

import org.folio.calendar.domain.dto.NormalHoursDTO;
import org.folio.calendar.domain.entity.NormalOpening;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NormalOpeningMapper {
  NormalOpeningMapper INSTANCE = Mappers.getMapper(NormalOpeningMapper.class);

  NormalHoursDTO toDto(NormalOpening source);
  NormalOpening fromDto(NormalHoursDTO source);
}
