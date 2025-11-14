package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemService.addComment(itemId, commentDto, authorId);
    }
}
