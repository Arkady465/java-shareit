package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepositoryJpa;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepositoryJpa;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepositoryJpa bookingRepository;
    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;

    @Override
    public BookingDto add(Long userId, BookingCreateDto bookingCreateDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + bookingCreateDto.getItemId() + " не найдена"));

        if (!item.isAvailable()) {
            throw new ValidationException("Вещь  с id = " + item.getId() + " недоступна для бронирования");
        }

        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart()) ||
                bookingCreateDto.getEnd().equals(bookingCreateDto.getStart())) {
            throw new ValidationException("Дата начала бронирования не может быть равна или раньше даты окончания");
        }

        if (bookingCreateDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        }

        if (bookingCreateDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания бронирования не может быть в прошлом");
        }

        Booking booking = BookingMapper.mapToBooking(bookingCreateDto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(savedBooking);
    }

    @Override
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new InternalServerException("Редактировать вещь может только её владелец");
        }

        validateUser(userId);

        if (approved != null && approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(savedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)
                && !booking.getBooker().getId().equals(userId)) {
            throw new InternalServerException("""
                    Получение данных о бронировании может быть выполнено либо автором бронирования,
                    либо владельцем вещи, к которой относится бронирование
                    """
            );
        }

        validateUser(userId);

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, String state) {
        validateUser(ownerId);
        List<Booking> bookings;
        LocalDateTime currentDate = LocalDateTime.now();

        bookings = switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItem_Owner_Id(ownerId);
                break;
            case "PAST":
                bookings = bookingRepository.findByItem_Owner_IdAndEndBefore(ownerId, currentDate);
                break;
            case "CURRENT":
                bookings = bookingRepository
                        .findByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, currentDate, currentDate);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfter(ownerId, currentDate);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED);
                break;

        };

        return bookings.stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByUser(Long userId, String state) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        LocalDateTime currentDate = LocalDateTime.now();
        List<Booking> bookings;

        switch (stateStr) {
            case "ALL":
                bookings = bookingRepository.findAllByBooker(user);
                break;
            case "CURRENT":
                bookings = bookingRepository
                        .findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user, currentDate, currentDate);
                break;
            case "PAST":
                bookings = bookingRepository
                        .findByBookerAndEndIsBeforeOrderByStartDesc(user, currentDate);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByBookerAndStartIsAfterOrderByStartDesc(user, currentDate);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByBooker_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository
                        .findByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + stateStr);
        }


        return bookings.stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    private void validateUser(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
    }
}
