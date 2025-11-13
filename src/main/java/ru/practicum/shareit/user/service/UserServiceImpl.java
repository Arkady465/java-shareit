package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.common.exceptions.ConflictException;
import ru.practicum.shareit.common.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto dto) {
        if (!StringUtils.hasText(dto.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        User user = UserMapper.fromDto(dto);
        // на всякий случай выбиваем id, чтобы не мешал автоинкременту
        user.setId(null);

        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long userId, UserDto patchDto) {
        if (patchDto.getEmail() != null
                && userRepository.existsByEmailAndIdNot(patchDto.getEmail(), userId)) {
            throw new ConflictException("Email already in use");
        }

        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (patchDto.getName() != null) {
            existing.setName(patchDto.getName());
        }
        if (patchDto.getEmail() != null) {
            existing.setEmail(patchDto.getEmail());
        }

        User updated = userRepository.save(existing);
        return UserMapper.toDto(updated);
    }

    @Override
    public UserDto get(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}



