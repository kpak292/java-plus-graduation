package ru.practicum.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.service.AnalyzerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerListener {
    private final AnalyzerService analyzerService;

    @KafkaListener(topics = "${kafka.topic.similarity.v1}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenActions(EventSimilarityAvro eventSimilarityAvro) {
        log.info("Received event: {}", eventSimilarityAvro);

        analyzerService.saveUpdate(eventSimilarityAvro);
    }
}

