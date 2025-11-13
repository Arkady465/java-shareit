package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    // Создание бронирования
    @PostMapping
    public BookingResponseDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingRequestDto dto) {
        return service.create(userId, dto);
    }

    // Подтверждение или отклонение бронирования
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        return service.approve(ownerId, bookingId, approved);
    }

    // Получение данных о бронировании
    @GetMapping("/{bookingId}")
    public BookingResponseDto get(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return service.get(userId, bookingId);
    }

    // Бронирования текущего пользователя (booker)
    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        BookingState s = BookingState.from(state);
        return service.getUserBookings(userId, s);
    }

    // Бронирования вещей владельца
    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        BookingState s = BookingState.from(state);
        return service.getOwnerBookings(ownerId, s);
    }
}
