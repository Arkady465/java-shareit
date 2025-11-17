package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;

    @NotBlank
    private String description;

    private Long requestorId;

    private LocalDateTime created;
}
