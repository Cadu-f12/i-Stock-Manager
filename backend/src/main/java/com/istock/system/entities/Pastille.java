package com.istock.system.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "pastilles")
public class Pastille {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_code", nullable = false, unique = true, length = 30)
    private String skuCode;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "minimum_stock_level", nullable = false, columnDefinition = "INT CHECK (minimum_stock_level >= 0)")
    private Integer minimumStockLevel;

    @Column(name = "current_balance", nullable = false)
    private Integer  currentBalance = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manufacturer_id",
            nullable = false
    )
    private Manufacturer manufacturer;

    @ManyToMany
    @JoinTable(
            name = "pastille_supplier",
            joinColumns = @JoinColumn(name = "pastille_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<Supplier> suppliers;

    @OneToMany(mappedBy = "pastille", fetch = FetchType.LAZY)
    private List<Movement> movements;

    public Pastille() {}

    public Pastille(
            Long id,
            String skuCode,
            String description,
            Integer minimumStockLevel,
            Integer currentBalance,
            Manufacturer manufacturer,
            List<Supplier> suppliers,
            List<Movement> movements
    ) {
        this.id = id;
        this.skuCode = skuCode;
        this.description = description;
        this.minimumStockLevel = minimumStockLevel;
        this.currentBalance = currentBalance;
        this.manufacturer = manufacturer;
        this.suppliers = suppliers;
        this.movements = movements;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(Integer minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }
}
