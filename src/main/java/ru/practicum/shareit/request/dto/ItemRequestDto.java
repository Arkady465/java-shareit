package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    @PositiveOrZero(message = "Id запроса не может быть отрицательным числом")
    private int id;

    private String description;

    @PositiveOrZero(message = "Id пользователя запроса не может быть отрицательным числом")
    private Long requesterId;

    @NotNull(message = "Дата запроса не может быть null")
    @FutureOrPresent(message = "Дата запроса не может быть в прошлом")
    private LocalDateTime created;
}
