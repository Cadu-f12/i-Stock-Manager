package com.istock.system.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

public record RegisterMovementRequest(
    @NotNull(message = "Pastille ID is required.")
    @Positive(message = "Pastille ID must be greater than zero.")
    Long pastille,
    @Positive(message = "Movement reason ID must be greater than zero.")
    @JsonProperty("reason_movement_id")
    Long reasonMovementId,
    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be greater than zero.")
    Integer quantity,
    @Size(max = 80, message = "Custom reason must not exceed 80 characters.")
    @JsonProperty("custom_reason")
    String customReason
) {
    @AssertTrue(message = "Provide either a movement reason ID or a nonblank custom entry reason.")
    public boolean isReasonSelectionValid() {
        return customReason == null ? reasonMovementId != null : reasonMovementId == null && !customReason.isBlank();
    }
}
