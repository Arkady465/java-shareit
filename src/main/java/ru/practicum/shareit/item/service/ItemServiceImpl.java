package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage storage = new InMemoryItemStorage();
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        // 404 если владелец не существует
        userService.get(ownerId);
        Item saved = storage.save(ItemMapper.fromDto(dto, ownerId));
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        Item patched = storage.updatePartial(itemId, ItemMapper.fromDto(patch, ownerId), ownerId);
        if (patched == null) {
            // скрываем факт существования вещи, если она чужая или отсутствует
            throw new NotFoundException("Item not found: " + itemId);
        }
        return ItemMapper.toDto(patched);
    }

    @Override
    public ItemDto get(Long requesterId, Long itemId) {
        return storage.findById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        return storage.findByOwner(ownerId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return storage.search(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}


