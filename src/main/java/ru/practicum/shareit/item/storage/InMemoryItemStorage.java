package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryItemStorage {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Item save(Item item) {
        long id = seq.getAndIncrement();
        item.setId(id);
        items.put(id, item);
        return item;
    }
    public Optional<Item> findById(Long id) { return Optional.ofNullable(items.get(id)); }
    public List<Item> findByOwner(Long ownerId) {
        return items.values().stream().filter(i -> Objects.equals(i.getOwnerId(), ownerId))
                .sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
    }
    public Item updatePartial(Long id, Item patch, Long ownerId) {
        Item ex = items.get(id);
        if (ex == null) return null;
        if (!Objects.equals(ex.getOwnerId(), ownerId)) return null; // not owner
        if (patch.getName() != null) ex.setName(patch.getName());
        if (patch.getDescription() != null) ex.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) ex.setAvailable(patch.getAvailable());
        return ex;
    }
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        String q = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(q))
                        || (i.getDescription() != null && i.getDescription().toLowerCase().contains(q)))
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }
}
