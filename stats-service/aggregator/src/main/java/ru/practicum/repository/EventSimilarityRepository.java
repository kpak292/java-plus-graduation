package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.EventSimilarityCalculationResult;
import ru.practicum.model.EventSimilarity;

import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    @Query("""
            SELECT es
            FROM EventSimilarity as es
            WHERE es.eventAId = :eventId OR es.eventBId = :eventId
            """)
    List<EventSimilarity> findAllSimilaritiesByEventId(Long eventId);

    @Query(value = """
            WITH sum_min as (
                        SELECT MIN(ua.score) mins
                        FROM aggregator_service.user_action ua
                        WHERE ua.event_id = :eventAId OR ua.event_id = :eventBId
                        GROUP BY ua.user_id)
            SELECT :eventAId as eventAId,
                    :eventBId as eventBId,
                    COALESCE(SUM(sm.mins), 0.0) as sMin,
                    COALESCE((SELECT SUM(score) FROM aggregator_service.user_action where event_id = :eventAId), 0.0) as sA,
                    COALESCE((SELECT SUM(score) FROM aggregator_service.user_action where event_id = :eventBId), 0.0) as sB,
                    CURRENT_TIMESTAMP as timestamp
            FROM sum_min sm
            """ , nativeQuery = true)
    EventSimilarityCalculationResult calculateSimilarity(Long eventAId, Long eventBId);

    @Query("""
            SELECT DISTINCT(ua.eventId)
            FROM UserAction as ua
            WHERE ua.eventId != :eventId
            """)
    List<Long> findAllEventIds(Long eventId);
}
