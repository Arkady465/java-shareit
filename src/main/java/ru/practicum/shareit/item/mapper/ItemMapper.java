package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public final class ItemMapper
{
    private ItemMapper()
    {
    }

    public static ItemDto toDto(Item model)
    {
        if (model == null)
        {
            return null;
        }
        return new ItemDto(model.getId(), model.getName(), model.getDescription(), model.getAvailable());
    }

    public static Item fromDto(ItemDto dto, Long ownerId)
    {
        if (dto == null)
        {
            return null;
        }
        return new Item(dto.getId(), dto.getName(), dto.getDescription(), dto.getAvailable(), ownerId);
    }
}
