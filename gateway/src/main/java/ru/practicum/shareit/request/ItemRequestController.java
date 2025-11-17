package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long itemRequestId) {
        return itemRequestClient.getItemRequestById(itemRequestId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAllRequests(userId);
    }
}
