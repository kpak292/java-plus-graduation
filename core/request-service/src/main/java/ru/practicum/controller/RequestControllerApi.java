package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.RequestService;

@Slf4j
@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestControllerApi {
    private final RequestService requestService;

    @GetMapping("/{userId}/events/{eventId}/requests/comfirmed")
    public Boolean checkRegister(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("Checking registration for user {} on event {}", userId, eventId);
        return requestService.checkRegister(userId, eventId);
    }
}
