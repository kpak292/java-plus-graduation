package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EventSimilarityScore;

import java.util.List;
import java.util.Optional;

public interface EventSimilarityScoreRepository extends JpaRepository<EventSimilarityScore, Long> {
    Optional<EventSimilarityScore> findByEventAIdAndEventBId(Long eventAId, Long eventBId);

    @Query(value = """
            with user_events as (select ua.event_id
                                 from stats_service.user_action ua
                                 where ua.user_id = :userId)
            select es.event_b_id as eventId,
                   max(es.score) as score
            from stats_Service.event_similarity_score es
            where es.event_a_id in (select ue.event_id
                                    from user_events ue)
              and es.event_b_id not in (select ue.event_id
                                        from user_events ue)
            group by es.event_b_id
            order by max(es.score) desc
            limit :limit
            """, nativeQuery = true)
    List<RecommendedEvent> findRecommendationsForUser(Long userId, Integer limit);

    @Query(value = """
            with user_events as (select ua.event_id
                                 from stats_service.user_action ua
                                 where ua.user_id = :userId)
            select es.event_b_id as eventId,
                   max(es.score) as score
            from stats_Service.event_similarity_score es
            where es.event_a_id = :eventId
              and es.event_b_id not in (select ue.event_id
                                        from user_events ue)
            group by es.event_b_id
            order by max(es.score) desc
            limit :limit
            """, nativeQuery = true)
    List<RecommendedEvent> findSimilarEvents(Long eventId, Long userId, Integer limit);

    @Query(value = """
            select ua.event_id   as event_id,
                   sum(ua.score) as score
            from stats_service.user_action ua
            where ua.event_id = :eventId
            group by ua.event_id
            
            """, nativeQuery = true)
    List<RecommendedEvent> findInteractionsCount(Long eventId);
}
