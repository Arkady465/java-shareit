package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId,
                          @RequestBody ItemDto dto) {
        return itemService.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto dto) {
        return itemService.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_HEADER) Long userId,
                           @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader(USER_HEADER) Long userId) {
        return itemService.getByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto dto) {
        return itemService.addComment(userId, itemId, dto);
    }
}
