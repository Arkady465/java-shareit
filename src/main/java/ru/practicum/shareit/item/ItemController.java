package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/** REST controller for Item CRUD & search. */
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId, @Valid @RequestBody ItemDto dto) {
        return service.create(userId, dto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentCreateDto dto
    ) {
        ItemController itemService = null;
        return itemService.addComment(userId, itemId, dto);
    }


    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto patch) {
        return service.update(userId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                       @PathVariable Long itemId) {
        return service.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> ownerItems(@RequestHeader(USER_HEADER) Long userId) {
        return service.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        return service.search(text);
    }
}

