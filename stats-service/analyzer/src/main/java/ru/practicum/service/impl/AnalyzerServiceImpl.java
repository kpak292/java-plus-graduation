package ru.practicum.service.impl;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.grpc.stats.event.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.event.RecommendedEventProto;
import ru.practicum.grpc.stats.event.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.event.UserPredictionsRequestProto;
import ru.practicum.mapper.AnalyzerMapper;
import ru.practicum.model.EventSimilarityScore;
import ru.practicum.repository.EventSimilarityScoreRepository;
import ru.practicum.repository.RecommendedEvent;
import ru.practicum.service.AnalyzerService;

import java.util.List;
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

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEvent> list = eventSimilarityScoreRepository.findRecommendationsForUser(request.getUserId(),
                request.getMaxResults());

        processResponse(list.stream()
                .map(AnalyzerMapper.INSTANCE::toRecommendedEventProto)
                .toList(), responseObserver);
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEvent> list = eventSimilarityScoreRepository.findSimilarEvents(request.getUserId(),
                request.getEventId(), request.getMaxResults());

        processResponse(list.stream()
                .map(AnalyzerMapper.INSTANCE::toRecommendedEventProto)
                .toList(), responseObserver);
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEvent> list = eventSimilarityScoreRepository.findInteractionsCount(request.getEventId());

        processResponse(list.stream()
                .map(AnalyzerMapper.INSTANCE::toRecommendedEventProto)
                .toList(), responseObserver);
    }

    private void processResponse(List<RecommendedEventProto> response, StreamObserver<RecommendedEventProto> responseObserver) {
        response.forEach(responseObserver::onNext);
    }
}
