package ru.practicum.shareit.booking;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {

    BookingDto create(Long bookerId, BookingCreateDto dto);

    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingDto get(Long userId, Long bookingId);

    List<Booking> listForBooker(Long userId, BookingState state);

    List<BookingDto> listForOwner(Long ownerId, BookingState state);
}
