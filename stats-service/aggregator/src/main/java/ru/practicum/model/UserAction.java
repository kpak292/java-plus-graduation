package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_action", schema = "stats_service")
public class UserAction {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    Long userId;
    Long eventId;
    Double score;
    @Column(name = "action_at")
    LocalDateTime timestamp;
    @CreationTimestamp
    LocalDateTime createdAt;
}
