package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemMapperTest {

    User user = new User(1L, "test1@test.com", "test1");

    ItemRequest itemRequest = new ItemRequest(1L, "testDescription1",
            user, LocalDateTime.of(2024, 1, 13, 9, 19));

    Item item = new Item(1L, "TestName1", "Test description1", true, user, itemRequest);

    @Test
    public void itemDtoToItem() {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(1L);
        itemDto.setName("TestName");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(1L);
        itemDto.setRequestId(1L);

        Item item = ItemMapper.mapToItem(itemDto, user);

        assertAll(() -> {
            assertEquals(1L, item.getId());
            assertEquals("TestName", item.getName());
            assertEquals("Test description", item.getDescription());
            assertTrue(item.isAvailable());
            assertEquals(1L, item.getOwner().getId());
        });
    }

    @Test
    public void itemToItemDto() {
        Item item = new Item(2L, "TestName2", "Test description2", true, user, itemRequest);

        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        assertAll(() -> {
            assertEquals(2L, itemDto.getId());
            assertEquals("TestName2", itemDto.getName());
            assertEquals("Test description2", itemDto.getDescription());
            assertEquals(true, itemDto.getAvailable());
            assertEquals(user.getId(), itemDto.getOwnerId());
            assertEquals(itemRequest.getId(), itemDto.getRequestId());
        });
    }
}