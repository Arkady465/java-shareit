package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService
{
    UserDto create(UserDto dto);

    UserDto update(Long userId, UserDto patchDto);

    UserDto get(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}
