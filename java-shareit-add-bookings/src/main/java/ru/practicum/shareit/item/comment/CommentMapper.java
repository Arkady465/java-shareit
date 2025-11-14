package ru.practicum.shareit.item.comment;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        String authorName = "Unknown";

        if (comment.getAuthor() != null) {
            authorName = comment.getAuthor().getName();
        }

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated()
        );
    }
}
