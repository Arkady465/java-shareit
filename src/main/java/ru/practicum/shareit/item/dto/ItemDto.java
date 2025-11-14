package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;

    BookingShortDto lastBooking;
    BookingShortDto nextBooking;

    List<CommentDto> comments;
}




