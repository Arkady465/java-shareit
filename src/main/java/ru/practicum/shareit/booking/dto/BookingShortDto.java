package ru.practicum.shareit.booking.dto;

public class BookingShortDto {

    private Long id;
    private Long bookerId;

    public BookingShortDto(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }

    public Long getId() {
        return id;
    }

    public Long getBookerId() {
        return bookerId;
    }
}
