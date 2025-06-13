package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments", schema = "comments_service")
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;

    @Column(name = "event_id")
    Long event;

    @Column(name = "author_id")
    Long author;

    @Column(name = "message")
    String message;
}
