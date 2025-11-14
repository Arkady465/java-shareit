package ru.practicum.shareit.booking;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State from(String text) {
        try {
            return State.valueOf(text.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + text);
        }
    }
}
