package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.Constants;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EventDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    LocalDateTime createdOn;
    String description;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    LocalDateTime eventDate;
    Long initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Double rating;
    List<CommentDto> comments;
}
