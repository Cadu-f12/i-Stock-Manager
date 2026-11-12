package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PastilleListDTO(
        Long id,
        @JsonProperty("sku_code")
        String skuCode,
        String description,
        @JsonProperty("minimum_stock_level")
        Integer minimumStockLevel,
        @JsonProperty("current_balance")
        Integer currentBalance,
        String manufacturer
) {}
