package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemDto dto) {
        return service.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto patchDto) {
        return service.update(ownerId, itemId, patchDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @PathVariable Long itemId) {
        return service.get(requesterId, itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text) {
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto.getText());
    }
}
