package ru.practicum.controller.privateapi;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.service.EventService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/{userId}/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(@PathVariable long userId,
                             @RequestBody @Valid NewEventDto newEventDto) {
        log.info("adding new event from user {} with body: {}", userId, newEventDto);
        return eventService.save(userId, newEventDto);
    }

    @GetMapping
    public Collection<EventShortDto> getEvents(@PathVariable long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("getting events for user {}", userId);
        return eventService.findEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable long userId,
                             @PathVariable long eventId) {
        log.info("getting event {} for user {}", eventId, userId);
        return eventService.findEvent(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEvent(@PathVariable long userId,
                             @PathVariable long eventId,
                             @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("updating event {} for user {} with body {}", eventId, userId, updateEventUserRequest);
        return eventService.updateEvent(eventId, userId, updateEventUserRequest);
    }
}
