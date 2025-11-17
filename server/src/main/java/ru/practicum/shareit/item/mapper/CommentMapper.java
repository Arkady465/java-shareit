package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static Comment mapToComment(CommentDto commentDto, Item item, User author) {
        if (commentDto == null) {
            throw new NotFoundException("CommentDto cannot be null");
        }

        if (item == null) {
            throw new NotFoundException("Item cannot be null");
        }

        if (author == null) {
            throw new NotFoundException("Author cannot be null");
        }

        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(commentDto.getCreated());

        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        if (comment == null) {
            throw new NotFoundException("Comment cannot be null");
        }

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }
}
