package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService
{
    private final InMemoryUserStorage storage = new InMemoryUserStorage();

    @Override
    public UserDto create(UserDto dto)
    {
        if (!StringUtils.hasText(dto.getEmail()))
        {
            throw new IllegalArgumentException("Email is required");
        }
        if (storage.emailExists(dto.getEmail(), null))
        {
            throw new IllegalArgumentException("Email already in use");
        }
        User saved = storage.save(UserMapper.fromDto(dto));
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long userId, UserDto patchDto)
    {
        if (patchDto.getEmail() != null && storage.emailExists(patchDto.getEmail(), userId))
        {
            throw new IllegalArgumentException("Email already in use");
        }
        User patched = storage.updatePartial(userId, UserMapper.fromDto(patchDto));
        if (patched == null)
        {
            throw new NoSuchElementException("User not found: " + userId);
        }
        return UserMapper.toDto(patched);
    }

    @Override
    public UserDto get(Long id)
    {
        return storage.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    @Override
    public List<UserDto> getAll()
    {
        return storage.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id)
    {
        storage.delete(id);
    }
}
