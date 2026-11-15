package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovementPastilleDTO(
        Long id,
        @JsonProperty("sku_code")
        String skuCode,
        String description
) {}
