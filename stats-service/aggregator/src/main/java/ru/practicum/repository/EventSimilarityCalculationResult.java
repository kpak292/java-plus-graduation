package ru.practicum.repository;

import java.time.LocalDateTime;

public interface EventSimilarityCalculationResult {
    Long getEventAId();

    Long getEventBId();

    Double getSMin();

    Double getSA();

    Double getSB();

    LocalDateTime getTimestamp();
}

