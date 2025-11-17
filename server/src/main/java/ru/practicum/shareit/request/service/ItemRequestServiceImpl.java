package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepositoryJpa;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepositoryJpa;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepositoryJpa;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepositoryJpa itemRequestRepository;
    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;

    @Override
    public ItemRequestDto add(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, requestor);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto getById(Long itemRequestId) {
        ItemRequest existingItemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с id = " + itemRequestId + " не найден"));

        List<ItemDto> items = itemRepository.findAllByRequestId(itemRequestId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(existingItemRequest);
        itemRequestDto.setItems(items);

        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getByUser(Long userId) {
        validateUser(userId);

        return itemRequestRepository.findByRequestorId(userId)
                .stream()
                .sorted((i1, i2) -> i2.getCreated().compareTo(i1.getCreated()))
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        validateUser(userId);

        return itemRequestRepository.findAll()
                .stream()
                .sorted((i1, i2) -> i2.getCreated().compareTo(i1.getCreated()))
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    private void validateUser(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
    }
}
