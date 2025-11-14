package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User owner = findUserById(userId);

        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemUpdateDto itemDto, Long userId) {

        Item item = findItemById(itemId);
        checkOwner(item, userId); // проверяем, что пользователь – владелец вещи

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findByIdWithComments(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        checkUserExists(userId);
        return itemMapper.toDto(itemRepository.findByOwnerId(userId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemMapper.toDto(itemRepository.search(text));
    }

    @Override
    public List<ItemForOwnerDto> getItemsByOwner(Long ownerId) {
        checkUserExists(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);

        return items.stream()
                .map(this::mapToItemDtoForOwner)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CreateCommentRequest request) {
        User author = findUserById(userId);

        Item item = findItemById(itemId);

        boolean hasBooked = bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED
        );

        if (!hasBooked) {
            throw new ValidationException("Пользователь не может прокомментировать данную вещь");
        }

        Comment comment = new Comment(
                request.getText(),
                item,
                author,
                LocalDateTime.now()
        );

        Comment savedComment = commentRepository.save(comment);

        item.getComments().add(comment);
        itemRepository.save(item);

        return commentMapper.toDto(savedComment);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void checkOwner(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Пользователь не является владельцем предмета");
        }
    }

    private ItemForOwnerDto mapToItemDtoForOwner(Item item) {
        ItemForOwnerDto itemDtoForOwner = itemMapper.toDtoForOwner(item);

        List<Booking> bookings = bookingRepository.findByItemIdAndStatusInOrderByStartAsc(
                item.getId(), List.of(BookingStatus.APPROVED)
        );

        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .reduce((first, second) -> second)
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .findFirst()
                .orElse(null);

        if (lastBooking != null) {
            itemDtoForOwner.setLastBooking(new BookingShortDto(
                    lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd()
            ));
        }

        if (nextBooking != null) {
            itemDtoForOwner.setNextBooking(new BookingShortDto(
                    nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd()
            ));
        }

        return itemDtoForOwner;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }
}
