package ru.practicum.shareit.item.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerOrderByIdAsc(User owner);

    List<Item> findByAvailableTrueAndNameIgnoreCaseContainingOrAvailableTrueAndDescriptionIgnoreCaseContaining(
            String inName,
            String inDesc
    );
}
