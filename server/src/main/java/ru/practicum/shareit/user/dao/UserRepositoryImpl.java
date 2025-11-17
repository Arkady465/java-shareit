package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user, Boolean isSameEmail) {
        if (user.getId() == null) {
            if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
                throw new InternalServerException("Пользователь с email " + user.getEmail() + " уже существует");
            }

            user.setId(getNextId());
        } else {
            if (user.getEmail() != null && !isSameEmail) {
                if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail())
                        && !u.getId().equals(user.getId()))) {
                    throw new InternalServerException("Пользователь с email " + user.getEmail() + " уже существует");
                }
            }
        }

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id =" + id + " не найден"));
        users.remove(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
