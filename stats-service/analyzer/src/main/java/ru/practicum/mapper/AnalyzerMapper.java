package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.grpc.stats.event.RecommendedEventProto;
import ru.practicum.model.EventSimilarityScore;
import ru.practicum.repository.RecommendedEvent;

import java.time.Instant;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface AnalyzerMapper {
    AnalyzerMapper INSTANCE = Mappers.getMapper(AnalyzerMapper.class);

    @Mapping(target = "id", ignore = true)
    EventSimilarityScore toUserAction(EventSimilarityAvro eventSimilarityAvro);

    default LocalDateTime map(Instant value) {
        return LocalDateTime.ofInstant(value, java.time.ZoneId.systemDefault());
    }

    RecommendedEventProto toRecommendedEventProto(RecommendedEvent recommendedEvent);
}
