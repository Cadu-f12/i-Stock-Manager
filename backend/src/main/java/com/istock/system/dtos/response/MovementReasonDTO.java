package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovementReasonDTO(
        Long id,
        @JsonProperty("sky_code")
        String skuCode,
        String description
) {}
