package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequest) {
        return ItemRequest.builder()
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequesterId())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequesterId())
                .build();
    }
}
