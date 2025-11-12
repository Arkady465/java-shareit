package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto findById(Long id) {
        log.info("Запрос на получения пользователя с id = {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", id);
            return new NotFoundException("Пользователь не найден");
        });

        log.info("Пользователь с id = {} успешно найден", id);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto create(User user) {
        log.info("Запрос на создание пользователя");

        try {
            User createdUser = userRepository.save((user));
            log.info("Пользователь с id = {} успешно создан", createdUser.getId());
            return UserMapper.toUserDto(createdUser);
        } catch (DataIntegrityViolationException e) {
            log.warn("Некорректный или уже существующий email {}", user.getEmail());
            throw new ValidationException("Некорректный или уже существующий email. Ошибка: " + e.getLocalizedMessage());
        }
    }

    @Transactional
    @Override
    public UserDto update(User user) {
        log.info("Запрос на обновление данных пользователя с id = {}", user.getId());

        User oldUser = userRepository.findById(user.getId()).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", user.getId());
            return new NotFoundException("Пользователь не найден");
        });

        Optional.ofNullable(user.getName()).ifPresent(oldUser::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(oldUser::setEmail);

        try {
            User updateUser = userRepository.save(oldUser);
            log.info("Пользователь с id = {} успешно обновлен", updateUser.getId());
            return UserMapper.toUserDto(updateUser);
        } catch (DataIntegrityViolationException e) {
            log.warn("Некорректный или уже существующий email {}", user.getEmail());
            throw new ValidationException("Некорректный или уже существующий email. Ошибка: " + e.getLocalizedMessage());
        }
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        userRepository.deleteById(user.getId());
        log.info("Пользователь с id = {} успешно удален", userId);
    }
}
