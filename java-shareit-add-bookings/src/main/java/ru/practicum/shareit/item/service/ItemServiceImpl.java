package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import ru.practicum.shareit.item.dao.ItemRepositoryJpa;
import ru.practicum.shareit.item.dao.CommentRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.CommentMapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepositoryJpa;

import ru.practicum.shareit.user.User;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;
    private final BookingRepositoryJpa bookingRepository;
    private final CommentRepositoryJpa commentRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = ItemMapper.mapToItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.mapToItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        validateItem(itemId);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        validateItem(itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookingRepository
                .findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, now)
                .orElse(null);

        Booking nextBooking = bookingRepository
                .findFirstByItem_IdAndStartAfterOrderByStart(itemId, now)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findByItem_Id(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();

        return itemRepository.findByOwner_Id(userId)
                .stream()
                .map(item -> {
                    Booking lastBooking = bookingRepository
                            .findFirstByItem_IdAndEndBeforeOrderByEndDesc(item.getId(), now)
                            .orElse(null);
                    Booking nextBooking = bookingRepository
                            .findFirstByItem_IdAndStartAfterOrderByStart(item.getId(), now)
                            .orElse(null);
                    List<CommentDto> comments = commentRepository.findByItem_Id(item.getId())
                            .stream()
                            .map(CommentMapper::mapToCommentDto)
                            .collect(Collectors.toList());
                    return ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text.toLowerCase())
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        validateItem(itemId);

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        LocalDateTime now = LocalDateTime.now();

        boolean hasCompletedBooking = bookingRepository
                .existsByItem_IdAndBooker_IdAndEndBefore(itemId, authorId, now);

        if (!hasCompletedBooking) {
            throw new ValidationException("User has not completed booking for this item");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now);

        Comment saved = commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(saved);
    }

    private void validateItem(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }
}
