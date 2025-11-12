package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
public class CommentRequestDto {
    @NotNull(message = "Содержание комментария не должно быть null")
    private String text;

    private String authorName;

    private LocalDateTime created;
}