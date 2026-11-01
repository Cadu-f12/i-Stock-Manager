package com.istock.system.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegistryPastilleRequest(
        @NotBlank(message = "SKU code must not be blank.")
        @JsonProperty("sku_code")
        String skuCode,

        @Size(max = 255, message = "Description must not exceed 255 characters.")
        String description,

        @Positive(message = "Minimum stock level must be greater than zero.")
        @JsonProperty("minimum_stock_level")
        Integer minimumStockLevel,

        @Positive(message = "Manufacturer ID must be greater than zero.")
        @JsonProperty("manufacturer_id")
        Long manufacturerId
) {}
