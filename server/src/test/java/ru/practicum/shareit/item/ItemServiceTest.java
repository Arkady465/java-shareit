package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    private User owner;
    private User booker;
    private User user;
    private Item item1;

    @BeforeEach
    void setup() {
        owner = createAndSaveUser("OwnerTest", "OwnerTest@test.com");
        booker = createAndSaveUser("BookerTest", "BookerTest@test.com");
        user = createAndSaveUser("UserTest", "UserTest@test.com");
        item1 = createAndSaveItem("ItemTest 1", "DescriptionTest 1", owner, true);
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
    void getOwnersItem() {
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item1");
        itemDto1.setDescription("Description1");
        itemDto1.setAvailable(true);
        itemService.addItem(owner.getId(), itemDto1);

        List<ItemDto> items = itemService.getItemsByOwner(owner.getId());

        assertEquals(2, items.size());
    }

    @Test
    void searchingItems() {
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item1");
        itemDto1.setDescription("Description1");
        itemDto1.setAvailable(true);
        itemService.addItem(owner.getId(), itemDto1);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Item2");
        itemDto2.setDescription("Description2");
        itemDto2.setAvailable(true);
        itemService.addItem(owner.getId(), itemDto2);

        List<ItemDto> items = itemService.searchItems("item1");

        assertEquals(1, items.size());
        assertTrue(items.getFirst().getName().toLowerCase().contains("item1") ||
                items.getFirst().getDescription().toLowerCase().contains("item1"));
    }

    @Test
    void getItemById() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));

        ItemDto itemDto = itemService.getItemById(user.getId(), item1.getId());

        assertNotNull(itemDto);
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void searchingItemsWithBlankText() {
        List<ItemDto> items = itemService.searchItems(" ");

        assertTrue(items.isEmpty());
    }

    @Test
    void addComment() {
        createAndSaveBooking(
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                item1, booker, BookingStatus.APPROVED
        );

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Text");

        CommentDto commentDto1 = itemService.addComment(item1.getId(), commentDto, booker.getId());

        assertNotNull(commentDto1);
        assertEquals("Text", commentDto1.getText());
    }

    @Test
    void createItemWithInvalidData() {
        ItemDto itemDto = new ItemDto();

        assertThrows(NotFoundException.class,
                () -> itemService.addItem(999L, itemDto));
    }

    @Test
    void getItemByIdForOwner() {
        User booker1 = createAndSaveUser("Booker1", "Booker1@email.com");
        User booker2 = createAndSaveUser("Booker2", "Booker2@email.com");

        Booking pastBooking = createAndSaveBooking(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().minusHours(1),
                item1, booker1, BookingStatus.APPROVED
        );

        Booking futureBooking = createAndSaveBooking(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1, booker2, BookingStatus.APPROVED
        );

        ItemDto itemDto = itemService.getItemById(owner.getId(), item1.getId());

        assertNotNull(itemDto.getLastBooking());
        assertEquals(pastBooking.getId(), itemDto.getLastBooking().getId());
        assertEquals(booker1.getId(), itemDto.getLastBooking().getBooker().getId());

        assertNotNull(itemDto.getNextBooking());
        assertEquals(futureBooking.getId(), itemDto.getNextBooking().getId());
        assertEquals(booker2.getId(), itemDto.getNextBooking().getBooker().getId());
    }
}