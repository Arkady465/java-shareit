package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Все бронирования пользователя-бронирующего (booker), новые → старые
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    // Все бронирования вещей конкретного владельца
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    // Последнее прошедшее подтверждённое бронирование вещи
    Booking findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            Long itemId,
            BookingStatus status,
            LocalDateTime now
    );

    // Ближайшее будущее подтверждённое бронирование вещи
    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId,
            BookingStatus status,
            LocalDateTime now
    );
}

