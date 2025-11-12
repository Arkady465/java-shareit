package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemShortDto {
    @PositiveOrZero(message = "Id вещи не может быть отрицательным числом")
    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull(message = "Статус вещи не может быть null")
    private Boolean available;

    @PositiveOrZero(message = "Id владельца не может быть отрицательным числом")
    private Long ownerId;

    @PositiveOrZero(message = "Id запроса не может быть отрицательным числом")
    private Long requestId;
}
