package ru.practicum.shareit.booking;

/**
 * Состояние бронирований, используемое в запросах (параметр state).
 * Это фильтр, а не статус в БД.
 */
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + value);
        }
    }
}
