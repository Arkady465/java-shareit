package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime now1,
            LocalDateTime now2
    );

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            Long bookerId,
            LocalDateTime now
    );

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime now
    );

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Long bookerId,
            BookingStatus status
    );

    @Query("select b from Booking b where b.item.owner.id = :ownerId order by b.start desc")
    List<Booking> findAllByOwner(Long ownerId);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.start < :now and b.end > :now order by b.start desc")
    List<Booking> findCurrentByOwner(Long ownerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.end < :now order by b.start desc")
    List<Booking> findPastByOwner(Long ownerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.start > :now order by b.start desc")
    List<Booking> findFutureByOwner(Long ownerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.status = :status order by b.start desc")
    List<Booking> findByOwnerAndStatus(Long ownerId, BookingStatus status);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    @Query("""
           select (count(b) > 0) from Booking b
           where b.item.id = :itemId
             and b.booker.id = :userId
             and b.status = 'APPROVED'
             and b.end < :now
           """)
    boolean userHasFinishedApprovedBooking(Long itemId, Long userId, LocalDateTime now);
}
