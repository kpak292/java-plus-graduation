package ru.practicum.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface AnalyzerService {
    void saveUpdate(EventSimilarityAvro eventSimilarityAvro);
}
