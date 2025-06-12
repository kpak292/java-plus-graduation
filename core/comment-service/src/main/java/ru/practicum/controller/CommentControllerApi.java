package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v2/admin/events")
@RequiredArgsConstructor
public class CommentControllerApi {
    private final CommentService commentService;

    @GetMapping("/{eventId}/comments")
    public List<CommentDto> getComments(@PathVariable long eventId) {
        log.info("apiv2 get comments for eventId {}", eventId);
        return commentService.getAll(eventId);
    }
}
