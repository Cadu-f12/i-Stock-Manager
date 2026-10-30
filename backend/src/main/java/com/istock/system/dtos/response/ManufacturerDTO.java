package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ManufacturerDTO(
       Long id,
       String name,
       String contact,
       @JsonProperty("is_active")
       Boolean isActive,
        List<PastilleDTO> pastilles
) {}
