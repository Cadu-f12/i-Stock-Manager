package com.istock.system.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class PastilhaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pastilha_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manufacturer_id",
            nullable = false
    )
    private List<ManufacturerEntity> manufacturer;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String SKUCode;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal  currentBalance;

    public PastilhaEntity() {}

    public PastilhaEntity(
            Long pastilha_id,
            List<ManufacturerEntity> manufacturer,
            String SKUCode,
            String description,
            Integer minimumStockLevel,
            BigDecimal currentBalance
    ) {
        this.pastilha_id = pastilha_id;
        this.manufacturer = manufacturer;
        this.SKUCode = SKUCode;
        this.description = description;
        this.minimumStockLevel = minimumStockLevel;
        this.currentBalance = currentBalance;
    }

    public Long getPastilha_id() {
        return pastilha_id;
    }

    public void setPastilha_id(Long pastilha_id) {
        this.pastilha_id = pastilha_id;
    }

    public List<ManufacturerEntity> getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(List<ManufacturerEntity> manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSKUCode() {
        return SKUCode;
    }

    public void setSKUCode(String SKUCode) {
        this.SKUCode = SKUCode;
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
}
