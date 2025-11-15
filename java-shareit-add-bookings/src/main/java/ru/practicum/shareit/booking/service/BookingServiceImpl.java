package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dao.BookingRepositoryJpa;
import ru.practicum.shareit.booking.mapper.BookingMapper; // <-- ВАЖНО: mapper-пакет

import ru.practicum.shareit.item.dao.ItemRepositoryJpa;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.user.User;                      // <-- без .model

import ru.practicum.shareit.exception.NotFoundException;   // <-- exception, не exceptions
import ru.practicum.shareit.exception.ValidationException; // <-- exception, не exceptions


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepositoryJpa bookingRepository;
    private final UserRepositoryJpa userRepository;
    private final ItemRepositoryJpa itemRepository;

    @Override
    public BookingDto bookItem(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null || !start.isBefore(end)) {
            throw new ValidationException("Invalid booking time");
        }

        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(savedBooking);
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can approve/reject booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already processed");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(savedBooking);
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        validateUser(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new NotFoundException("Booking not available for this user");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, String state) {
        validateUser(ownerId);

        String stateStr = (state != null) ? state.toUpperCase() : "ALL";
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (stateStr) {
            case "ALL":
                bookings = bookingRepository.findAllByItem_Owner_Id(ownerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByItem_Owner_IdAndEndBefore(ownerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfter(ownerId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByUser(Long userId, String state) {
        validateUser(userId);

        String stateStr = (state != null) ? state.toUpperCase() : "ALL";
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (stateStr) {
            case "ALL":
                bookings = bookingRepository.findAllByBooker_Id(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(userId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByBooker_IdAndEndBefore(userId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBooker_IdAndStartAfter(userId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    private void validateUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}

