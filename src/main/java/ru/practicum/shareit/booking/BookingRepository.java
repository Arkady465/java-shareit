package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and b.start < ?2 and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and b.start > ?2 and b.status = 'APPROVED' " +
            "order by b.start asc")
    List<Booking> findNextBooking(Long itemId, LocalDateTime now);
}
