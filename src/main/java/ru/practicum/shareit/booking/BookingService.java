package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingRequestDto dto);

    BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingResponseDto get(Long userId, Long bookingId);

    List<BookingResponseDto> getUserBookings(Long userId, BookingState state);

    List<BookingResponseDto> getOwnerBookings(Long ownerId, BookingState state);
}
