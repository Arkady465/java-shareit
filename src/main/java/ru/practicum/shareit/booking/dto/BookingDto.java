package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

@Data
public class BookingDto {

    Long id;
    Long itemId;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
}
