package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.config.RateConfig;
import ru.practicum.dto.EventSimilarityCalculationResult;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.mapper.AggregatorMapper;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.service.SimilarityService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimilarityServiceImpl implements SimilarityService {
    private final RateConfig rateConfig;
    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Value("${kafka.topic.similarity.v1}")
    private String similarityTopic;

    @Override
    public double getRate(ActionTypeAvro actionType) {
        return rateConfig.getRates().get(actionType);
    }

    @Override
    public void sendUpdate(Long userId, Long eventId, Double rateDelta) {
        List<EventSimilarity> updates;

        //STEP 1
        //GET all comparisons with eventId
        List<EventSimilarity> similarities = eventSimilarityRepository.findAllSimilaritiesByEventId(eventId);

        //STEP 2
        //Check if there are no similarities for this eventId
        if (similarities.isEmpty()) {
            updates = createSimilarity(eventId); //Create and calculate all potential similarities
        } else {
            //update similarities calculation
            updates = updateSimilarities(similarities);
        }

        //STEP 3 save a list to DB and send to Kafka
        sendUpdates(updates);
    }

    //Create new similarities for a new event
    private List<EventSimilarity> createSimilarity(Long eventId) {
        //Get all ids not equal to this event
        List<Long> events = eventSimilarityRepository.findAllEventIds(eventId);

        //If an empty list does nothing
        if (events.isEmpty()) {
            return List.of();
        }

        List<EventSimilarity> newSimilarities = events.stream()
                .map(event -> {
                    EventSimilarity similarity = new EventSimilarity();
                    if (event > eventId) {
                        similarity.setEventAId(eventId);
                        similarity.setEventBId(event);
                    } else {
                        similarity.setEventAId(event);
                        similarity.setEventBId(eventId);
                    }

                    return similarity;
                })
                .toList();

        return updateSimilarities(newSimilarities);
    }

    //Process List of similarities
    private List<EventSimilarity> updateSimilarities(List<EventSimilarity> similarities) {
        return similarities.stream()
                .map(this::calculateSimilarity)
                .toList();
    }

    //Recalculate similarity
    private EventSimilarity calculateSimilarity(EventSimilarity eventSimilarity) {
        EventSimilarityCalculationResult calculated = eventSimilarityRepository.calculateSimilarity(eventSimilarity.getEventAId(),
                eventSimilarity.getEventBId());

        eventSimilarity.setSMin(calculated.getSMin());
        eventSimilarity.setSA(calculated.getSA());
        eventSimilarity.setSB(calculated.getSB());
        eventSimilarity.setTimestamp(calculated.getTimestamp());
        return eventSimilarity;
    }

    //Send updates to Kafka
    private void sendUpdates(List<EventSimilarity> updates) {
        eventSimilarityRepository.saveAll(updates);

        updates.forEach(update -> {
            kafkaTemplate.send(similarityTopic, AggregatorMapper.INSTANCE.toEventSimilarityAvro(update));
        });
    }
}
