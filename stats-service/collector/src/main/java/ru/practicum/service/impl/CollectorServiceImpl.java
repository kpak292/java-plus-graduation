package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.event.UserActionProto;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.service.CollectorService;

@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final KafkaTemplate<String, UserActionAvro> kafkaTemplate;

    @Value("${collector.topic.stats.v1}")
    private String USER_ACTION_TOPIC;

    @Override
    public void createUserAction(UserActionProto request) {
        kafkaTemplate.send(USER_ACTION_TOPIC, UserActionMapper.INSTANCE.toUserActionAvro(request));
    }
}
