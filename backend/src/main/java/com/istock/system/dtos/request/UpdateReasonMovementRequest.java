package com.istock.system.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateReasonMovementRequest(
        @NotBlank(message = "Description must not be blank.")
        @Size(max = 80, message = "Description must not exceed 80 characters.")
        String description,
        @JsonProperty("is_active") @NotNull(message = "Active status is required.") Boolean isActive
) {}
