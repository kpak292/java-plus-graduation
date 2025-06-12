package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.stats.StatsHitDto;
import ru.practicum.dto.stats.StatsViewDto;

import java.util.Collection;
import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsClient {
    @PostMapping("/hit")
    StatsHitDto saveHit(@RequestBody StatsHitDto hitDto);

    @GetMapping("/stats")
    Collection<StatsViewDto> getStat(@RequestParam String start,
                                     @RequestParam String end,
                                     @RequestParam(required = false) List<String> uris,
                                     @RequestParam(required = false, defaultValue = "false") Boolean unique);
}
