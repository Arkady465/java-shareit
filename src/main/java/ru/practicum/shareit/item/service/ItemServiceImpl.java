package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.common.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        // проверяем, что владелец существует
        userService.get(ownerId);

        Item item = ItemMapper.fromDto(dto, ownerId);
        item.setId(null);

        Item saved = itemRepository.save(item);
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        if (!existing.getOwnerId().equals(ownerId)) {
            // скрываем факт существования чужой вещи
            throw new NotFoundException("Item not found: " + itemId);
        }

        if (patch.getName() != null) {
            existing.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            existing.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            existing.setAvailable(patch.getAvailable());
        }

        Item updated = itemRepository.save(existing);
        return ItemMapper.toDto(updated);
    }

    @Override
    public ItemDto get(Long requesterId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        ItemDto dto = ItemMapper.toDto(item);

        // комментарии
        var comments = commentRepository.findByItemIdOrderByCreatedAsc(itemId).stream()
                .map(CommentMapper::toDto)
                .toList();
        dto.setComments(comments);

        // last/next бронирования только для владельца
        if (item.getOwnerId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();
            var last = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                    itemId, BookingStatus.APPROVED, now);
            var next = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                    itemId, BookingStatus.APPROVED, now);

            dto.setLastBooking(ItemMapper.toShortBooking(last));
            dto.setNextBooking(ItemMapper.toShortBooking(next));
        }

        return dto;
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();

        return itemRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toDto(item);

                    // last/next booking
                    var last = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                            item.getId(), BookingStatus.APPROVED, now);
                    var next = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), BookingStatus.APPROVED, now);

                    dto.setLastBooking(ItemMapper.toShortBooking(last));
                    dto.setNextBooking(ItemMapper.toShortBooking(next));

                    // комментарии
                    var comments = commentRepository.findByItemIdOrderByCreatedAsc(item.getId()).stream()
                            .map(CommentMapper::toDto)
                            .toList();
                    dto.setComments(comments);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailable(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, String text) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        LocalDateTime now = LocalDateTime.now();

        boolean hasPastBooking = bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                .anyMatch(b -> b.getItem().getId().equals(itemId)
                        && b.getEnd().isBefore(now)
                        && b.getStatus() == BookingStatus.APPROVED);

        if (!hasPastBooking) {
            throw new IllegalArgumentException("User has not completed booking of this item");
        }

        Comment comment = new Comment(
                null,
                text,
                item,
                user,
                now
        );

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDto(saved);
    }
}

