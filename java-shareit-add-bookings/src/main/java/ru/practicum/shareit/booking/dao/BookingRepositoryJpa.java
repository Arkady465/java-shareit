package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepositoryJpa extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem(Item item);

    // все бронирования вещей конкретного владельца
    List<Booking> findAllByItem_Owner_Id(Long ownerId);

    // все бронирования конкретного пользователя
    List<Booking> findAllByBooker(User booker);

    // проверка, бронировал ли пользователь вещь и аренда уже закончилась
    boolean existsByItem_IdAndBooker_IdAndEndBefore(Long itemId,
                                                    Long bookerId,
                                                    LocalDateTime end);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items,
                                                          BookingStatus status);

    List<Booking> findAllByItemInAndStatusOrderByStartDesc(List<Item> items,
                                                           BookingStatus status);

    List<Booking> findAllByItem(Item item,
                                BookingStatus status,
                                LocalDateTime date);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User user,
                                                                            LocalDateTime currentDate,
                                                                            LocalDateTime currentDate1);

    List<Booking> findByBookerAndEndIsBeforeOrderByStartDesc(User user,
                                                             LocalDateTime currentDate);

    List<Booking> findByBookerAndStartIsAfterOrderByStartDesc(User user,
                                                              LocalDateTime currentDate);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(User booker,
                                                            LocalDateTime currentDate,
                                                            LocalDateTime currentDate1);

    List<Booking> findByBookerAndEndIsBefore(User booker,
                                             LocalDateTime currentDate);

    List<Booking> findByBookerAndStartIsAfter(User booker,
                                              LocalDateTime currentDate);

    List<Booking> findByBookerAndStatus(User user,
                                        BookingStatus status);

    List<Booking> findByBooker_IdAndStatus(Long bookerId,
                                           BookingStatus status);

    List<Booking> findByItemInAndStatus(List<Item> items,
                                        BookingStatus status);

    List<Booking> findByItemAndStartIsBeforeAndEndIsAfter(Item item,
                                                          LocalDateTime currentDate,
                                                          LocalDateTime currentDate1);

    List<Booking> findByItemAndEndIsBefore(Item item,
                                           LocalDateTime currentDate);

    List<Booking> findByItemAndStartIsAfter(Item item,
                                            LocalDateTime currentDate);

    List<Booking> findByItemAndStatus(Item item,
                                      BookingStatus status);

    // все бронирования по вещам конкретного владельца со статусом "в прошлом"
    List<Booking> findByItem_Owner_IdAndEndBefore(Long ownerId,
                                                  LocalDateTime end);

    // все будущие бронирования по вещам владельца
    List<Booking> findByItem_Owner_IdAndStartAfter(Long ownerId,
                                                   LocalDateTime start);

    // текущие бронирования по вещам владельца
    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId,
                                                               LocalDateTime start,
                                                               LocalDateTime end);

    // бронирования по вещам владельца с определённым статусом
    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId,
                                               BookingStatus status);

    // для списка id вещей (используется при просмотрах вещей владельца)
    List<Booking> findAllByItem_IdInAndStatusAndStartLessThanEqualOrderByStartDesc(List<Long> idList,
                                                                                   BookingStatus status,
                                                                                   LocalDateTime now);

    List<Booking> findAllByItem_IdInAndStatusAndStartAfterOrderByStartDesc(List<Long> idList,
                                                                           BookingStatus status,
                                                                           LocalDateTime now);

    // last booking
    Optional<Booking> findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId,
                                                                   LocalDateTime date);

    // next booking
    Optional<Booking> findFirstByItem_IdAndStartAfterOrderByStart(Long itemId,
                                                                  LocalDateTime date);
}

