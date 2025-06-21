package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "event_similarity_score", schema = "stats_service")
@NoArgsConstructor
@AllArgsConstructor
public class EventSimilarityScore {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @Column(name = "event_a_id")
    Long eventAId;
    @Column(name = "event_b_id")
    Long eventBId;
    Double score;
    @Column(name = "calculated_at")
    LocalDateTime timestamp;
}
