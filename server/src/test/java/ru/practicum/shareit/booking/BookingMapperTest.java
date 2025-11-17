package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingMapperTest {

    User user = new User(1L, "test1@test.com", "test1");

    ItemRequest itemRequest = new ItemRequest(1L, "testDescription1",
            user, LocalDateTime.of(2024, 1, 13, 9, 19));

    Item item = new Item(1L, "TestName1", "Test description1", true, user, itemRequest);
    LocalDateTime startTime = LocalDateTime.of(2024, 1, 14, 1, 29);
    LocalDateTime endTime = LocalDateTime.of(2024, 1, 18, 2, 45);

    @Test
    public void bookingDtoToBooking() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();

        bookingCreateDto.setStart(startTime);
        bookingCreateDto.setEnd(endTime);

        Booking booking = BookingMapper.mapToBooking(bookingCreateDto);

        assertAll(() -> {
            assertEquals(booking.getStart(), bookingCreateDto.getStart());
            assertEquals(booking.getEnd(), bookingCreateDto.getEnd());
        });
    }

    @Test
    public void bookingToBookingDto() {
        Booking booking = new Booking(3L, startTime, endTime, item, user, BookingStatus.APPROVED);

        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        UserDto userDto = UserMapper.mapToUserDto(user);

        assertAll(() -> {
            assertEquals(booking.getId(), bookingDto.getId());
            assertEquals(booking.getStart(), bookingDto.getStart());
            assertEquals(booking.getEnd(), bookingDto.getEnd());
            assertEquals(booking.getStatus(), bookingDto.getStatus());
            assertEquals(itemDto.getId(), bookingDto.getItem().getId());
            assertEquals(userDto, bookingDto.getBooker());

        });
    }
}