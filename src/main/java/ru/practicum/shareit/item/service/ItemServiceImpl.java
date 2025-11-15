package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .requestId(itemDto.getRequestId())
                .build();

        Item savedItem = itemRepository.save(item);
        return toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(item);
        return toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemDto itemDto = toItemDto(item);

        if (item.getOwner().getId().equals(userId)) {
            addBookingInfo(itemDto);
        }

        itemDto.setComments(getCommentsByItemId(itemId));
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        List<Item> items = itemRepository.findByOwnerId(userId, pageable);

        return items.stream()
                .map(this::toItemDto)
                .map(this::addBookingInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(0, 20);
        return itemRepository.searchAvailableItems(text.toLowerCase(), pageable).stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<Booking> userBookings = bookingRepository
                .findByBookerIdAndItemIdAndEndBeforeAndStatus(userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (userBookings.isEmpty()) {
            throw new ValidationException("User didn't book this item");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return toCommentDto(savedComment);
    }

    private ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    private ItemDto addBookingInfo(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> lastBookings = bookingRepository
                .findLastBookingForItem(itemDto.getId(), now, BookingStatus.APPROVED);
        if (!lastBookings.isEmpty()) {
            itemDto.setLastBooking(new ItemDto.BookingInfo(lastBookings.get(0).getId(), lastBookings.get(0).getBooker().getId()));
        }

        List<Booking> nextBookings = bookingRepository
                .findNextBookingForItem(itemDto.getId(), now, BookingStatus.APPROVED);
        if (!nextBookings.isEmpty()) {
            itemDto.setNextBooking(new ItemDto.BookingInfo(nextBookings.get(0).getId(), nextBookings.get(0).getBooker().getId()));
        }

        return itemDto;
    }

    private List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    private CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
