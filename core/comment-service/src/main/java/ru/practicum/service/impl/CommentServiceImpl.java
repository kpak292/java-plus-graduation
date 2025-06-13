package ru.practicum.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.clients.EventClient;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.enums.EventState;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.service.CommentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventClient eventClient;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto save(Long userId, Long eventId, NewCommentDto newCommentDto) {
        EventDto event = eventClient.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id %d not found".formatted(eventId)));
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Event is not published");
        }
        Comment comment = commentMapper.getComment(
                newCommentDto,
                eventId,
                userId
        );
        return commentMapper.getCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAll(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "id"));
        return commentRepository.findAllByEvent(pageable, eventId).stream()
                .map(commentMapper::getCommentDto)
                .toList();
    }

    @Override
    public List<CommentDto> getAll(Long eventId) {
        return commentRepository.findAllByEvent(eventId).stream()
                .map(commentMapper::getCommentDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto update(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = validateCommentForEvent(eventId, commentId, userId);
        comment.setMessage(newCommentDto.getMessage());
        return commentMapper.getCommentDto(comment);
    }

    @Override
    public void delete(Long userId, Long eventId, Long commentId) {
        Comment comment = validateCommentForEvent(eventId, commentId, userId);
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto adminUpdate(Long eventId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = validateCommentForEvent(eventId, commentId);
        comment.setMessage(newCommentDto.getMessage());
        return commentMapper.getCommentDto(comment);
    }

    @Override
    public void adminDelete(Long eventId, Long commentId) {
        Comment comment = validateCommentForEvent(eventId, commentId);
        commentRepository.delete(comment);
    }

    private Comment checkExistsAndReturnComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id %d not found".formatted(commentId)));
    }

    private Comment validateCommentForEvent(Long eventId, Long commentId, Long userId) {
        Comment comment = checkExistsAndReturnComment(commentId);

        if (comment.getEvent().equals(eventId)) {
            throw new ConflictException("Comment with id %d is not belong to event with id %d"
                    .formatted(comment.getId(), eventId));
        }
        if (!userId.equals(comment.getAuthor())) {
            throw new ConflictException("Comment with id %d is not created by user with id %d"
                    .formatted(comment.getAuthor(), userId));
        }
        return comment;
    }

    private Comment validateCommentForEvent(Long eventId, Long commentId) {
        Comment comment = checkExistsAndReturnComment(commentId);

        if (comment.getEvent().equals(eventId)) {
            throw new ConflictException("Comment with id %d is not belong to event with id %d"
                    .formatted(comment.getId(), eventId));
        }
        return comment;
    }
}
