package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static BookingDto toDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setItemId(b.getItem().getId());
        dto.setBookerId(b.getBooker().getId());
        dto.setStart(b.getStart());
        dto.setEnd(b.getEnd());
        dto.setStatus(b.getStatus());
        return dto;
    }
}
