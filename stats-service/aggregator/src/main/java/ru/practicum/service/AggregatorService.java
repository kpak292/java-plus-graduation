package ru.practicum.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface AggregatorService {
    void saveUpdate(UserActionAvro userActionAvro);
}
