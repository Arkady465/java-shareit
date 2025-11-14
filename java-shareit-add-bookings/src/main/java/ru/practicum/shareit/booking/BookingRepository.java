package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId); // GET state=ALL

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status); // GET state=WAITING/REJECTED

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end); // GET state=CURRENT

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end); // GET state=PAST

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start); // GET state=FUTURE

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime end, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId); // GET /owner state=ALL

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            @Param("ownerId") Long ownerId, @Param("status") BookingStatus status); // GET /owner state=WAITING/REJECTED

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.start < :now " +
            "AND b.end > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentlyByItemOwnerIdOrderByStartDesc(
            @Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(
            @Param("ownerId") Long ownerId, @Param("now") LocalDateTime now); // GET /owner state=PAST

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            @Param("ownerId") Long ownerId, @Param("now") LocalDateTime now); // GET /owner state=FUTURE

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start < :end " +
            "AND b.end > :start")
    List<Booking> findOverlappingBookings(
            @Param("itemId") Long itemId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Booking> findByItemIdAndStatusInOrderByStartAsc(
            Long itemId,
            List<BookingStatus> statuses);
}
