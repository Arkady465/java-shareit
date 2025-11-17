package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        User owner = new User(1L, "1@email.com", "Owner");

        when(itemService.addItem(eq(owner.getId()), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");

        User owner = new User(1L, "2@email.com", "Owner");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        when(itemService.updateItem(eq(owner.getId()), eq(item.getId()), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void getItemById() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        when(itemService.getItemById(eq(userId), eq(itemId)))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
    }

    @Test
    void getItemsByOwner() throws Exception {
        User owner = new User(1L, "3@email.com", "Owner");
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        List<ItemDto> items = List.of(itemDto);

        when(itemService.getItemsByOwner(eq(owner.getId())))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(items.getFirst().getId()));
    }

    @Test
    void searchItems() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        List<ItemDto> items = List.of(itemDto);

        when(itemService.searchItems(eq("Item")))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "Item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(items.getFirst().getId()));
    }

    @Test
    void addComment() throws Exception {
        Long itemId = 1L;
        Long authorId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test");

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(1L);
        commentDto2.setText("Test");
        commentDto2.setAuthorName("AuthorTest");

        when(itemService.addComment(eq(itemId), any(CommentDto.class), eq(authorId)))
                .thenReturn(commentDto2);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto2.getId()))
                .andExpect(jsonPath("$.text").value(commentDto2.getText()));
    }

    @Test
    void getItemsByOwnerEmpty() throws Exception {
        Long userId = 1L;

        when(itemService.getItemsByOwner(eq(userId))).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}