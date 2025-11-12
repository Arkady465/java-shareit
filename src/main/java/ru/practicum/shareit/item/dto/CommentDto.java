package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentDto {

    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}
