package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findAllByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.startDate <= CURRENT_TIMESTAMP " +
            "AND b.endDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.endDate < CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findPastByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.startDate > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findByBookerIdAndWaitingStatus(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findByBookerIdAndRejectStatus(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.startDate <= CURRENT_TIMESTAMP " +
            "AND b.endDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.endDate < CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.startDate > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findWaitingByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.startDate DESC")
    Collection<Booking> findRejectedByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.endDate < CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED'")
    boolean hasUserCompletedBooking(@Param("itemId") Long itemId,
                                    @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.startDate < CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.endDate DESC")
    Optional<Booking> findLastBooking(@Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.startDate > CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.startDate ASC")
    Optional<Booking> findNextBooking(@Param("itemId") Long itemId);


}
