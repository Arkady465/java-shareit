package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImp implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImp(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingResponseDto> findAllBookingsUser(String state, Long userId) {
        log.info("Получен запрос на все сделанные брони пользователем с id = {}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new NotFoundException("Пользователя не существует");
        }

        BookingState bookingState = BookingState.from(state);

        Collection<Booking> bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId);
            case PAST -> bookingRepository.findPastByBookerId(userId);
            case FUTURE -> bookingRepository.findFutureByBookerId(userId);
            case WAITING -> bookingRepository.findByBookerIdAndWaitingStatus(userId);
            case REJECTED -> bookingRepository.findByBookerIdAndRejectStatus(userId);
            default -> bookingRepository.findAllByBookerId(userId);
        };

        Collection<BookingResponseDto> bookingResponseDtoCollection = bookings.stream()
                .map(booking -> {
                    User booker = userRepository.findById(booking.getBooker().getId())
                            .orElseThrow(() -> {
                                log.warn("Пользователь с id = {} не существует", booking.getBooker().getId());
                                return new NotFoundException("Пользователь не существует");
                            });

                    Item item = itemRepository.findById(booking.getItem().getId())
                            .orElseThrow(() -> {
                                log.warn("Вещи для брони с id = {} не существует", booking.getItem().getId());
                                return new NotFoundException("Вещи для брони не существует");
                            });

                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                })
                .collect(Collectors.toList());

        log.info("Найдено бронирований в количестве {} для пользователя c id = {}, со статусом {}", bookings.size(), userId, state);
        return bookingResponseDtoCollection;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingResponseDto> findAllBookingsOwner(String state, Long ownerId) {
        log.info("Получен запрос на все брони владельца вещей с id = {}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            log.warn("Пользователя с id = {} не существует", ownerId);
            throw new NotFoundException("Пользователя не существует");
        }

        BookingState bookingState = BookingState.from(state);

        Collection<Booking> bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByOwnerId(ownerId);
            case PAST -> bookingRepository.findPastByOwnerId(ownerId);
            case FUTURE -> bookingRepository.findFutureByOwnerId(ownerId);
            case WAITING -> bookingRepository.findWaitingByOwnerId(ownerId);
            case REJECTED -> bookingRepository.findRejectedByOwnerId(ownerId);
            default -> bookingRepository.findAllByOwnerId(ownerId);
        };

        Collection<BookingResponseDto> bookingResponseDtoCollection = bookings.stream()
                .map(booking -> {
                    User booker = userRepository.findById(booking.getBooker().getId())
                            .orElseThrow(() -> {
                                log.warn("Пользователь с id = {} не существует", booking.getBooker().getId());
                                return new NotFoundException("Пользователь не существует");
                            });

                    Item item = itemRepository.findById(booking.getItem().getId())
                            .orElseThrow(() -> {
                                log.warn("Вещи для брони с id = {} не существует", booking.getItem().getId());
                                return new NotFoundException("Вещи для брони не существует");
                            });

                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                })
                .collect(Collectors.toList());

        log.info("Найдено бронирований в количестве {} для владельца вещей с id = {}, со статусом {}", bookings.size(), ownerId, state);
        return bookingResponseDtoCollection;
    }

    @Transactional(readOnly = true)
    @Override
    public BookingResponseDto findById(Long bookingId, Long userId) {
        log.info("Запрос на получение брони с id = {} от пользователя с id = {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Бронь с id = {} не найдена", bookingId);
            return new NotFoundException("Бронь не найден");
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не существует", userId);
                    return new NotFoundException("Пользователь не существует");
                });

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> {
                    log.warn("Вещи для брони с id = {} не существует", booking.getItem().getId());
                    return new NotFoundException("Вещи для брони не существует");
                });

        if (!booking.getBooker().getId().equals(user.getId()) && !item.getOwner().getId().equals(user.getId())) {
            log.warn("Пользователь с id = {} не имеет доступа к бронированию c id = {}", userId, bookingId);
            throw new NotAccessException("Пользователь не имеет доступа к бронированию");
        }

        log.info("Бронь с id = {} успешно найдена", bookingId);
        return BookingMapper.toBookingResponseDto(booking, item, user);
    }

    @Transactional
    @Override
    public BookingResponseDto create(BookingRequestDto booking, Long bookerId) {
        log.info("Запрос на создание брони для вещи с id = {} пользователем с id = {}", booking.getItemId(), bookerId);

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> {
                    log.warn("Пользователя с id = {} не существует", bookerId);
                    return new NotFoundException("Пользователя не существует");
                });

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> {
                    log.warn("Вещи для бронирования с id = {} не существует", booking.getItemId());
                    return new NotFoundException("Вещи не существует");
                });

        LocalDateTime now = LocalDateTime.now();

        if (!item.getAvailable()) {
            log.warn("Вещь с id = {} не доступна для бронирования", item.getId());
            throw new NotAccessException("Вещь не доступна для бронирования");
        }

        if (item.getOwner().getId().equals(booker.getId())) {
            log.warn("Владелец с id = {} не может бронировать свою вещь с id = {}", booker.getId(), item.getId());
            throw new NotAccessException("Владелец не может бронировать свою вещь");
        }

        if (booking.getStart().isBefore(now) && booking.getEnd().isBefore(now)) {
            log.warn("Время начало и окончания бронирования не может быть в прошлом");
            throw new NotAccessException("Время начало и окончания бронирования не может быть в прошлом");
        }

        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            log.warn("Некорректное время бронирования");
            throw new ValidationException("Некорректное время бронирования");
        }

        Booking createBooking = bookingRepository.save(BookingMapper.toBooking(booking, item, booker));

        log.info("Бронь для вещи с id = {} пользователем с id = {} успешно создана", createBooking.getItem().getId(), createBooking.getBooker().getId());
        return BookingMapper.toBookingResponseDto(createBooking, item, booker);
    }

    @Transactional
    @Override
    public BookingResponseDto update(Long bookingId, Long userId, boolean approved) {
        log.info("Запрос на обновление брони для вещи с id = {} пользователем с id = {}", bookingId, userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Владельца вещи с id = {} не существует", bookingId);
                    return new NotAccessException("Владельца вещи не существует");
                });

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Брони с id = {} не существует", bookingId);
            return new NotFoundException("Брони не существует");
        });

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> {
                    log.warn("Вещи для обновления брони с id = {} не существует", bookingId);
                    return new NotFoundException("Вещи не существует");
                });

        User booker = userRepository.findById(booking.getBooker().getId()).orElseThrow(() -> {
            log.warn("Автора брони с id = {} не существует", bookingId);
            return new NotFoundException("Автора брони не существует");
        });

        if (!item.getOwner().getId().equals(owner.getId())) {
            log.warn("Пользователь id = {} не является владельцем вещи id = {}", userId, booking.getItem().getId());
            throw new NotAccessException("Пользователь не является владельцем вещи");
        }

        if (booking.getStatus() == Status.APPROVED && approved) {
            throw new ValidationException("Бронирование уже одобрено");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Статус брони вещи с id = {} изменён на {}", booking.getItem().getId(), booking.getStatus());
        return BookingMapper.toBookingResponseDto(updatedBooking, item, booker);
    }
}
