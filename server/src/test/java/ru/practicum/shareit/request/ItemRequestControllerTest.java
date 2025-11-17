package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");
        ItemRequestDto savedRequest = new ItemRequestDto();
        savedRequest.setId(1L);
        savedRequest.setDescription("Description");
        savedRequest.setRequestorId(userId);
        savedRequest.setCreated(LocalDateTime.now());

        when(itemRequestService.add(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(savedRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void createRequestWithInvalidUser() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");

        when(itemRequestService.add(anyLong(), any(ItemRequestDto.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestByIdWithNonExistentRequest() throws Exception {
        when(itemRequestService.getById(anyLong()))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mockMvc.perform(get("/requests/{requestId}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestsByUser() throws Exception {
        User user = new User(1L, "5@email.com", "User");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");
        List<ItemRequestDto> requests = List.of(itemRequestDto);

        when(itemRequestService.getByUser(eq(user.getId())))
                .thenReturn(requests);

        mockMvc.perform(get("/requests")
                    .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Description"));
    }

    @Test
    void getRequestById() throws Exception {
        Long requestId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");

        when(itemRequestService.getById(eq(requestId)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllRequests() throws Exception {
        User user = new User(1L, "5@email.com", "User");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");
        List<ItemRequestDto> requests = List.of(itemRequestDto);

        when(itemRequestService.getAllRequests(eq(user.getId())))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Description"));;
    }
}