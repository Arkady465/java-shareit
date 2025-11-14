package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto getById(Long id);

    void delete(Long id);
}
