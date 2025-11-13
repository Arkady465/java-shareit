package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {

    public static CommentDto toDto(Comment c) {
        return new CommentDto(
                c.getId(),
                c.getText(),
                c.getAuthor().getName(),
                c.getCreated()
        );
    }
}
