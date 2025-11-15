package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepositoryJpa;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.dao.CommentRepositoryJpa;
import ru.practicum.shareit.item.dao.ItemRepositoryJpa;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;
    private final BookingRepositoryJpa bookingRepository;
    private final CommentRepositoryJpa commentRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = validateUser(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);
        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        User owner = validateUser(ownerId);
        Item existing = validateItem(itemId);

        if (!existing.getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            existing.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existing.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existing.setAvailable(itemDto.getAvailable());
        }

        Item saved = itemRepository.save(existing);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        validateUser(userId);
        Item item = validateItem(itemId);

        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookingRepository
                .findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, now)
                .orElse(null);

        Booking nextBooking = bookingRepository
                .findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId, now)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemDtoWithBookings(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        User owner = validateUser(ownerId);

        List<Item> items = itemRepository.findAllByOwner_Id(owner.getId());

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    Booking lastBooking = bookingRepository
                            .findFirstByItem_IdAndEndBeforeOrderByEndDesc(item.getId(), now)
                            .orElse(null);

                    Booking nextBooking = bookingRepository
                            .findFirstByItem_IdAndStartAfterOrderByStartAsc(item.getId(), now)
                            .orElse(null);

                    List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                            .map(CommentMapper::mapToCommentDto)
                            .collect(Collectors.toList());

                    return ItemMapper.toItemDtoWithBookings(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.search(text.toLowerCase());
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, CommentDto commentDto, Long itemId) {
        User author = validateUser(userId);
        Item item = validateItem(itemId);

        // Пользователь должен иметь завершённое бронирование этого предмета
        LocalDateTime now = LocalDateTime.now();
        boolean hasCompletedBooking = bookingRepository
                .existsByItem_IdAndBooker_IdAndEndBefore(itemId, userId, now);

        if (!hasCompletedBooking) {
            throw new ValidationException("User has not completed a booking for this item");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        Comment saved = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(saved);
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }
}
