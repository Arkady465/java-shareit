package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemIdOrderByCreatedDesc(Long itemId);

    List<Comment> findByItemIdInOrderByCreatedDesc(List<Long> itemIds);

    List<Comment> findAllByItemId(Long itemId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :currentTime")
    boolean existsByBookerAndItemAndEndBefore(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId,
            @Param("currentTime") LocalDateTime currentTime
    );
}
