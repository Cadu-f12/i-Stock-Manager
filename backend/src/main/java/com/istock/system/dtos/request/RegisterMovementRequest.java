package com.istock.system.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterMovementRequest(
    @NotNull(message = "Pastille ID is required.")
    @Positive(message = "Pastille ID must be greater than zero.")
    Long pastille,
    @NotNull(message = "Movement reason ID is required.")
    @Positive(message = "Movement reason ID must be greater than zero.")
    @JsonProperty("reason_movement_id")
    Long reasonMovementId,
    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be greater than zero.")
    Integer quantity
) {}
