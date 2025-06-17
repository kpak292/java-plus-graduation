package ru.practicum.dto;

import java.time.LocalDateTime;

public interface EventSimilarityCalculationResult {
    Long getEventAId();

    Long getEventBId();

    Double getSMin();

    Double getSA();

    Double getSB();

    LocalDateTime getTimestamp();
}

