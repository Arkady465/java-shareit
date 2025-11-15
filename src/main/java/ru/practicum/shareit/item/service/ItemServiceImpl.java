package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepositoryJpa;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepositoryJpa;
import ru.practicum.shareit.item.dao.ItemRepositoryJpa;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepositoryJpa;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;
    private final CommentRepositoryJpa commentRepository;
    private final BookingRepositoryJpa bookingRepository;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        Item item = ItemMapper.mapToItem(itemDto, owner);
        item = itemRepository.save(item);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        validateUser(ownerId);
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Редактировать вещь может только её владелец");
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

        existingItem = itemRepository.save(existingItem);

        return ItemMapper.mapToItemDto(existingItem);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        List<CommentDto> commentDto = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());

        validateUser(userId);
        ItemDto itemDto = ItemMapper.mapToItemDto(existingItem);
        itemDto.setComments(commentDto);

        if (itemDto.getOwnerId().equals(userId)) {
            Optional<Booking> lastBooking = bookingRepository
                    .findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());

            lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.mapToBookingDto(booking)));

            Optional<Booking> nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterOrderByStart(itemId, LocalDateTime.now());
            nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.mapToBookingDto(booking)));
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        validateUser(ownerId);

        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(item -> {
                    List<CommentDto> commentDto = commentRepository.findAllByItemId(item.getId())
                            .stream()
                            .map(CommentMapper::mapToCommentDto)
                            .collect(Collectors.toList());

                    ItemDto itemDto = ItemMapper.mapToItemDto(item);
                    itemDto.setComments(commentDto);

                    Optional<Booking> lastBooking = bookingRepository
                            .findFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
                    lastBooking.ifPresent(
                            booking -> itemDto.setLastBooking(BookingMapper.mapToBookingDto(booking))
                    );

                    Optional<Booking> nextBooking = bookingRepository
                            .findFirstByItemIdAndStartAfterOrderByStart(item.getId(), LocalDateTime.now());
                    nextBooking.ifPresent(
                            booking -> itemDto.setNextBooking(BookingMapper.mapToBookingDto(booking))
                    );

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + authorId + " не найден"));

        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                itemId, authorId, LocalDateTime.now());

        if (!hasBooked) {
            throw new ValidationException("Пользователь с id = " + authorId + " не брал вещь с id = " + itemId + "в аренду");
        }

        Comment comment = CommentMapper.mapToComment(commentDto, item, user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    private void validateItem(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    private void validateUser(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
    }
}
