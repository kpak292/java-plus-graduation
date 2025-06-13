package ru.practicum.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDto getCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    Comment getComment(NewCommentDto commentDto, Long event, Long author);

    List<CommentDto> toCommentDtoList(List<Comment> comments);
}
