package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;

    private List<CommentDto> comments;

    public ItemDto() {
    }

    public ItemDto(Long id,
                   String name,
                   String description,
                   Boolean available,
                   BookingShortDto lastBooking,
                   BookingShortDto nextBooking,
                   List<CommentDto> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public BookingShortDto getLastBooking() {
        return lastBooking;
    }

    public void setLastBooking(BookingShortDto lastBooking) {
        this.lastBooking = lastBooking;
    }

    public BookingShortDto getNextBooking() {
        return nextBooking;
    }

    public void setNextBooking(BookingShortDto nextBooking) {
        this.nextBooking = nextBooking;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }
}


