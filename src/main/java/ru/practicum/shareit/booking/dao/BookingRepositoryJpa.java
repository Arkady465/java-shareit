package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepositoryJpa extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemOwnerId(Long id);

    List<Booking> findAllByItem(Item item);

    List<Booking> findAllByBooker(User booker);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start);

    /**
     * CURRENT-бронирования владельца вещи.
     * Явно задаём путь b.item.owner.id, чтобы не падать на ownerId в Item.
     */
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start < :start " +
            "AND b.end > :end")
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(@Param("ownerId") Long ownerId,
                                                             @Param("start") LocalDateTime start,
                                                             @Param("end") LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime date);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStart(Long itemId, LocalDateTime date);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);
}

