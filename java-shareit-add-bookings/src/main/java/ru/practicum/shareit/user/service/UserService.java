package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);

    List<UserDto> getUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserUpdateDto newUserDto);

    void deleteUser(Long id);
}
