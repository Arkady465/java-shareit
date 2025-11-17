package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static Item mapToItem(ItemDto itemDto, User owner) {
        if (itemDto == null) {
            throw new NotFoundException("ItemDto cannot be null");
        }

        if (owner == null) {
            throw new NotFoundException("Owner cannot be null");
        }

        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);

        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        if (item == null) {
            throw new NotFoundException("Item cannot be null");
        }

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setOwnerId(item.getOwner().getId());

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }
}
