package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(Long userId, BookItemRequestDto dto) {
        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new ValidationException("Start must be before end");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item not available");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        Booking booking = BookingMapper.toBooking(dto, item, booker);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Not owner");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Not found"));

        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new NotFoundException("Forbidden");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getForBooker(Long userId, State state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return filter(bookingRepository.findByBookerIdOrderByStartDesc(userId), state);
    }

    @Override
    public List<BookingDto> getForOwner(Long ownerId, State state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found"));
        return filter(bookingRepository.findByOwnerIdOrderByStartDesc(ownerId), state);
    }

    private List<BookingDto> filter(List<Booking> bookings, State state) {
        LocalDateTime now = LocalDateTime.now();

        return bookings.stream()
                .filter(b -> switch (state) {
                    case ALL -> true;
                    case CURRENT -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
                    case PAST -> b.getEnd().isBefore(now);
                    case FUTURE -> b.getStart().isAfter(now);
                    case WAITING -> b.getStatus() == BookingStatus.WAITING;
                    case REJECTED -> b.getStatus() == BookingStatus.REJECTED;
                })
                .map(BookingMapper::toDto)
                .toList();
    }
}
