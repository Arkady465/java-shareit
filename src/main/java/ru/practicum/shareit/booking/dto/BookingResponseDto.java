package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public class BookingResponseDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;

    private UserDto booker;
    private ItemDto item;

    public BookingResponseDto(Long id,
                              LocalDateTime start,
                              LocalDateTime end,
                              BookingStatus status,
                              UserDto booker,
                              ItemDto item) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public UserDto getBooker() {
        return booker;
    }

    public ItemDto getItem() {
        return item;
    }
}
