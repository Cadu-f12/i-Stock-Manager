package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SupplierDTO(
        Long id,
        String name,
        String contact,
        @JsonProperty("is_active")
        Boolean isActive
) {}
