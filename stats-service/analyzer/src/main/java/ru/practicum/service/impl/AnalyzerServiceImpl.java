package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.mapper.AnalyzerMapper;
import ru.practicum.model.EventSimilarityScore;
import ru.practicum.repository.EventSimilarityScoreRepository;
import ru.practicum.service.AnalyzerService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyzerServiceImpl implements AnalyzerService {
    private final EventSimilarityScoreRepository eventSimilarityScoreRepository;

    @Override
    public void saveUpdate(EventSimilarityAvro eventSimilarityAvro) {
        EventSimilarityScore score = AnalyzerMapper.INSTANCE.toUserAction(eventSimilarityAvro);

        Optional<EventSimilarityScore> dbScore = eventSimilarityScoreRepository
                .findByEventAIdAndEventBId(score.getEventAId(), score.getEventBId());

        dbScore.ifPresent(eventSimilarityScore -> score.setId(eventSimilarityScore.getId()));

        eventSimilarityScoreRepository.save(score);
    }
}
