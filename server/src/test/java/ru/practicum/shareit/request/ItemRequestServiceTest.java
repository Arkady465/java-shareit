package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepositoryJpa;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepositoryJpa userRepository;

    @Autowired
    private ItemService itemService;

    @Test
    void createRequest() {
        User user = userRepository.save(new User(null, "User1@email.com", "User1"));
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");

        ItemRequestDto result = itemRequestService.add(user.getId(), itemRequestDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Description", result.getDescription());
        assertEquals(user.getId(), result.getRequestorId());
        assertNotNull(result.getCreated());
    }

    @Test
    void createRequestWithNonExistentUser() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");

        assertThrows(NotFoundException.class,
                () -> itemRequestService.add(1L, itemRequestDto));
    }

    @Test
    void getAllRequests() {
        User user = userRepository.save(new User(null, "User2@email.com", "User2"));

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Description1");
        itemRequestService.add(user.getId(), itemRequestDto1);

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("Description2");
        itemRequestService.add(user.getId(), itemRequestDto2);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getDescription().equals("Description1")));
        assertTrue(result.stream().anyMatch(i -> i.getDescription().equals("Description2")));
    }

    @Test
    void getRequestsByUser() {
        User user3 = userRepository.save(new User(null, "User3@email.com", "User3"));
        User user4 = userRepository.save(new User(null, "User4@email.com", "User4"));

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("User3 Description");
        itemRequestService.add(user3.getId(), itemRequestDto1);

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("User4 Description");
        itemRequestService.add(user4.getId(), itemRequestDto2);

        List<ItemRequestDto> result = itemRequestService.getByUser(user3.getId());

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(i -> i.getDescription().equals("User3 Description")));
        assertFalse(result.stream().anyMatch(i -> i.getDescription().equals("User4 Description")));
    }

    @Test
    void getRequestById() {
        User user5 = userRepository.save(new User(null, "User5@email.com", "User5"));
        User user6 = userRepository.save(new User(null, "User6@email.com", "User6"));

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("ItemRequestDescription");
        ItemRequestDto savedRequest = itemRequestService.add(user5.getId(), itemRequestDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("ItemDto");
        itemDto.setDescription("ItemRequestDescription");
        itemDto.setAvailable(true);
        itemDto.setRequestId(savedRequest.getId());
        itemService.addItem(user6.getId(), itemDto);

        ItemRequestDto result = itemRequestService.getById(savedRequest.getId());

        assertNotNull(result);
        assertEquals(savedRequest.getId(), result.getId());
        assertEquals("ItemRequestDescription", result.getDescription());
        assertFalse(result.getItems().isEmpty());
        assertEquals("ItemDto", result.getItems().getFirst().getName());
    }

    @Test
    void getRequestByIdWithNonExistentRequest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L));
    }

    @Test
    void getRequestsWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(1L));
    }
}