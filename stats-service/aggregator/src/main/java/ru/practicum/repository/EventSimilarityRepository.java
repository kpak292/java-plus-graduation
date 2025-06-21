package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
                SELECT case when ua2.score is null then 0
                    when ua.score is null then 0
                    when ua.score >= ua2.score then ua2.score
                    else ua.score
                    end as mins
                FROM stats_service.user_action ua
                full join (select ua2.user_id, ua2.score as score from stats_service.user_action ua2 where event_id = :eventBId) ua2 on
                    ua.user_id = ua2.user_id
                WHERE ua.event_id = :eventAId )
            SELECT :eventAId as eventAId,
                   :eventBId as eventBId,
                   COALESCE(SUM(sm.mins), 0.0) as sMin,
                   COALESCE((SELECT SUM(score) FROM stats_service.user_action where event_id = :eventAId), 0.0) as sA,
                   COALESCE((SELECT SUM(score) FROM stats_service.user_action where event_id = :eventBId), 0.0) as sB,
                   CURRENT_TIMESTAMP as timestamp
            FROM sum_min sm
            """, nativeQuery = true)
    EventSimilarityCalculationResult calculateSimilarity(Long eventAId, Long eventBId);

    @Query("""
            SELECT DISTINCT(ua.eventId)
            FROM UserAction as ua
            WHERE ua.eventId != :eventId
            """)
    List<Long> findAllEventIds(Long eventId);
}
