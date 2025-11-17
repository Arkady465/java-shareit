package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingCreateDto {
    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
