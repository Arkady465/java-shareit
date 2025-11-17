package ru.practicum.shareit.booking.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingCreateDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
