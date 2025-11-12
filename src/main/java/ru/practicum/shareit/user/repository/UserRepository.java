package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT CASE " +
            "WHEN COUNT(*) > 0 THEN TRUE " +
            "ELSE FALSE END " +
            "FROM users u " +
            "WHERE LOWER(u.email) = LOWER(:email)",
            nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}
