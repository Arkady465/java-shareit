package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserMapperTest {

    @Test
    public void userDtoToUser() {
        UserDto userDto = new UserDto();

        userDto.setId(1L);
        userDto.setName("Test");
        userDto.setEmail("test1@test.com");

        User user = UserMapper.mapToUser(userDto);

        assertAll(() -> {
            assertEquals(1L, user.getId());
            assertEquals("Test", user.getName());
            assertEquals("test1@test.com", user.getEmail());
        });
    }

    @Test
    public void userToUserDto() {
        User user = new User(1L, "test@test.com", "Test");

        UserDto userDto = UserMapper.mapToUserDto(user);

        assertAll(() -> {
            assertEquals(1L, userDto.getId());
            assertEquals("Test", userDto.getName());
            assertEquals("test@test.com", userDto.getEmail());
        });
    }
}
