package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface AggregatorMapper {
    AggregatorMapper INSTANCE = Mappers.getMapper(AggregatorMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserAction toUserAction(UserActionAvro userActionAvro);

    default LocalDateTime map(Instant value) {
        return LocalDateTime.ofInstant(value, java.time.ZoneId.systemDefault());
    }

    default EventSimilarityAvro toEventSimilarityAvro(EventSimilarity eventSimilarity) {
        EventSimilarityAvro result = new EventSimilarityAvro();
        result.setEventAId(eventSimilarity.getEventAId());
        result.setEventBId(eventSimilarity.getEventBId());
        result.setTimestamp(eventSimilarity.getTimestamp().toInstant(ZoneOffset.UTC));

        double score = eventSimilarity.getSMin() / (Math.sqrt(eventSimilarity.getSA()) * Math.sqrt(eventSimilarity.getSB()));
        result.setScore(score);

        return result;
    }
}
