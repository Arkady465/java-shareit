package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookItemRequestDto {

    @NotNull
    Long itemId;

    @NotNull
    @Future
    LocalDateTime start;

    @NotNull
    @Future
    LocalDateTime end;
}
