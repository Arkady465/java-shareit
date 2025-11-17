package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user, Boolean isSameEmail);

    Optional<User> findById(Long id);

    void delete(Long id);
}
