package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.service.EventService;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v2/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventControllerApi {
    private final EventService eventService;

    @GetMapping("/{eventId}")
    public Optional<EventDto> findEventById(@PathVariable long eventId) {
        log.info("getting event {} ", eventId);
        return eventService.findEventById(eventId);
    }

    @PutMapping("{eventId}/confirmed")
    public void setConfirmed(@PathVariable long eventId,
                             @RequestBody long requests) {
        log.info("setting confirmed for event {} to {}", eventId, requests);
        eventService.setConfirmed(eventId, requests);
    }
}