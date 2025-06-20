package ru.practicum.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.AggregatorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationListener {
    private final AggregatorService aggregatorService;

    @KafkaListener(topics = "${kafka.topic.stats.v1}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenActions(UserActionAvro event) {
        log.info("Received event: {}", event);
        aggregatorService.saveUpdate(event);
    }
}

