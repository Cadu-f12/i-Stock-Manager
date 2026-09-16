package com.istock.system.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "pastilles")
public class Pastilles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manufacturer_id",
            nullable = false
    )
    private Manufacturer manufacturer;

    @Column(name = "sku_code", nullable = false, unique = true, length = 30)
    private String skuCode;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel;

    @Column(name = "current_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal  currentBalance = BigDecimal.ZERO;

    @ManyToMany
    @JoinTable(
            name = "pastilles_supplier",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<Supplier> suppliers;

    public Pastilles() {}

    public Pastilles(
            Long id,
            Manufacturer manufacturer,
            String skuCode,
            String description,
            Integer minimumStockLevel,
            BigDecimal currentBalance
    ) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.skuCode = skuCode;
        this.description = description;
        this.minimumStockLevel = minimumStockLevel;
        this.currentBalance = currentBalance;
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

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }
}
