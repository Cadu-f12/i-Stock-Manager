package com.istock.system.dtos.request;

import com.istock.system.entities.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReasonMovementRequest(
        @NotBlank(message = "Description must not be blank.")
        @Size(max = 80, message = "Description must not exceed 80 characters.")
        String description,

        @NotNull(message = "Movement type is required.")
        MovementType type
) {}
