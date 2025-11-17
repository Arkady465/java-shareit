package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingClient.addBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookingByOwner(ownerId, state);
    }
}
