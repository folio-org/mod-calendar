package org.folio.calendar.domain.mapper;

import java.util.Collection;
import java.util.List;
import org.folio.calendar.domain.dto.ExceptionRangeDTO;
import org.folio.calendar.domain.dto.ExceptionalOpeningDTO;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.ExceptionRange.ExceptionRangeBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

// This class directly relates to ExceptionRange and, due to MapStruct's automatic implementation
// generation, does not need to be prefixed with abstract
@SuppressWarnings("java:S118")
@Mapper(componentModel = "spring", uses = { ExceptionHourMapper.class })
public interface ExceptionRangeMapper {
  ExceptionRangeMapper INSTANCE = Mappers.getMapper(ExceptionRangeMapper.class);

  ExceptionRangeDTO toDto(ExceptionRange source);

  default ExceptionRange fromDto(ExceptionRangeDTO source) {
    if (source == null) {
      return null;
    }

    ExceptionRangeBuilder exceptionRange = ExceptionRange.builder();

    exceptionRange.id(source.getId());
    exceptionRange.name(source.getName());
    exceptionRange.startDate(source.getStartDate());
    exceptionRange.endDate(source.getEndDate());
    exceptionRange.openings(
      exceptionalOpeningDTOListToExceptionHourCollection(source.getOpenings())
    );

    ExceptionRange range = exceptionRange.build();
    range.propagate();
    return range;
  }

  Collection<ExceptionHour> exceptionalOpeningDTOListToExceptionHourCollection(
    List<ExceptionalOpeningDTO> source
  );
}
