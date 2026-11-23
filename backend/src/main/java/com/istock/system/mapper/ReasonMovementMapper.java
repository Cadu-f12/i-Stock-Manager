package com.istock.system.mapper;

import com.istock.system.dtos.response.MovementReasonDTO;
import com.istock.system.dtos.response.ReasonMovementDTO;
import com.istock.system.entities.ReasonMovement;
import org.springframework.stereotype.Component;

@Component
public class ReasonMovementMapper {

    public MovementReasonDTO toMovementReasonDTO(ReasonMovement reasonMovement) {
        return new MovementReasonDTO(
                reasonMovement.getId(),
                reasonMovement.getDescription(),
                reasonMovement.getType()
        );
    }

    public ReasonMovementDTO toReasonMovementDTO(ReasonMovement reasonMovement) {
        return new ReasonMovementDTO(
                reasonMovement.getId(),
                reasonMovement.getDescription(),
                reasonMovement.getType()
        );
    }
}
