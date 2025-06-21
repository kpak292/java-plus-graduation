package ru.practicum.service;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;

public interface SimilarityService {
    double getRate(ActionTypeAvro actionType);

    void sendUpdate(Long userId, Long eventId, Double rateDelta);

}
