package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "event_similarity", schema = "aggregator_service")
@NoArgsConstructor
@AllArgsConstructor
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @Column(name = "event_a_id")
    Long eventAId;
    @Column(name = "event_b_id")
    Long eventBId;
    @Column(name = "s_min")
    Double sMin;
    @Column(name = "s_a")
    Double sA;
    @Column(name = "s_b")
    Double sB;
    @Column(name = "calculated_at")
    LocalDateTime timestamp;
    @CreationTimestamp
    LocalDateTime createdAt;

    public EventSimilarity(Long eventAId, Long eventBId, Double sMin, Double sA, Double sB, LocalDateTime timestamp) {
        this.eventAId = eventAId;
        this.eventBId = eventBId;
        this.sMin = sMin;
        this.sA = sA;
        this.sB = sB;
        this.timestamp = timestamp;
    }
}
