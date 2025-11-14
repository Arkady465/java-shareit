package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {

    @NotNull
    private Long itemId;

    @NotNull
    @FutureOrPresent(message = "Начало бронирования должно быть в будущем")
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent(message = "Конец бронирования должен быть в будущем")
    private LocalDateTime end;
}
