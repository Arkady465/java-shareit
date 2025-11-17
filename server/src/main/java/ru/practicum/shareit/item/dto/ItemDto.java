package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

/**
 * DTO-объект вещи Item.
 */
@Data
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}
