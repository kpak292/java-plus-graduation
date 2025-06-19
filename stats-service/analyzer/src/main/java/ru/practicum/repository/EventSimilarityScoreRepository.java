package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EventSimilarityScore;

import java.util.Optional;

public interface EventSimilarityScoreRepository extends JpaRepository<EventSimilarityScore, Long> {
    Optional<EventSimilarityScore> findByEventAIdAndEventBId(Long eventAId, Long eventBId);
}
