package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepositoryJpa;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepositoryJpa;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepositoryJpa bookingRepository;
    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;

    @Override
    public BookingDto createBooking(Long userId, BookingSimpleDto bookingSimpleDto) {
        validateBookingDates(bookingSimpleDto);

        User booker = validateUser(userId);
        Item item = validateItem(bookingSimpleDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Item not available for booking");
        }

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Owner cannot book their own item");
        }

        Booking booking = BookingMapper.toBooking(bookingSimpleDto, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(saved);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = validateBooking(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Only owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(saved);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = validateBooking(bookingId);

        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("User has no access to this booking");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByUser(Long userId, String state, int from, int size) {
        User booker = validateUser(userId);

        if (from < 0 || size <= 0) {
            throw new ValidationException("Pagination parameters are invalid");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBooker(booker, pageable).getContent();
                break;
            case "CURRENT":
                bookings = bookingRepository
                        .findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(booker, now, now);
                break;
            case "PAST":
                bookings = bookingRepository
                        .findByBookerAndEndIsBeforeOrderByStartDesc(booker, now);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByBookerAndStartIsAfterOrderByStartDesc(booker, now);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository
                        .findByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, String state, int from, int size) {
        User owner = validateUser(ownerId);

        if (from < 0 || size <= 0) {
            throw new ValidationException("Pagination parameters are invalid");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository
                        .findByItemOwnerId(owner.getId(), pageable)
                        .getContent();
                break;
            case "CURRENT":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartBeforeAndEndAfter(
                                owner.getId(), now, now);
                break;
            case "PAST":
                bookings = bookingRepository
                        .findByItemOwnerIdAndEndBefore(owner.getId(), now);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartAfter(owner.getId(), now);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }

    private Booking validateBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
    }

    private void validateBookingDates(BookingSimpleDto bookingSimpleDto) {
        if (bookingSimpleDto.getStart() == null || bookingSimpleDto.getEnd() == null) {
            throw new ValidationException("Booking dates must not be null");
        }
        if (!bookingSimpleDto.getEnd().isAfter(bookingSimpleDto.getStart())) {
            throw new ValidationException("End time must be after start time");
        }
    }
}


