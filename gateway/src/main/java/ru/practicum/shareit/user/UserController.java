package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userClient.delete(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return userClient.getById(id);
    }
}