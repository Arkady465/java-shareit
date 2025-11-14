package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingService.add(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByOwner(ownerId, state);
    }

    @GetMapping
    public List<BookingDto> getBookingByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByUser(userId, state);
    }
}
