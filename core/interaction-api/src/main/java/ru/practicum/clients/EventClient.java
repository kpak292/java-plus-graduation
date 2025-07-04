package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.event.EventDto;

import java.util.Optional;

@FeignClient(name = "event-service")
public interface EventClient {
    @GetMapping("/api/v2/events/{eventId}")
    Optional<EventDto> findEvent(@PathVariable long eventId);

    @PutMapping("/api/v2/events/{eventId}/confirmed")
    void setConfirmed(@PathVariable long eventId,
                      @RequestBody long requests);

    @GetMapping("/api/v2/events/{eventId}")
    Optional<EventDto> findEventById(@PathVariable long eventId);
}
