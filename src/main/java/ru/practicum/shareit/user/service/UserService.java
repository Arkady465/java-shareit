package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    UserDto findById(Long userId);

    UserDto create(User user);

    UserDto update(User user);

    void delete(Long userId);
}
