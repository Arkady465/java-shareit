package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookingCreateDto {

    @NotNull
    Long itemId;

    @NotNull
    LocalDateTime start;

    @NotNull
    LocalDateTime end;
}
