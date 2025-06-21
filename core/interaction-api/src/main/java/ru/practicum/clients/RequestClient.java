package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "request-service")
public interface RequestClient {
    @GetMapping("/api/v2/users/{userId}/events/{eventId}/requests/comfirmed")
    Boolean checkRegistration(@PathVariable long userId,
                              @PathVariable long eventId);
}
