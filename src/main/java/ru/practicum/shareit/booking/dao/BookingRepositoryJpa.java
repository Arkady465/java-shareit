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

    List<Booking> findAllByItemOwnerId(Long id);

    List<Booking> findAllByBooker(User booker);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime date);

    List<Booking> findAllByItem(Item item);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime date);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStart(Long itemId, LocalDateTime date);
}
