package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING, // ожидает подтверждения владельца
    APPROVED, // бронирование подтверждено владельцем
    REJECTED, // бронирование отклонено владельцем
    CANCELED // бронирование отменено создателем
}
