package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.add(userId, itemRequestDto);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long itemRequestId) {
        return itemRequestService.getById(itemRequestId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequests(userId);
    }
}
