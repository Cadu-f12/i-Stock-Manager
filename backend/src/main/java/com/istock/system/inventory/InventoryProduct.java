package com.istock.system.inventory;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_product")
public class InventoryProduct {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Version Long version;
    @Column(nullable = false, length = 120) String name;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id")
    InventoryCategory category;
    @Column(nullable = false) int quantity;
    @Column(nullable = false) LocalDate updatedAt;
    @ElementCollection
    @CollectionTable(name = "inventory_product_history", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "entry_order")
    List<History> history = new ArrayList<>();

    @Embeddable
    public static class History {
        @Column(nullable = false, length = 120) String author;
        Long userId;
        @Column(nullable = false) Instant at;
        @Column(nullable = false, length = 2000) String description;
        public History() {}
        History(String author, Long userId, String description) {
            this.author = author; this.userId = userId; this.description = description; this.at = Instant.now();
        }
    }
}
