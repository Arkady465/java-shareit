package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.Collection;

@Data
@Builder
public class ItemFullDto {
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

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private Collection<CommentResponseDto> comments;
}
