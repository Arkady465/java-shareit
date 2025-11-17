package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO-объект отзыва Comment.
 */
@Data
public class CommentDto {
    private Long id;

    @NotBlank
    private String text;

    private String authorName;

    private LocalDateTime created;
}
