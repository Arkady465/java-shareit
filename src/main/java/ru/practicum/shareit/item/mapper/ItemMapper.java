package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemDto toDto(Item model) {
        if (model == null) {
            return null;
        }
        return new ItemDto(model.getId(), model.getName(), model.getDescription(), model.getAvailable());
    }

    public static Item fromDto(ItemDto dto, Long ownerId) {
        if (dto == null) {
            return null;
        }
        return new Item(dto.getId(), dto.getName(), dto.getDescription(), dto.getAvailable(), ownerId);
    }
}


