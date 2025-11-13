package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,   // lastBooking заполнится в сервисе
                null,   // nextBooking заполнится в сервисе
                Collections.emptyList() // список комментариев добавим в сервисе
        );
    }

    public static Item fromDto(ItemDto dto, Long ownerId) {
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                ownerId
        );
    }

    public static BookingShortDto toShortBooking(Booking b) {
        if (b == null) {
            return null;
        }
        return new BookingShortDto(b.getId(), b.getBooker().getId());
    }
}

