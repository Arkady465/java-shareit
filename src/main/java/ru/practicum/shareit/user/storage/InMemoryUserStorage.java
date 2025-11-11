package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserStorage {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public User save(User user) {
        long id = seq.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void delete(Long id) {
        users.remove(id);
    }

    public User updatePartial(Long id, User patch) {
        User existing = users.get(id);
        if (existing == null) {
            return null;
        }
        if (patch.getName() != null) {
            existing.setName(patch.getName());
        }
        if (patch.getEmail() != null) {
            existing.setEmail(patch.getEmail());
        }
        return existing;
    }

    public boolean emailExists(String email, Long excludeId) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email)
                        && (excludeId == null || !Objects.equals(excludeId, u.getId())));
    }
}
