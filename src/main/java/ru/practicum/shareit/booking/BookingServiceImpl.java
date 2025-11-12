package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    @Override
    public BookingDto create(Long bookerId, BookingCreateDto dto) {
        User booker = userRepo.findById(bookerId).orElseThrow();
        Item item = itemRepo.findById(dto.getItemId()).orElseThrow();

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new IllegalStateException("Item not available");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new IllegalStateException("Owner cannot book own item");
        }

        if (dto.getStart().isAfter(dto.getEnd()) || dto.getStart().isEqual(dto.getEnd())) {
            throw new IllegalArgumentException("Invalid time range");
        }

        Booking b = new Booking();
        b.setItem(item);
        b.setBooker(booker);
        b.setStart(dto.getStart());
        b.setEnd(dto.getEnd());
        b.setStatus(BookingStatus.WAITING);

        bookingRepo.save(b);
        return BookingMapper.toDto(b);
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow();

        if (!b.getItem().getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Only owner can approve/reject");
        }

        if (b.getStatus() != BookingStatus.WAITING) {
            throw new IllegalStateException("Only WAITING can be changed");
        }

        b.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(b);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto get(Long userId, Long bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow();
        Long ownerId = b.getItem().getOwner().getId();
        Long bookerId = b.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new SecurityException("Not allowed");
        }

        return BookingMapper.toDto(b);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> listForBooker(Long userId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case ALL -> bookingRepo.findByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepo.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepo.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepo.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepo.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepo.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        }.stream().map(BookingMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> listForOwner(Long ownerId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> list = switch (state) {
            case ALL -> bookingRepo.findAllByOwner(ownerId);
            case CURRENT -> bookingRepo.findCurrentByOwner(ownerId, now);
            case PAST -> bookingRepo.findPastByOwner(ownerId, now);
            case FUTURE -> bookingRepo.findFutureByOwner(ownerId, now);
            case WAITING -> bookingRepo.findByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepo.findByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
        };

        return list.stream().map(BookingMapper::toDto).toList();
    }
}
