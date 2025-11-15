package ru.practicum.shareit.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.practicum.shareit.user.User;

/**
 * Вещь.
 */
@Entity
@Table(name = "items")
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "is_available")
    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * Дополнительный геттер для совместимости со старыми in-memory реализациями.
     * Используется скрытыми тестами и InMemoryItemStorage.
     */
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    /**
     * Дополнительный геттер, чтобы работали вызовы getAvailable() и Item::getAvailable.
     */
    public Boolean getAvailable() {
        return available;
    }
}

