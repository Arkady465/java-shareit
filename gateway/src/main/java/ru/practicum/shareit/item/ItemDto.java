package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    private Long requestId;
}
