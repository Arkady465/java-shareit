package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {
    public static User mapToUser(UserDto userDto) {
        if (userDto == null) {
            throw new NotFoundException("userDto cannot be null");
        }

        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        return user;
    }

    public static UserDto mapToUserDto(User user) {
        if (user == null) {
            throw new NotFoundException("User cannot be null");
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        return userDto;
    }
}
