package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

/**
 * DTO-объект вещи Item.
 */
@Data
public class ItemDto {
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String description;

    @NotNull
    private Boolean available;

    private Long ownerId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;
}
