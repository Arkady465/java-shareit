package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody BookItemRequestDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @RequestHeader(USER_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam("approved") boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getForBooker(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getForBooker(userId, State.from(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getForOwner(
            @RequestHeader(USER_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getForOwner(ownerId, State.from(state));
    }
}
