package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PastilleDTO(
        Long id,
        @JsonProperty("sku_code")
        String skuCode,
        String description,
        StockDTO stock,
        ManufacturerDTO manufacturer,
        List<SupplierDTO> suppliers,
        List<MovementDTO> movements
) {}
