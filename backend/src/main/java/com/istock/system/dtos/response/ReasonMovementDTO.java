package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.istock.system.entities.MovementType;

import java.util.List;

public record ReasonMovementDTO(
        Long id,
        String description,
        MovementType type,
        @JsonProperty("is_active")
        Boolean isActive,
        List<MovementDTO> movements
) {}
