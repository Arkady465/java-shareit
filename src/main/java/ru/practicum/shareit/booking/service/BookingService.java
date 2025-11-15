package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

public interface BookingService {
    BookingDto add(Long userId, BookingCreateDto bookingCreateDto);

    BookingDto update(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getByOwner(Long ownerId, String state);

    List<BookingDto> getByUser(Long userId, String state);
}
