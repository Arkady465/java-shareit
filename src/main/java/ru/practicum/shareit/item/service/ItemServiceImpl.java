package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto.BookingShortDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final CommentRepository commentRepo;
    private final BookingRepository bookingRepo;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto dto) {
        User owner = userRepo.findById(ownerId).orElseThrow();
        Item item = ItemMapper.fromDto(dto, owner);
        itemRepo.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemWithBookingsDto getById(Long requesterId, Long itemId) {
        Item item = itemRepo.findById(itemId).orElseThrow();
        return toItemWithBookingsDto(item, requesterId);
    }

    @Override
    public List<ItemWithBookingsDto> getOwnerItems(Long ownerId) {
        User owner = userRepo.findById(ownerId).orElseThrow();
        List<Item> items = itemRepo.findByOwnerOrderByIdAsc(owner);
        return items.stream()
                .map(i -> toItemWithBookingsDto(i, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> found = itemRepo
                .findByAvailableTrueAndNameIgnoreCaseContainingOrAvailableTrueAndDescriptionIgnoreCaseContaining(text, text);
        return found.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto) {
        Item item = itemRepo.findById(itemId).orElseThrow();
        User author = userRepo.findById(userId).orElseThrow();

        boolean allowed = bookingRepo.userHasFinishedApprovedBooking(itemId, userId, LocalDateTime.now());
        if (!allowed) {
            throw new SecurityException("User has no finished approved booking for this item");
        }

        Comment c = new Comment();
        c.setText(dto.getText());
        c.setItem(item);
        c.setAuthor(author);
        c.setCreated(LocalDateTime.now());
        commentRepo.save(c);
        return CommentMapper.toDto(c);
    }

    private ItemWithBookingsDto toItemWithBookingsDto(Item item, Long requesterId) {
        ItemWithBookingsDto dto = ItemMapper.toWithBookingsDto(item);

        // Комментарии
        dto.setComments(
                commentRepo.findByItemIdOrderByCreatedDesc(item.getId())
                        .stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList())
        );

        // last/next — показываем владельцу
        if (item.getOwner().getId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();

            var last = bookingRepo.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), now);
            if (last != null) {
                BookingShortDto lb = new BookingShortDto();
                lb.setId(last.getId());
                lb.setBookerId(last.getBooker().getId());
                dto.setLastBooking(lb);
            }

            var next = bookingRepo.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), now);
            if (next != null) {
                BookingShortDto nb = new BookingShortDto();
                nb.setId(next.getId());
                nb.setBookerId(next.getBooker().getId());
                dto.setNextBooking(nb);
            }
        }

        return dto;
    }
}



