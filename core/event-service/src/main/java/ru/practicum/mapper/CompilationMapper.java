package ru.practicum.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    CompilationDto getCompilationDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    Compilation getCompilation(NewCompilationDto newCompilationDto);

    void update(@MappingTarget Compilation compilation, UpdateCompilationRequest updateCompilationRequest);

    @Mapping(target = "id", source = "id")
    Event getEventFromLong(Long id);
}
