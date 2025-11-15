package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);
}