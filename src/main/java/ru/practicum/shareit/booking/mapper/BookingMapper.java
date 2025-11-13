package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {
    public static Booking mapToBooking(BookingCreateDto bookingCreateDto, Item item, User booker) {
        if (bookingCreateDto == null) {
            throw new NotFoundException("BookingCreateDto cannot be null");
        }

        if (item == null) {
            throw new NotFoundException("Item cannot be null");
        }

        if (booker == null) {
            throw new NotFoundException("Booker cannot be null");
        }

        Booking booking = new Booking();
        booking.setId(bookingCreateDto.getId());
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        if (booking == null) {
            throw new NotFoundException("Booking cannot be null");
        }

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        itemDto.setDescription(booking.getItem().getDescription());
        itemDto.setAvailable(booking.getItem().isAvailable());
        itemDto.setOwnerId(booking.getItem().getOwner().getId());
        bookingDto.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(booking.getBooker().getId());
        userDto.setName(booking.getBooker().getName());
        userDto.setEmail(booking.getBooker().getEmail());
        bookingDto.setBooker(userDto);

        return bookingDto;
    }
}
