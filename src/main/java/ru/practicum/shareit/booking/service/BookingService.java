package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, Long userId);

    BookingDto updateStatus(Long bookingId, Long userId, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size);
}
