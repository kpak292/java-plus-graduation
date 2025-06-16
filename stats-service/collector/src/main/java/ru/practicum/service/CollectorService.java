package ru.practicum.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.event.UserActionProto;

public interface CollectorService {
    void createUserAction(UserActionProto request);
}
