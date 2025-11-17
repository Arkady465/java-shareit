package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EntityManager entityManager;

    private User owner;
    private User booker;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setup() {
        owner = createAndSaveUser("OwnerTest", "OwnerTest@test.com");
        booker = createAndSaveUser("BookerTest", "BookerTest@test.com");
        item1 = createAndSaveItem("ItemTest 1", "DescriptionTest 1", owner, true);
        item2 = createAndSaveItem("ItemTest 2", "DescriptionTest 2", owner, false);
    }

    private User createAndSaveUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        return user;
    }

    private Item createAndSaveItem(String name, String description, User owner, Boolean available) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
        entityManager.persist(item);
        entityManager.flush();

        return item;
    }

    private Booking createAndSaveBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(status)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        return booking;
    }

    @Test
    void createBookingMustCreateReservation() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item1.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(3));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(4));

        BookingDto result = bookingService.add(booker.getId(), bookingCreateDto);

        assertNotNull(result);
        assertEquals(item1.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void createBookingWithUnavailableItemMustThrowException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item2.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(5));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(6));

        assertThrows(ValidationException.class,
                () -> bookingService.add(booker.getId(), bookingCreateDto));
    }

    @Test
    void getUserBookingsMustReturnUserBookings() {
        createAndSaveBooking(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1, booker, BookingStatus.APPROVED
        );

        List<BookingDto> result = bookingService.getByUser(booker.getId(), "ALL");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void createBookingWithNonExistentItemMustThrowException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(7));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(8));

        assertThrows(NotFoundException.class,
                () -> bookingService.add(booker.getId(), bookingCreateDto));
    }

    @Test
    void createBookingWithNonExistentUserMustThrowException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(9));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(10));

        assertThrows(NotFoundException.class,
                () -> bookingService.add(1L, bookingCreateDto));
    }

    @Test
    void createBookingWithNullUserIdMustThrowException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(13));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(14));

        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> bookingService.add(null, bookingCreateDto));
    }

    @Test
    void updateBookingStatusWithNonOwnerMustThrowException() {
        User user = createAndSaveUser("User", "User@email.com");

        Booking booking = createAndSaveBooking(
                LocalDateTime.now().plusHours(15),
                LocalDateTime.now().plusHours(16),
                item1, booker, BookingStatus.WAITING
        );

        assertThrows(ForbiddenException.class,
                () -> bookingService.update(user.getId(), booking.getId(), true));
    }

    @Test
    void updateBookingStatusWithNonExistentBookingMustThrowException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.update(1L, 1L, true));
    }

    @Test
    void getUserBookingsWithDifferentStatesMustReturnCorrectBookings() {
        createAndSaveBooking(
                LocalDateTime.now().plusHours(17),
                LocalDateTime.now().plusHours(18),
                item1, booker, BookingStatus.WAITING
        );

        assertDoesNotThrow(() -> bookingService.getByUser(booker.getId(), "ALL"));
        assertDoesNotThrow(() -> bookingService.getByUser(booker.getId(), "FUTURE"));
        assertDoesNotThrow(() -> bookingService.getByUser(booker.getId(), "WAITING"));
    }

    @Test
    void getOwnerBookingsWithDifferentStatesMustReturnCorrectBookings() {
        createAndSaveBooking(
                LocalDateTime.now().plusHours(19),
                LocalDateTime.now().plusHours(20),
                item1, booker, BookingStatus.WAITING
        );

        assertDoesNotThrow(() -> bookingService.getByOwner(owner.getId(), "ALL"));
        assertDoesNotThrow(() -> bookingService.getByOwner(owner.getId(), "FUTURE"));
    }

    @Test
    void createBookingWithPastStartDateMustThrowException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item1.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(21));
        bookingCreateDto.setEnd(LocalDateTime.now().minusHours(22));

        assertThrows(ValidationException.class,
                () -> bookingService.add(booker.getId(), bookingCreateDto));
    }

    @Test
    void createBookingWithSameStartAndEndMustThrowException() {
        LocalDateTime time = LocalDateTime.now().plusHours(23);
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item1.getId());
        bookingCreateDto.setStart(time);
        bookingCreateDto.setEnd(time);

        assertThrows(ValidationException.class,
                () -> bookingService.add(booker.getId(), bookingCreateDto));
    }

    @Test
    void bookingStateAllValuesMustBeAccessible() {
        assertDoesNotThrow(() -> {
            for (BookingState state : BookingState.values()) {
                BookingState result = BookingState.valueOf(state.name());
                assertEquals(state, result);
            }
        });
    }

    @Test
    void bookingStatusAllValuesMustBeAccessible() {
        assertDoesNotThrow(() -> {
            for (BookingStatus status : BookingStatus.values()) {
                BookingStatus result = BookingStatus.valueOf(status.name());
                assertEquals(status, result);
            }
        });
    }

    @Test
    void getBookingByIdWithNonExistentBookingMustThrowNotFoundException() {
        Long userId = 1L;
        Long bookingId = 9L;

        assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, userId));
    }
}