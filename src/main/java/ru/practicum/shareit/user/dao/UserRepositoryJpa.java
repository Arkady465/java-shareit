package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

public interface UserRepositoryJpa extends JpaRepository<User, Long> {
}
