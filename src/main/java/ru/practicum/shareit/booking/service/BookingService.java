package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingDto, Long userId);

    BookingResponseDto updateBooking(Long bookingId, Boolean approved, Long userId);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    List<BookingResponseDto> getUserBookings(BookingState state, Long userId, Integer from, Integer size);

    List<BookingResponseDto> getOwnerBookings(BookingState state, Long userId, Integer from, Integer size);
}