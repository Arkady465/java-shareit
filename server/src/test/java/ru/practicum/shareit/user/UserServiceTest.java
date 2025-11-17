package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepositoryJpa;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepositoryJpa userRepository;

    @Test
    void createUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Test");
        userDto.setEmail("Test@test.com");

        UserDto result = userService.add(userDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test", result.getName());
        assertEquals("Test@test.com", result.getEmail());
    }

    @Test
    void createUserWithSameEmail() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("Test1");
        userDto1.setEmail("Test@test.com");

        userService.add(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("Test2");
        userDto2.setEmail("Test@test.com");

        assertThrows(InternalServerException.class, () -> userService.add(userDto2));
    }

    @Test
    void updateUser() {
        User user = userRepository.save(new User(null, "Test3@test.com", "Test3"));

        UserDto userDto4 = new UserDto();
        userDto4.setName("Test4");
        userDto4.setEmail("Test4@test.com");

        UserDto result = userService.update(user.getId(), userDto4);

        assertEquals("Test4", result.getName());
        assertEquals("Test4@test.com", result.getEmail());
    }

    @Test
    void updateUserWithNonExistentUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Test5");
        userDto.setEmail("Test5@test.com");

        assertThrows(NotFoundException.class,
                () -> userService.update(1L, userDto));
    }

    @Test
    void getUserById() {
        User user = userRepository.save(new User(null, "Test6@test.com", "Test6"));

        UserDto result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("Test6", result.getName());
        assertEquals("Test6@test.com", result.getEmail());
    }

    @Test
    void deleteUser() {
        User user = userRepository.save(new User(null, "Test7@test.com", "Test7"));

        userService.delete(user.getId());

        assertThrows(NotFoundException.class, () -> userService.getById(user.getId()));
    }

    @Test
    void getUserByIdWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> userService.getById(555L));
    }

    @Test
    void getUserByIdWithNull() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userService.getById(null));
    }
}