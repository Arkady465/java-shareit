package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto add(Long userId, BookingCreateDto bookingCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        Booking booking = Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can update booking status");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking status already decided");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Access denied");
        }

        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerId(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(userId, now, now, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBefore(userId, now, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfter(userId, now, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByUser(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBefore(userId, now, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfter(userId, now, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }

    private BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new BookingSimpleDto(booking.getBooker().getId(), booking.getBooker().getId()))
                .item(new BookingSimpleDto(booking.getItem().getId(), booking.getBooker().getId()))
                .build();
    }
}

