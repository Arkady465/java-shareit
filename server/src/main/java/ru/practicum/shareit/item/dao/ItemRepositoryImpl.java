package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(getNextId());
        }

        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.isAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
