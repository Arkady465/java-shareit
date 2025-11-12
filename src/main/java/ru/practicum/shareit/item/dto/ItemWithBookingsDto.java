package ru.practicum.shareit.item.dto;

import java.util.List;
import lombok.Data;

@Data
public class ItemWithBookingsDto extends ItemDto {

    BookingShortDto lastBooking;
    BookingShortDto nextBooking;
    List<CommentDto> comments;

    @Data
    public static class BookingShortDto {
        Long id;
        Long bookerId;
    }
}
