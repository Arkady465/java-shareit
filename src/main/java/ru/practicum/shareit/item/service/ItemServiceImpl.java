package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage storage = new InMemoryItemStorage();

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        Item saved = storage.save(ItemMapper.fromDto(dto, ownerId));
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        Item patched = storage.updatePartial(itemId, ItemMapper.fromDto(patch, ownerId), ownerId);
        if (patched == null) throw new NoSuchElementException("Item not found or not owned by user");
        return ItemMapper.toDto(patched);
    }

    @Override
    public ItemDto get(Long requesterId, Long itemId) {
        return storage.findById(itemId).map(ItemMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        return storage.findByOwner(ownerId).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return storage.search(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }
}
