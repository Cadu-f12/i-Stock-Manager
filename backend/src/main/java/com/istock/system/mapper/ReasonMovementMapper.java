package com.istock.system.mapper;

import com.istock.system.dtos.request.CreateReasonMovementRequest;
import com.istock.system.dtos.request.UpdateReasonMovementRequest;
import com.istock.system.dtos.response.MovementReasonDTO;
import com.istock.system.dtos.response.ReasonMovementDTO;
import com.istock.system.entities.ReasonMovement;
import org.springframework.stereotype.Component;

@Component
public class ReasonMovementMapper {

    public ReasonMovement toEntity(CreateReasonMovementRequest request) {
        return new ReasonMovement(true, request.type(), request.description());
    }

    public void updateEntity(ReasonMovement reasonMovement, UpdateReasonMovementRequest request) {
        reasonMovement.setDescription(request.description());
        reasonMovement.setActive(request.isActive());
    }

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
