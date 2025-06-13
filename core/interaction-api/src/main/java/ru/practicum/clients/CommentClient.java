package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.comment.CommentDto;

import java.util.List;

@FeignClient(name = "comment-service")
public interface CommentClient {
    @GetMapping("/api/v2/admin/events/{eventId}/comments")
    List<CommentDto> getComments(@PathVariable long eventId);
}
