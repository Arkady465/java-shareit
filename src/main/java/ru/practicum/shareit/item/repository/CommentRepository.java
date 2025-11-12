package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "SELECT * FROM comments c " +
            "WHERE c.item_id = :itemId " +
            "ORDER BY c.created DESC",
            nativeQuery = true)
    Collection<Comment> findAllByItemId(@Param("itemId") Long itemId);
}
