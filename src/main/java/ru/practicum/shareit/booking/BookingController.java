package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid BookingCreateDto dto
    ) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @RequestHeader(USER_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<Booking> listForBooker(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state
    ) {
        return bookingService.listForBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> listForOwner(
            @RequestHeader(USER_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state
    ) {
        return bookingService.listForOwner(ownerId, state);
    }
}
