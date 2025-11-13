package ru.practicum.shareit.booking;

/** Статус бронирования в БД. */
public enum BookingStatus {
    WAITING,    // ожидает подтверждения владельцем
    APPROVED,   // подтверждено
    REJECTED,   // отклонено
    CANCELED    // отменено (на будущее)
}
