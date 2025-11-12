package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER = "X-Sharer-User-Id";
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemFullDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId) {
        return itemService.getItemsByOwner(ownerId);

    }

    @GetMapping("/search")
    public Collection<ItemFullDto> getItemsBySearchQuery(@RequestParam String text) {
        return itemService.getItemsBySearchQuery(text);
    }

    @GetMapping("/{itemId}")
    public ItemFullDto getItemById(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        return itemService.getItemById(itemId, ownerId);
    }

    @PostMapping
    public ItemShortDto create(@Valid @RequestBody ItemShortDto itemShortDto, @RequestHeader(OWNER) Long ownerId) {
        return itemService.create(itemShortDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto update(@RequestBody ItemShortDto itemShortDto, @PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        return itemService.update(itemShortDto, itemId, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        itemService.delete(itemId, ownerId);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@Valid @RequestBody CommentRequestDto commentRequestDto, @RequestHeader(OWNER) Long userId, @PathVariable Long itemId) {
        return itemService.create(commentRequestDto, itemId, userId);
    }

}
