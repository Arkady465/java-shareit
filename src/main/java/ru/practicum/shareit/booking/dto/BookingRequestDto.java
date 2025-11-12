package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    @PositiveOrZero(message = "Id брони не может быть отрицательным числом")
    private Long id;

    @NotNull(message = "Дата начала бронирования не может быть null")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    private LocalDateTime end;

    @NotNull(message = "Id бронируемой вещи не может быть null")
    @PositiveOrZero(message = "Id не может отрицательным числом")
    private Long itemId;

    @PositiveOrZero(message = "Id арендатора не может быть отрицательным числом")
    private Long bookerId;

    private Status status;
}
