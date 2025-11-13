package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Все вещи конкретного владельца
    List<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    // Поиск доступных вещей по тексту в имени/описании
    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "   OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> searchAvailable(@Param("text") String text);
}
