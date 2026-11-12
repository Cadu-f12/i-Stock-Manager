package com.istock.system.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdatePastilleRequest(
        @NotNull(message = "Description must not be null.")
        @Size(max = 255, message = "Description must not exceed 255 characters.")
        String description,

        @JsonProperty("manufacturer_id")
        @NotNull(message = "Manufacturer ID must not be null.")
        @Positive(message = "Manufacturer ID must be greater than zero.")
        Long manufacturerId,

        @JsonProperty("minimum_stock_level")
        @PositiveOrZero(message = "Minimum stock level must be zero or greater.")
        @NotNull(message = "Minimum stock level must not be null.")
        Integer minimumStockLevel
) {}
