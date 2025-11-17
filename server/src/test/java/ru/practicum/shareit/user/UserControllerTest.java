package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test");
        userDto.setEmail("Test@test.com");

        when(userService.add(any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("Test@test.com"));
    }

    @Test
    void updateUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Test");
        userDto.setEmail("Test@test.com");

        when(userService.update(eq(userId), any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("Test@test.com"));
    }

    @Test
    void getUserById() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test");
        userDto.setEmail("Test@test.com");

        when(userService.getById(eq(userId)))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("Test@test.com"));
    }

    @Test
    void deleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}
