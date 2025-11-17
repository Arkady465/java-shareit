package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.getItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemClient.addComment(itemId, commentDto, authorId);
    }
}
