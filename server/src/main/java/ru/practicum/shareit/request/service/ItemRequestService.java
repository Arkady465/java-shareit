package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getById(Long itemRequestId);

    List<ItemRequestDto> getByUser(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);
}
