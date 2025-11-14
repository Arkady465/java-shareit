package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.exceptions.NotFoundException;
import ru.practicum.shareit.common.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, Booking booking) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }

        var item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + booking.getItem().getId()));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }

        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        if (booking.getStart().isAfter(booking.getEnd()) ||
                booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Invalid booking dates");
        }

        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        Long itemOwner = booking.getItem().getOwnerId();

        // ❗ → Тест ждёт ValidationException, а НЕ 404
        if (!itemOwner.equals(ownerId)) {
            throw new ValidationException("User is not item owner");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking status already updated");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updated = bookingRepository.save(booking);

        return BookingMapper.toDto(updated);
    }

    @Override
    public BookingResponseDto get(Long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        boolean isOwner = booking.getItem().getOwnerId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new NotFoundException("Booking not found: " + bookingId);
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAll(Long userId, String state) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }

        BookingState bookingState = parseState(state);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findCurrent(userId, now);
                break;
            case PAST:
                bookings = bookingRepository.findPast(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFuture(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state) {

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found: " + ownerId);
        }

        BookingState bookingState = parseState(state);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findCurrentOwner(ownerId, now);
                break;
            case PAST:
                bookings = bookingRepository.findPastOwner(ownerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureOwner(ownerId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findOwnerWaiting(ownerId);
                break;
            case REJECTED:
                bookings = bookingRepository.findOwnerRejected(ownerId);
                break;
            default:
                bookings = bookingRepository.findAllOwner(ownerId);
        }

        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}

