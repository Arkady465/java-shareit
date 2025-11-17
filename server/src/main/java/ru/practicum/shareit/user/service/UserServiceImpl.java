package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepositoryJpa;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryJpa userRepository;

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);

        if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()))) {
            throw new InternalServerException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        user = userRepository.save(user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        boolean isSameEmail = false;
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            isSameEmail = existingUser.getEmail().equals(userDto.getEmail());

            if (!isSameEmail) {
                if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail())
                        && !u.getId().equals(userDto.getId()))) {
                    throw new InternalServerException("Пользователь с email " + userDto.getEmail() + " уже существует");
                }
            }

            existingUser.setEmail(userDto.getEmail());
        }

        existingUser = userRepository.save(existingUser);

        return UserMapper.mapToUserDto(existingUser);
    }

    @Override
    public UserDto getById(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));

        return UserMapper.mapToUserDto(existingUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
