package com.istock.system.dtos.response;

import com.istock.system.entities.MovementType;
import com.istock.system.entities.Movement;
import com.istock.system.entities.ReasonMovement;

public record ReasonMovementDTO(
        Long id,
        String description,
        MovementType type
) {
    public static ReasonMovementDTO from(Movement movement) {
        if (movement.getCustomReason() != null) {
            return new ReasonMovementDTO(null, movement.getCustomReason(), MovementType.IN);
        }
        return from(movement.getReasonMovement());
    }

    public static ReasonMovementDTO from(ReasonMovement reason) {
        return new ReasonMovementDTO(reason.getId(), reason.getDescription(), reason.getType());
    }
}
