package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ManufacturerDTO(
       Long id,
       String name,
       String contact,
       @JsonProperty("is_active")
       Boolean isActive
) {}
