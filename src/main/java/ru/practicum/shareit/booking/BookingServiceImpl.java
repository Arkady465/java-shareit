package ru.practicum.shareit.booking;

import ru.practicum.shareit.common.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.common.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingResponseDto create(Long userId, BookingRequestDto dto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available");
        }

        // Нельзя бронировать собственную вещь
        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Item not found: " + dto.getItemId());
        }

        if (dto.getEnd().isBefore(dto.getStart()) ||
                dto.getEnd().isEqual(dto.getStart())) {
            throw new IllegalArgumentException("End date must be after start");
        }

        Booking booking = new Booking(
                null,
                dto.getStart(),
                dto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING
        );

        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Booking not found: " + bookingId);
        }

        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new ValidationException("User is not item owner");
        }


        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalArgumentException("Booking already decided");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updated = bookingRepository.save(booking);

        return BookingMapper.toDto(updated);
    }

    @Override
    public BookingResponseDto get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        Long ownerId = booking.getItem().getOwnerId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new NotFoundException("Booking not found: " + bookingId);
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        List<Booking> all = bookingRepository.findByBookerIdOrderByStartDesc(userId);

        return filterByState(all, state);
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, BookingState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found: " + ownerId));

        List<Booking> all = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);

        return filterByState(all, state);
    }

    private List<BookingResponseDto> filterByState(List<Booking> all, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        return all.stream()
                .filter(b -> switch (state) {
                    case ALL -> true;
                    case CURRENT -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
                    case PAST -> b.getEnd().isBefore(now);
                    case FUTURE -> b.getStart().isAfter(now);
                    case WAITING -> b.getStatus() == BookingStatus.WAITING;
                    case REJECTED -> b.getStatus() == BookingStatus.REJECTED;
                })
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}

