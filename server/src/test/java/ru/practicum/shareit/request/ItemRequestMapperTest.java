package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemRequestMapperTest {

    User user = new User(1L, "test1@test.com", "test1");

    ItemRequest itemRequest = new ItemRequest(1L, "testDescription1",
            user, LocalDateTime.of(2024, 1, 13, 9, 19));

    Item item = new Item(1L, "TestName1", "Test description1", true, user, itemRequest);

    @Test
    public void itemRequestDtoToItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Test description");
        itemRequestDto.setRequestorId(1L);

        ItemRequest itemRequest1 = ItemRequestMapper.mapToItemRequest(itemRequestDto, user);

        assertAll(() -> {
            assertEquals(1L, itemRequest1.getId());
            assertEquals("Test description", itemRequest1.getDescription());
            assertEquals(1L, itemRequest1.getRequestor().getId());
        });
    }

    @Test
    public void itemRequestToItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest(2L, "Test description2", user, LocalDateTime.now());

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);

        assertAll(() -> {
            assertEquals(2L, itemRequestDto.getId());
            assertEquals("Test description2", itemRequestDto.getDescription());
            assertEquals(1L, itemRequestDto.getRequestorId());
        });
    }
}