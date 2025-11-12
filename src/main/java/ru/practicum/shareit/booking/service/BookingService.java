package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.Collection;

public interface BookingService {
    Collection<BookingResponseDto> findAllBookingsUser(String state, Long userId);

    Collection<BookingResponseDto> findAllBookingsOwner(String state, Long ownerId);

    BookingResponseDto findById(Long bookingId, Long userId);

    BookingResponseDto create(BookingRequestDto booking, Long bookerId);

    BookingResponseDto update(Long bookingId, Long userId, boolean approved);
}
