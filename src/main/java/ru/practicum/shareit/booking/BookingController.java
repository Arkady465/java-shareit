package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String OWNER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public Collection<BookingResponseDto> getAllBookingsUser(@RequestParam(name = "state", defaultValue = "ALL") String state, @RequestHeader(OWNER) Long userId) {
        return bookingService.findAllBookingsUser(state, userId);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getAllBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state, @RequestHeader(OWNER) Long ownerId) {
        return bookingService.findAllBookingsOwner(state, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId, @RequestHeader(OWNER) Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @PostMapping()
    public BookingResponseDto create(@Valid @RequestBody BookingRequestDto bookingRequestDto, @RequestHeader(OWNER) Long userId) {
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(@PathVariable Long bookingId, @RequestHeader(OWNER) Long userId, @RequestParam Boolean approved) {
        return bookingService.update(bookingId, userId, approved);
    }
}
