package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        if (itemRequestDto == null) {
            throw new NotFoundException("ItemRequestDto cannot be null");
        }

        if (requestor == null) {
            throw new NotFoundException("Requestor cannot be null");
        }

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(itemRequestDto.getCreated());

        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new NotFoundException("ItemRequest cannot be null");
        }

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestorId(itemRequest.getRequestor().getId());
        itemRequestDto.setCreated(itemRequest.getCreated());

        return itemRequestDto;
    }
}
