package com.istock.system.inventory;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_category")
public class InventoryCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @Column(nullable = false, length = 60) String name;
    @Column(nullable = false, unique = true, length = 120) String normalizedName;
}
