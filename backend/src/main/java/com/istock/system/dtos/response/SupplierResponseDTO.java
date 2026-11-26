package com.istock.system.dtos.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SupplierResponseDTO(
        Long id,
        String name,
        String contact,
        @JsonProperty("is_active")
        Boolean isActive,
        List<PastilleListDTO> pastilles
) {}
