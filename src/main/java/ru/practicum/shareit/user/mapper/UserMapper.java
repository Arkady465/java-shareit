package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

/** Maps between User model and UserDto. */
public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User fromDto(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }
}

