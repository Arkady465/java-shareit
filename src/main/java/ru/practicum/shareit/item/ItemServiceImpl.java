package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long userId, ItemDto dto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User is not owner");
        }

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) item.setAvailable(dto.getAvailable());

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemDto dto = ItemMapper.toDto(item);

        // комментарии
        dto.setComments(commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList());

        // last/next booking — только если владелец
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            var last = bookingRepository.findLastBooking(itemId, now).stream().findFirst();
            var next = bookingRepository.findNextBooking(itemId, now).stream().findFirst();

            last.ifPresent(b -> dto.setLastBooking(toShort(b)));
            next.ifPresent(b -> dto.setNextBooking(toShort(b)));
        }

        return dto;
    }

    @Override
    public List<ItemDto> getByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);

        LocalDateTime now = LocalDateTime.now();

        return items.stream().map(item -> {
            ItemDto dto = ItemMapper.toDto(item);

            dto.setComments(commentRepository.findByItemIdOrderByCreatedDesc(item.getId())
                    .stream()
                    .map(CommentMapper::toDto)
                    .toList());

            var last = bookingRepository.findLastBooking(item.getId(), now).stream().findFirst();
            var next = bookingRepository.findNextBooking(item.getId(), now).stream().findFirst();

            last.ifPresent(b -> dto.setLastBooking(toShort(b)));
            next.ifPresent(b -> dto.setNextBooking(toShort(b)));

            return dto;
        }).toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        LocalDateTime now = LocalDateTime.now();

        boolean hasBooking = bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                .anyMatch(b -> b.getItem().getId().equals(itemId)
                        && b.getStatus() == BookingStatus.APPROVED
                        && b.getEnd().isBefore(now));

        if (!hasBooking) {
            throw new ValidationException("User has not completed booking");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .author(user)
                .item(item)
                .created(now)
                .build();

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private BookingShortDto toShort(Booking booking) {
        return new BookingShortDto(booking.getId(), booking.getBooker().getId());
    }
}



